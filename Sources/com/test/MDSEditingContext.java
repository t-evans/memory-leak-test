package com.test;

import org.apache.log4j.Logger;

import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EOAdaptorOperation;
import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eoaccess.EODatabaseOperation;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOObjectStore;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXObjectStoreCoordinatorPool;

public class MDSEditingContext extends ERXEC {

	Logger _logger = Logger.getLogger( MDSEditingContext.class );

    // methods for resolving lock failures
    public static final int LAST_WRITE_WINS = 0;
    public static final int REFAULT = 1;

    private int _lockConflictResolutionMethod = LAST_WRITE_WINS;

	private static final int _MAX_SAVE_TRIES = 3;


	public static class Factory extends ERXObjectStoreCoordinatorPool.MultiOSCFactory {
		
        public Factory(ERXObjectStoreCoordinatorPool pool, ERXEC.Factory backingFactory) {
        	super( pool, backingFactory );
        }

		protected EOEditingContext _createEditingContext( EOObjectStore parent ) {
			return new MDSEditingContext( parent == null ? EOEditingContext.defaultParentObjectStore() : parent );
		}
	}
	

	
//	public boolean isLocked() {
//		boolean isLocked;
//		try {
//			new ThreadTimer( 500, 
//				new ThreadTimer.Timeable() {
//					@Override
//					public void executeUntilTimeout() throws Throwable {
//						try {
//							lock();
//							throw new ObjectReturnException( false );
//						}
//						finally {
//							unlock();
//						}
//					}
//				}
//			).execute();
//			isLocked = true;
//		}
//		catch ( ObjectReturnException e ) {
//			isLocked = (Boolean)e.object();
//		}
//        catch ( ThreadTimer.TimeExpiredException e ) {
//        	isLocked = true;
//		}
//		catch ( Throwable t ) {
//			throw new RuntimeException( "INTERNAL ERROR: Unable to determine if the EC is locked" );
//		}
//		return isLocked;
//	}

	private static NSMutableDictionary ecNames = new NSMutableDictionary();

	private NSTimestamp _creationTime;
	public MDSEditingContext() {
		_creationTime = new NSTimestamp();
	}

	public MDSEditingContext( EOObjectStore os ) {
		super( os );
		_creationTime = new NSTimestamp();
	}

	public void useRefaultingToResolveSaveConflicts() {
		lock();
		try {
			super.setOptions( true, true, false );
		}
		finally {
			unlock();
		}
		//_lockConflictResolutionMethod = REFAULT;
	}

	public static EOEditingContext newEditingContext( String ecName ) {
		return newEditingContext( null, ecName );

	}

	public static EOEditingContext newEditingContext( EOObjectStore os, String ecName ) {
		ERXEC ec = (ERXEC) (os == null 
			? ERXEC.newEditingContext() 
			: ERXEC.newEditingContext( os ));
					
		ec.lock();
		try {
			// nh 11/17/10: We have screwy code that sets things in validateForSave (like isNew in ClientNote)...
			// so don't disable validateForSave.
			//ERXEC.factory().setDefaultDelegateOnEditingContext(ec, false);
			ec.setOptions( true, true, true );
		}
		finally {
			ec.unlock();
		}
		
		synchronized( ecNames ) {
			ecNames.setObjectForKey( ecName, ec.hashCode() );
		}
		return ec;
	}
	

	public String toString() {
		String ecName = (String) ecNames.objectForKey( this.hashCode() );
		return "[" + ecName + "] (created: " + _creationTime + ") " + super.toString();
	}

	/** @deprecated -- we now use the ERXEC options for recovering, re-trying, and merging instead of these methods to recover 
	 */
	public void ___saveChanges() {

        //System.out.println("Starting save changes...");
		boolean success = false;
		int curTry = 0;
		while( ! success ) {
			++curTry;
			try {
				//System.out.println("Conflict resolving editing context, saving changes...");
				super.saveChanges();
				success = true;
			}
			catch( IllegalStateException e ) {
				//_logSaveChangesIllegalStateException( e );
				throw e;
			}
			catch (NullPointerException e) {
				//_logSaveChangesNullPointerException( e );
				throw e;		// For now, throw it on up since we don't know how to handle it
			}
			catch( EOGeneralAdaptorException saveException ) {

				if( curTry >= _MAX_SAVE_TRIES ) {
					_logger.error( "Attempt to save " + toString() + " still failed after " + curTry + " tries. Giving up..." );

					// revert ec to a stable state
					super.revert();
					throw saveException;
				}


				// http://developer.apple.com/documentation/WebObjects/Enterprise_Objects/UpdateStrategies/index.html

				_logger.error( "Exception while saving changes to " + toString() + ": " + saveException, saveException );

				// Determine if the exeption is an optimistic locking exception
				if (isOptimisticLockingFailure(saveException)) {       //Deal with the optimistic locking exception.
					switch( _lockConflictResolutionMethod ) {
						case LAST_WRITE_WINS:
							handleOptimisticLockingFailureByLastWriteWins( saveException );
							break;
						case REFAULT:
						default:
							handleOptimisticLockingFailureByRefaulting( saveException );
					}
				} else {

					// Might have been a deadlock situation in the database. (Can happen
					// when running multiple instances, as each instance has a separate
					// database connection, and they might be waiting on each other in
					// transactions of updating the same tables.)
					//
					// In the words of Scott from OpenBase (e-mail to Jonathan, 2/15/06):
					//
					// CONN#1: START TRANSACTION
					// CONN#1: UPDATE TABLE A
					//
					// ...meanwhile...
					//
					// CONN#2: START TRANSACTION
					// CONN#2: UPDATE TABLE B
					// CONN#2: UPDATE TABLE A (forces CONN#2 to wait for CONN#1 to either commit or rollback)
					//
					// ...meanwhile conn#1 continues:
					//
					// CONN#1: UPDATE TABLE B (deadlock detected and CONN#1 terminated so CONN#2 can continue)
					//
					// "That is essentially the sequence of events. The basic problem has to do with serialization
					// of transactions and avoiding deadlocks while serializing them. So the correct behavior is
					// that one of the transactions must fail because they cannot be serialized."

					// In case the problem was due to deadlock, we wait a second, then try again:
					try {
						Thread.sleep( 1000 );
					}
					catch( InterruptedException ignored ) {}
				}

				_logger.warn( "Attempting to save " + toString() + " again..." );
			}
		}

        // System.out.println("Leaving save changes...");
    }

    // Determine if the exception thrown during a save is an optimistic locking exception.
    public boolean isOptimisticLockingFailure(EOGeneralAdaptorException exceptionWhileSaving) {
        //Get the info dictionary that is created when the exception is thrown.
        NSDictionary exceptionInfo = exceptionWhileSaving.userInfo();
        //Determine the type of the failure.
        Object failureType = (exceptionInfo != null)
            ? exceptionInfo.objectForKey(EOAdaptorChannel.AdaptorFailureKey)
            : null;

        //Return depending on the type of failure.
        if ((failureType != null) && (failureType.equals(EOAdaptorChannel.AdaptorOptimisticLockingFailure)))
            return true;
        else
            return false;
    }

    public void handleOptimisticLockingFailureByRefaulting(EOGeneralAdaptorException lockingException) {
        // Deal with an optimistic locking failure.

        // Get the info dictionary that is created when the exception is thrown.
        NSDictionary info = lockingException.userInfo();

        // Determine the adaptor operation that triggered the optimistic locking failure.
        EOAdaptorOperation adaptorOperation =
            (EOAdaptorOperation)info.objectForKey(EOAdaptorChannel.FailedAdaptorOperationKey);
        int operationType = adaptorOperation.adaptorOperator();

        // Determine the database operation that triggered the failure.
        EODatabaseOperation dbOperation = (EODatabaseOperation)info.objectForKey(EODatabaseContext.FailedDatabaseOperationKey);

        // Retrieve the enterprise object that triggered the failure.
        EOEnterpriseObject failedEO = (EOEnterpriseObject)dbOperation.object();

        // Take action based on the type of adaptor operation that triggered the optimistic locking failure.
        if (operationType == EODatabaseOperation.AdaptorUpdateOperator) {
            // Recover by refaulting the enterprise object involved in the failure.
            // This refreshes the eo's data and allows the user to enter changes again and resave.
            failedEO.editingContext().invalidateObjectsWithGlobalIDs( new NSArray( super.globalIDForObject( failedEO ) ) );
        }

        else {
            // The optimistic locking failure was caused by another type of adaptor operation, not an update.
            throw new NSForwardException(lockingException, "Unknown adaptorOperator " + operationType
                                         + " in optimistic locking exception.");
        }
        //super.saveChanges();
    }

    public void handleOptimisticLockingFailureByLastWriteWins(EOGeneralAdaptorException lockingException) {
        // Deal with an optimistic locking failure.

        // Get the info dictionary that is created when the exception is thrown.
        NSDictionary info = lockingException.userInfo();

        // Determine the adaptor operation that triggered the optimistic locking failure.
        EOAdaptorOperation adaptorOperation =
            (EOAdaptorOperation)info.objectForKey(EOAdaptorChannel.FailedAdaptorOperationKey);
        int operationType = adaptorOperation.adaptorOperator();

        // Determine the database operation that triggered the failure.
        EODatabaseOperation dbOperation = (EODatabaseOperation)info.objectForKey(EODatabaseContext.FailedDatabaseOperationKey);
        // Retrieve the enterprise object that triggered the failure.
        EOEnterpriseObject failedEO = (EOEnterpriseObject)dbOperation.object();
        // Retrieve the dictionary of values involved in the failure.
        NSDictionary valuesInFailedSave = adaptorOperation.changedValues();
        NSLog.out.appendln("valuesInFailedSave: " + valuesInFailedSave);

        // Take action based on the type of adaptor operation that triggered the optimistic locking failure.
        if (operationType == EODatabaseOperation.AdaptorUpdateOperator) {
            // Recover by essentially ignoring the optimistic locking failure and committing the
            // changes that originally failed. This is a last write wins policy.
            // Overwrite any changes in the database with the eo's values.
            //super.refaultObject( failedEO );	// not WO 5.1 compatible
            failedEO.editingContext().invalidateObjectsWithGlobalIDs( new NSArray( super.globalIDForObject( failedEO ) ) );
            //System.out.println("-------------------------\nFailed EO before reapplying changes: " + failedEO );
            //super.refreshObject( failedEO );
            failedEO.reapplyChangesFromDictionary(valuesInFailedSave);
            //System.out.println("-------------------------\nFailed EO after reapplying changes: " + failedEO );
        }
        else {
            _logger.warn("Can't recover from conflict during operation " + adaptorOperation );
            // The optimistic locking failure was causes by another type of adaptor operation, not an update.
            throw new NSForwardException(lockingException, "Unknown adaptorOperator " + operationType
                                         + " in optimistic locking exception.");
        }
        //super.saveChanges();
    }

//	private void _logSaveChangesIllegalStateException( IllegalStateException e ) {
//		// This is just for debugging; hopefully we don't get this very often
//
//		if( parentObjectStore() instanceof EODatabaseContext ) {
//			EODatabaseContext dbContext = (EODatabaseContext) parentObjectStore();
//			NSArray dbOps = (NSArray) dbContext._databaseContextState().valueForKey( "databaseOperations" );
//			for( EODatabaseOperation dbOp: (Collection<EODatabaseOperation>) dbOps ) {
//				EOEntity entity = dbOp.entity();
///*4878*/        if(dbOp.databaseOperator() == 2 && EOAccessPackageAccessor.entityHasNonUpdateableAttributes( entity ))	// update
//                {
///*4879*/            NSArray keys = EOAccessPackageAccessor.dbSnapshotKeysForEntity( entity );
///*4880*/            NSDictionary dbSnapshot = dbOp.dbSnapshot();
///*4881*/            NSDictionary newRow = dbOp.newRow();
///*4886*/            for(int i = keys.count() - 1; i >= 0; i--)
//                    {
///*4887*/                String key = (String)keys.objectAtIndex(i);
///*4888*/                EOAttribute att = entity.attributeNamed(key);
///*4889*/                if(att._isNonUpdateable() && !dbSnapshot.objectForKey(key).equals(newRow.objectForKey(key)))   {
///*4891*/                    if(att.isReadOnly())
///*4892*/                        throw new IllegalStateException("cannot update read-only key '" + key + "' on object:" + dbOp.object() + " of entity: " + entity.name() + " in databaseContext " + this);
///*4895*/                    else {
///*4895*/                        //throw new IllegalStateException("cannot update primary-key '" + key + "' from '" + dbSnapshot.objectForKey(key) + "' to '" + newRow.objectForKey(key) + "' on object:" + dbOp.object() + " of entity: " + entity.name() + " in databaseContext " + this);
//								Object fromVal = dbSnapshot.objectForKey(key);
//								Object toVal = newRow.objectForKey(key);
//								_logger.error( "fromVal equals toVal? " + (fromVal == null ? toVal == null : fromVal.equals( toVal ) ) );
//								_logger.error( "FromVal = '" + fromVal + "'" + (fromVal == null ? "" : " (" + fromVal.getClass().getName() + ")") + " ToVal = '" + toVal + "'" + (toVal == null ? "" : " (" + toVal.getClass().getName() + ")") );
//							}
//						}
//                    }
//
//                }
//
//			}
//
//		}
//	}

//	private void _logSaveChangesNullPointerException( NullPointerException e ) {
//		_logger.error( "ERROR while saving changes: " + e, e );
//		EODatabaseOperation dbOp;
//		Enumeration mapEnumerator;
//		EOObjectStoreCoordinator rootStore = (EOObjectStoreCoordinator) rootObjectStore();
//
//		@SuppressWarnings("unchecked")
//		Collection<EOCooperatingObjectStore> cooperatingStores = rootStore.cooperatingObjectStores();
//		for( EOCooperatingObjectStore aStore: cooperatingStores ) {
//			if( ! (aStore instanceof EODatabaseContext) ) {
//				_logger.error( "MDSEditingContext.saveChanges(): Cooperating object store returned by rootObjectStore is not of type EODatabaseContext object, but of " + aStore.getClass().getName() + ". Skipping debug info for this store..." );
//			}
//			else {
//				NSDictionary<EOGlobalID, EODatabaseOperation> dbOperationsByGlobalID = (NSDictionary<EOGlobalID, EODatabaseOperation>) EOAccessPackageAccessor.dbOperationsByGlobalIDForDatabaseContext( (EODatabaseContext) aStore );
//				if( dbOperationsByGlobalID == null ) {
//					_logger.error( "MDSEditingContext.saveChanges(): dbOperationsByGlobalIDForDatabaseContext is null" );
//				}
//				else {
//					for (EOGlobalID gid: dbOperationsByGlobalID.keySet() ) {
//						dbOp = (EODatabaseOperation) dbOperationsByGlobalID.objectForKey(gid);
//
//						EOEntity entity = dbOp.entity();
//						NSArray keys = EOAccessPackageAccessor.dbSnapshotKeysForEntity( entity );
//						NSDictionary dbSnapshot = dbOp.dbSnapshot();
//						NSDictionary newRow = dbOp.newRow();
//						_logger.warn( "MDSEditingContext.saveChanges(): dbOp = " + dbOp + " dbSnapshot = " + dbSnapshot + " newRow = " + newRow
//								+ " dbOp.databaseOperator = " + dbOp.databaseOperator()
//								+ " entity.hasNonUpdatabaseAttributes = " + EOAccessPackageAccessor.entityHasNonUpdateableAttributes( entity ) );
//
//						for( int i = keys.count() - 1; i >= 0; i-- ) {
//							String key = (String) keys.objectAtIndex( i );
//							EOAttribute att = entity.attributeNamed( key );
//							_logger.warn( "  att = " + att + (att == null ? "" : " att._isNonUpdateable = " + att._isNonUpdateable()) + " dbSnapshot[" + key + "] = " + dbSnapshot.objectForKey( key ) + " newRow[" + key + "] = " + newRow.objectForKey( key ) );
//						}
//					}
//				}
//			}
//		}
//	}
	
	private Exception disposeTraceException;
	private boolean isDisposed = false;
	@Override
	public void dispose() {
		isDisposed = true;
//		if( AppProperties.isTestMode() && _logger.isDebugEnabled() ) {
			disposeTraceException = new Exception( "STRACK TRACE" );
//		}
		super.dispose();
	}
	
	public boolean isDisposed() {
		return isDisposed;
	}
	
	public Exception disposeTraceException() {
		return disposeTraceException;
	}
	
	// ----- Debugging -----
	@Override
	public void recordObject(EOEnterpriseObject object, EOGlobalID gid) {
		super.recordObject( object, gid );
		
		// TODO - Test
//		if( AppProperties.isTestMode() && _logger.isDebugEnabled() ) {
//			if( object instanceof MDSGenericRecord ) {
//				((MDSGenericRecord) object).setPreservedEditingContext( this );
//			}
//		}
	}

	/**
	 * Returns true if the provided EC is a non-top-level EC.
	 */
	public static boolean isChildOfAnyEc( EOEditingContext _ec ) {
		if ( _ec == null )
			return false;
		
		EOObjectStore parentObjectStore = _ec.parentObjectStore();
		return parentObjectStore != null && parentObjectStore instanceof EOEditingContext;
	}

	
}
