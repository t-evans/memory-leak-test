package com.test.migrations;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationTable;
import er.extensions.migration.ERXModelVersion;

public class DataStoreMigration extends ERXMigrationDatabase.Migration {
	@Override
	public NSArray<ERXModelVersion> modelDependencies() {
		return null;
	}
  
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		// DO NOTHING
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable {
		ERXMigrationTable dataStoreTable = database.newTableNamed("DataStore");
		dataStoreTable.newBlobColumn("data", true);
		dataStoreTable.newIntegerColumn("dataContainerID", false);
		dataStoreTable.newIntegerColumn("id", false);
		dataStoreTable.create();
	 	dataStoreTable.setPrimaryKey("id");

		dataStoreTable.addForeignKey("dataContainerID", "DataContainer", "id");
	}
}