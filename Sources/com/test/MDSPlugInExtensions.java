//
//  MDSPlugInExtensions.java
//  Helpdesk
//
//  Created by Nathan Hadfield on Thu Mar 13 2003.
//  Copyright (c) 2003 MacsDesign Studio. All rights reserved.
//

package com.test;

import java.sql.SQLException;

import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.synchronization.EOSchemaGenerationOptions;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

public abstract class MDSPlugInExtensions {

	private EOAdaptor _adaptor;
	public MDSPlugInExtensions( EOAdaptor adaptor ) {
		_adaptor = adaptor;
	}
	
	public EOAdaptor adaptor() {
		return _adaptor;
	}
	
    public abstract EOSchemaGenerationOptions schemaCreationOptions();
    public abstract EOSchemaGenerationOptions columnInsertionOptions();
    public abstract EOSchemaGenerationOptions columnConversionOptions();
    
	/**
	 * ensures MDSColumnTypes have index info when passed to statementsToConvertColumnType
	 */
    public abstract boolean columnConversionRequiresIndexInfo();

    public abstract NSArray indexStatementsForRelationship( EORelationship relationship );
    public abstract NSArray indexStatementsForAttribute( EOAttribute attribute );
    public void validateDatabase( EOAdaptorChannel channel ) { }	// throws a RuntimeException if database isn't valid for the plugin
    public abstract NSArray preUpdateInitializationStatements();
    public NSArray postUpdateInitializationStatements( NSArray<EOEntity> newEntities ) { return NSArray.EmptyArray; }
//    public abstract ModelValidator.Validator modelValidator();
	public abstract boolean reportsLowerCaseIdentifiers();
	
	public void modelWasLoaded( EOModel model ) { }

	public boolean isForeignKeyConstraintSupported() { return true; }
	
	// These methods may need to access the database, so they are given an EOAdaptorChannel, which is assumed to be available (and its corresponding EOF stack locked)
	public abstract NSArray nullableColumnsDetectedForEntity( EOAdaptorChannel adaptorChannel, EOEntity entity ) throws SQLException;
	public abstract NSArray statementsToMakeColumnNullable( EOAdaptorChannel adaptorChannel, EOAttribute attribute );
    public abstract void updatePrimaryKeyGeneratorForEntity( EOAdaptorChannel adaptorChannel, String entity );

}
