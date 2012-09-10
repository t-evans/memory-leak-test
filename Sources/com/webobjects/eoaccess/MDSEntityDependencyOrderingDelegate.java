package com.webobjects.eoaccess;

import org.apache.log4j.Logger;

//import com.macsdesign.util.LoggerUtils;
//import com.macsdesign.util.MDSEntityFKConstraintOrder;
//import com.macsdesign.util.MDSEntityFKConstraintOrder2;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSComparator.ComparisonException;
import com.webobjects.foundation.NSForwardException;
import com.webobjects.foundation.NSMutableSet;

import er.extensions.eof.ERXDatabaseContextDelegate;
import er.extensions.eof.ERXEntityOrder;
import er.extensions.eof.ERXModelGroup;

public class MDSEntityDependencyOrderingDelegate extends ERXEntityDependencyOrderingDelegate {

//	private static Logger _logger = Logger.getLogger( MDSEntityDependencyOrderingDelegate.class );
//
//
//	private ERXDatabaseContextDelegate erxDelegate = new ERXDatabaseContextDelegate();
//	
//    public MDSEntityDependencyOrderingDelegate() {
//        super();
//    }
//
//	/**
//     * Lazy creation of an EOAdaptorOpComparator that uses a list of entities that are in FK dependancy order.
//     * Enable DEBUG logging to see the ordered list of entity names.
//     *
//     * @see com.webobjects.eoaccess.EOAdaptorOpComparator
//     * @return EOAdaptorOpComparator that uses a list of entities that are in FK dependancy order
//     */
//    protected NSComparator adaptorOpComparator( NSArray<EOAdaptorOperation> adaptorOperations ) {
//    	
//    	EOAdaptorOpComparator adaptorOpComparator;
//    	
//    	NSArray<EOEntity> entitiesInOperations = entitiesInOperations( adaptorOperations );
//    	MDSEntityFKConstraintOrder2 constraintOrder = new MDSEntityFKConstraintOrder2( entitiesInOperations );
//        NSComparator entityOrderingComparator = new ERXEntityOrder.EntityInsertOrderComparator(constraintOrder);
//        try {
//            NSArray<EOEntity> entityOrdering = entitiesInOperations.sortedArrayUsingComparator(entityOrderingComparator);
//            NSArray<String> entityNameOrdering = (NSArray<String>)entityOrdering.valueForKey("name");
//
//            if (_logger.isDebugEnabled()) {
//                _logger.debug("Entity ordering:\n" + entityNameOrdering.componentsJoinedByString("\n"));
//            }
//
//            adaptorOpComparator = new ERXAdaptorOpComparator(entityNameOrdering);
//        }
//        catch (ComparisonException e) {
//            throw NSForwardException._runtimeExceptionForThrowable(e);
//        }
//
//        return adaptorOpComparator;
//    }
//    
//    protected NSArray<EOEntity> entitiesInOperations( NSArray<EOAdaptorOperation> adaptorOperations ) {
//    	NSMutableSet<EOEntity> entities = new NSMutableSet<EOEntity>( adaptorOperations.count() );
//    	for( EOAdaptorOperation op: adaptorOperations ) {
//    		EOEntity entity = op.entity();
//    		if( entity != null && ! ERXModelGroup.isPrototypeEntity(entity) )
//    			entities.addObject( entity );
//    	}
//    	return entities.allObjects();
//    }
//
//    /**
//     * EODatabaseContext.Delegate method to order a list of adaptor operations.  Uses adaptorOpComparator() for the ordering.
//     *
//     * @param aDatabaseContext EODatabaseContext that the operations will be executed in
//     * @param adaptorOperations list of operations to execute
//     * @param adaptorChannel the adaptor channel these will be executed on
//     *
//     * @see com.webobjects.eoaccess.EODatabaseContext.Delegate#databaseContextWillPerformAdaptorOperations(EODatabaseContext, NSArray,EOAdaptorChannel)
//     * @return operations in an order that should avoid FK constraint violations
//     */
//    @Override
//    public NSArray databaseContextWillPerformAdaptorOperations(EODatabaseContext aDatabaseContext,
//                                                               NSArray adaptorOperations,
//                                                               EOAdaptorChannel adaptorChannel) {
//    	
//    	// ERXDatabaseContextDelegate removes pairs of insertion/deletions of the same object
//    	adaptorOperations = erxDelegate.databaseContextWillPerformAdaptorOperations( aDatabaseContext, adaptorOperations, adaptorChannel );
//    	
//        try {
//            return adaptorOperations.sortedArrayUsingComparator(adaptorOpComparator( adaptorOperations ));
//        }
//        catch (com.webobjects.foundation.NSComparator.ComparisonException e) {
//            throw NSForwardException._runtimeExceptionForThrowable(e);
//        }
//    }

	
}
