package com.test;

import org.apache.log4j.Logger;

import com.webobjects.eoaccess.EODatabase;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

import er.extensions.eof.ERXDatabaseContext;

public class MDSDatabaseContext extends ERXDatabaseContext {

	private static Logger logger = Logger.getLogger( MDSDatabaseContext.class );
	public MDSDatabaseContext(EODatabase database) {
		super(database);
	}
	
	/*
	public void invalidateObjectsWithGlobalIDs(NSArray gids) {
		super.invalidateObjectsWithGlobalIDs( gids );
	}
	*/
	
	/**
	 * Fixes snapshotForGlobalID() to return an empty dictionary if the snapshot is null.
	 * 
	 * nh 2/14/11:
	 * EODatabasecontext.invalidateObjectsWithGlobalIDs( NSArray gids ) throws a NullPointerException if this
	 * method returns null. Oddly, this only ever seems to happen when we enable the MDSEntityDependencyOrderingDelegate
	 * in MDSApplication._installEntityDependencyOrderingDelegate(). Not sure how that delegate would cause
	 * some snapshots to come back null. 
	 * 
	 * Regardless, it appears that EODatabaseContext.invalidateObjectsWithGlobalIDs( NSArray gids ) should deal
	 * more gracefully if the snapshot is null. As a workaround, we override this method to return an 
	 * empty dictionary if the snapshot is null.
	 */
	public NSDictionary snapshotForGlobalID(EOGlobalID gid) {
		NSDictionary snapshot = super.snapshotForGlobalID( gid );
		
		if( snapshot == null ) {
			if( logger.isDebugEnabled() )
				logger.debug( "Returning null snapshot for GID " + gid );	// nh 3/31/12: This seems to happen often enough without serious consequences, I'm demoting it to DEBUG 
			snapshot = NSDictionary.EmptyDictionary;
		}
		
		return snapshot;
	}

}
