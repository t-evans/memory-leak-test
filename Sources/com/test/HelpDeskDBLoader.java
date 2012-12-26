//
// DBLoader.java
// Project Helpdesk
//
// Created by nathan on Mon Sep 16 2002
//
// Requres AppProperties, ClientFactory, ClientType, PriorityType, StatusType,
// Subscriber, Preference, Tech

package com.test;

import org.apache.log4j.Logger;

import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOJoin;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.EOSynchronizationFactory;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOObjectStoreCoordinator;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.eof.ERXModelGroup;
import er.extensions.eof.ERXObjectStoreCoordinator;

public class HelpDeskDBLoader extends Object {

	
	protected static Logger _logger = Logger.getLogger( HelpDeskDBLoader.class );

	public static class WriteSqlLogOnlyException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		public WriteSqlLogOnlyException( String s ) {
			super( s );
		}
	}

    //---------------------------------------------------------------------------
//    public static void updateDatabase( MDSProgressStatus status )
//        throws HelpDeskDataUpdater.InvalidDatabaseVersionException, Throwable
//    {
//    	
//    	if( status == null )
//    		status = new MDSProgressStatus();
//    	
//        //Keyboard.in.pause("About to load models...");
//        //loadModelsWithPrototypeReplacement();
//        if( DatabaseInfo.dbIsUpToDate() ) {
//            //_logger.info("Database is up to date.");
//            return;
//        }
//        else {
//            _logger.info("Updating database...");
//            new MDSDBUpdater().updateDatabaseWithModelNamed( AppProperties.DB_MODEL_NAME, status, AppProperties.updateSqlLogFile() );
//            System.gc();
//            
//            //loadModelsWithPrototypeReplacement();	// force update of connection info
//            new HelpDeskDataUpdater().updateData( status );
//        }
//    }


	
    //---------------------------------------------------------------------------
    public static void loadModelsWithPrototypeReplacement() {

    //    EOEditingContext ec = new EOEditingContext();
		String modelName = "TestEOModel";

//        MDSDatabaseConnectionDescriptor descriptor = HelpDeskDatabaseConfigurationProvider.currentDescriptor();

		//_logger.info( "Loading models for descriptor: " + descriptor );

        // TODO: nh 1/15/09: Model validator doesn't work correctly if prototypes have already been flattened;
        // however, EOF doesn't seem to work correctly if they haven't been flattened. Need to look into this more.
        // For now, validation is disabled. (SCARY!)
        
		if( "true".equals( System.getProperty( "com.macsdesign.whd.validateModels", "false" ) ) ) { //AppProperties.isTestMode() ) {
			
			EOObjectStoreCoordinator osc = new ERXObjectStoreCoordinator( true );
	    	osc.lock();
	    	try {

	    		System.setProperty( "er.extensions.ERXModelGroup.flattenPrototypes", "false" );
	    		EOModel model = ERXModelGroup.defaultGroup().modelNamed( modelName );

	    		// TODO
//	    		ModelValidator validator = new ModelValidator();
//	    		//JDBCAdaptor adaptor = (JDBCAdaptor) EOAdaptor.adaptorWithModel( model );
//	    		JDBCAdaptor adaptor = (JDBCAdaptor) MDSDatabaseUtils.adaptorForObjectStoreCoordinatorAndModel( osc, model );
//	    		validator.addValidatorForPrototypes( ((MDSPlugInExtensionsProvider) new MDSPostgreSQLPlugIn( adaptor ).createSchemaSynchronizationFactory()).plugInExtensions().modelValidator(), "EOJDBPostgreSQLPrototypes" );
//	    		validator.addValidatorForPrototypes( ((MDSPlugInExtensionsProvider) new MDSFrontBasePlugIn( adaptor ).createSchemaSynchronizationFactory()).plugInExtensions().modelValidator(), "EOJDBCFrontBasePrototypes" );
//	    		validator.addValidatorForPrototypes( ((MDSPlugInExtensionsProvider) new MDSOpenBasePlugIn( adaptor ).createSchemaSynchronizationFactory()).plugInExtensions().modelValidator(), "EOJDBCOpenBasePrototypes"  );
//	    		validator.addValidatorForPrototypes( ((MDSPlugInExtensionsProvider) new MDSMicrosoftPlugIn( adaptor ).createSchemaSynchronizationFactory()).plugInExtensions().modelValidator(), "EOJDBCSQLServer2000Prototypes" );
//	    		validator.addValidatorForPrototypes( ((MDSPlugInExtensionsProvider) new MDSMicrosoft2005PlugIn( adaptor ).createSchemaSynchronizationFactory()).plugInExtensions().modelValidator(), "EOJDBCSQLServer2005Prototypes" );
//	    		validator.addValidatorForPrototypes( ((MDSPlugInExtensionsProvider) new MDSMySQLPlugIn( adaptor ).createSchemaSynchronizationFactory()).plugInExtensions().modelValidator(), "EOJDBMySQLPrototypes" );
//	    		validator.addValidatorForPrototypes( ((MDSPlugInExtensionsProvider) new MDSOraclePlugIn( adaptor ).createSchemaSynchronizationFactory()).plugInExtensions().modelValidator(), "EOJDBCOraclePrototypes" );
//	    		validator.validateModel( model );
            
	    	}
	    	catch( Exception e ) {
	    		_logger.error( "Error while validating model: " + e, e );
	    	}
	    	finally {
	    		osc.unlock();
	    		osc.dispose();
	    	}
	
	    	System.err.println();
	    	System.err.println( "--------------------------");
    		System.err.println( "COMPLETED MODEL VALIDATION. Set com.macsdesign.whd.validateModels=false to disable model validation and resume normal application execution." );
    		System.exit(0);

        }

		
		// In order to get the jdbc2info plist from the model, the model must provide
		// a database configuration that matches the descriptor's 'subprotocol' property
		// (i.e., a descriptor subprotocol of 'microsoft2000' requires a database configuration called 'microsoft2000')
		
		//NSDictionary cd = descriptor.connectionDictionary();
//    	String url = "jdbc:FrontBase://127.0.0.1:20292/tz=UTC";
//    	String username = "whd";
//    	String password = "whd";
//    	String subprotocol = "FrontBase";
//    	String prototypeEntityName = "EOJDBCFrontBasePrototypes";
//    	String dbPlugin = "com.test.MDSFrontBaesLPlugIn";
		
    	String url = "jdbc:postgresql://127.0.0.1:5433/memleaktest";;
    	String username = "postgres";
    	String password = "postgres";
    	String subprotocol = "postgresql";
    	String prototypeEntityName = "EOJDBCPostgreSQLPrototypes";
    	String dbPlugin = "com.test.MDSPostgreSQLPlugIn";

		System.setProperty( modelName + ".URL", url );
		System.setProperty( modelName + ".DBUser", username );
		System.setProperty( modelName + ".DBPassword", password );
		System.setProperty( modelName + ".DBConfigName", subprotocol );
		System.setProperty( modelName + ".EOPrototypesEntity", prototypeEntityName ); 
    	System.setProperty( modelName + ".DBPlugin", dbPlugin );
    	
    	// nh 1/15/09: We pushed the JDBC Info down into the plugins, so this is not necessary (though removeJdbc2Info should remain set to true, the default)
    	//System.setProperty( modelName + ".DBJDBCInfo", descriptor.jdbcInfo() );
    	//System.setProperty( modelName + ".removeJdbc2Info", "false" );
    	
    	ERXModelGroup defaultMG = ((ERXModelGroup) ERXModelGroup.defaultGroup());
    	EOModel model = defaultMG.modelNamed( modelName );
    	
   
    	/*
    	NSMutableDictionary userInfo = model.userInfo().mutableClone();
    	userInfo.removeObjectForKey( "_EOPrototypesFixed" );
    	model.setUserInfo(  userInfo );
    	*/
    	
    	// nh 1/15/09: This was an attempt to flatten the protototypes after doing model validation
    	if( false ) { // AppProperties.isTestMode() ) {
    		defaultMG.removeModel( model );
        	System.setProperty( "er.extensions.ERXModelGroup.flattenPrototypes", "true" );
            System.setProperty( "er.extensions.ERXModelGroup.patchModelsOnLoad", "true" );	// reload the model info
        	defaultMG.addModel( model );
    	}

    	//((ERXModelGroup) ERXModelGroup.defaultGroup()).resetConnectionDictionaryInModel( model );
    	
    	// Adding this for my Eclipse which was getting a null model for some reason.  Seems to fix the issue
    	int count = 0; 
    	while ( model == null ) {
    		model = defaultMG.modelNamed( modelName );
    		if( model == null ) {
	    		try {
	    			Thread.sleep( 1000 );
	    		}
	    		catch( InterruptedException ignored ) {}
    		}
    		
    		if( ++count > 5 ) {
    			// Last ditch effort...
    	    	if ( model == null ) {
    	    		//File modelPath = new File( "/Developer/MacsDesign/EclipseWorkspace/Helpdesk/Resources/models/dbwhd.eomodeld" );
//    	    		String modelPath = AppProperties.getProperty( "WHDModelPath" );
//    	    		if ( ! MDSUtils.isEmptyOrNullString(modelPath) ) {
//	    	    		model = MDSDatabaseUtils.modelFromPath( modelPath );
//	    	    		defaultMG.addModel(model);
//    	    		}
    	    	}
    	    	
    	    	if ( model == null )
    	    		throw new RuntimeException( "INTERNAL ERROR: Unable to load database model '" + modelName + "'" );
    		}
    	}
    	
    	defaultMG.resetConnectionDictionaryInModel( model );
    	
    	// Call PlugInExtensions.modelWasLoaded()
    	// nh 6/18/10: Not sure if it's okay to close this connection or not -- currently Oracle is the only DB that uses modelWasLoaded()...
    	// need to test. For now, leaving the connection in place -- i.e., NOT calling ERXObjectStoreCoordinator(true) and then disposing the OSC
    	
    	// TODO
//		EOObjectStoreCoordinator osc = new ERXObjectStoreCoordinator();
//    	osc.lock();
//    	try {
//    		MDSDatabaseUtils.plugInExtensionsForAdaptor( MDSDatabaseUtils.adaptorForObjectStoreCoordinatorAndModel( osc, model ) ).modelWasLoaded( model );
//    	}
//    	finally {
//    		osc.unlock();
//    	}
    	
    	logModelInfo( model );
    	
    }

	public static void logModelInfo( EOModel model ) {
    	for( EOEntity entity: (NSArray<EOEntity>) model.entities() ) {
    		_logger.debug( "ENTITY: " + entity.name() );
    		for( EOAttribute attribute: (NSArray<EOAttribute>) entity.attributes() ) {
    			_logger.debug( "  - " + attribute.name() + " (" + attribute.columnName() + ") Type: " + attribute.adaptorValueType() + " Conv. Method: " + attribute.adaptorValueConversionMethodName()
    					+ " Def: " + attribute.definition() + " Ext. Type: " + attribute.externalType() + " Fact. Method Type: " + attribute.factoryMethodArgumentType() + " Prototype: " + attribute.prototypeName() + " " + attribute ); 
    		}
    	}
    }

	

    //---------------------------------------------------------------------------
    public static void showModelGroupState( EOModelGroup testMg ) {
        if( testMg == null )
            testMg = EOModelGroup.defaultGroup();

        NSArray modelsLoaded = testMg.models();
        java.util.Enumeration objEnum = modelsLoaded.objectEnumerator();
        _logger.error("EOModelGroup:"
                        + ((testMg == EOModelGroup.defaultGroup()) ? " (default)" : ""));
        while( objEnum.hasMoreElements() ) {
            showModelState( (EOModel) objEnum.nextElement() );
        }
    }
    //---------------------------------------------------------------------------
    public static void showModelState(EOModel curModel) {
        _logger.error("Model: " + curModel.name() );

        java.util.Enumeration entityEnum = curModel.entityNames().objectEnumerator();
        while( entityEnum.hasMoreElements() ) {
            _logger.error("  - " + (String) entityEnum.nextElement() );
        }

        NSArray externalModelsReferenced = curModel.externalModelsReferenced();
        if( externalModelsReferenced != null ) {


            java.util.Enumeration refModelEnum = externalModelsReferenced.objectEnumerator();
            _logger.error("  External models referenced: ");
            while( refModelEnum.hasMoreElements() ) {
                _logger.error("  > " + ((EOModel)refModelEnum.nextElement()).name() );
            }

        }
        else _logger.error("  No external models referenced.");

    }

    //---------------------------------------------------------------------------
    private static void showFetchSpecificationsForModelGroup ( EOModelGroup mg ) {

        _logger.info("MODEL GROUP FETCH SPECIFICATIONS");
        NSMutableArray entities = new NSMutableArray();
        NSArray models = mg.models();
        for ( int i=0; i < models.count(); ++i )
            entities.addObjectsFromArray( ((EOModel) models.objectAtIndex(i)).entities() );

        for( int i=0; i < entities.count(); ++i ) {
            EOEntity entity = (EOEntity) entities.objectAtIndex(i);

            _logger.info("Entity: " + entity.name());
            NSArray fSpecs = entity.fetchSpecificationNames();
            for( int j=0; j < fSpecs.count(); ++j ) {
                String fSpecName = (String) fSpecs.objectAtIndex(j);
                EOFetchSpecification fSpec = entity.fetchSpecificationNamed( fSpecName );
                _logger.info("  " + fSpecName + ": " + fSpec.toString());
                _logger.info("  Qualifier class: " + fSpec.qualifier().getClass().getName() );
                _logger.info("  Qualifier: " + fSpec.qualifier() );
            }
        }
    }

    //---------------------------------------------------------------------------
    private static void showRelationshipsForModelGroup( EOModelGroup mg ) {

        _logger.info("MODEL GROUP RELATIONSHIPS");
        NSMutableArray entities = new NSMutableArray();
        NSArray models = mg.models();
        for ( int i=0; i < models.count(); ++i )
            entities.addObjectsFromArray( ((EOModel) models.objectAtIndex(i)).entities() );

        for( int i=0; i < entities.count(); ++i ) {
            EOEntity entity = (EOEntity) entities.objectAtIndex(i);

            _logger.info("Entity: " + entity.name());
            NSArray relationships = entity.relationships();
            for ( int j=0; j < relationships.count(); ++j ) {
                EORelationship relationship = (EORelationship) relationships.objectAtIndex( j );
                NSArray joins = relationship.joins();
                for( int k=0; k < joins.count(); ++k ) {
                    EOJoin join = (EOJoin) joins.objectAtIndex( k );
                    EOAttribute srcAttr = join.sourceAttribute();
                    EOAttribute dstAttr = join.destinationAttribute();
                    _logger.info("  - " + relationship.name() + " = " + srcAttr.name()
                                      + " -> " + dstAttr.entity().name() + ": " + dstAttr.name() );
                }
            }
        }
    }

	//---------------------------------------------------------------------------
    public static void generateModelFiles( String outputDirectory ) {
        
    	// This method no longer works, now that we're using Wonder prototyping.
    	// Shouldn't even be necessary anymore.. should be able to do this directly from Entity Modeler
    	
    	/*
        // save the default model group
        
        String modelsDir = HelpDeskDBLoader.modelsDir();
        EOModel model = MDSDatabaseUtils.modelFromPath( modelsDir + "/openbase/" + AppProperties.DB_MODEL_NAME + ".eomodeld"  );

		// Include a text file containing all entity names, in case we need to split
		// the migration up into a few sets of entities at a time (can be used to
		// copy/paste the desired tables)

		_logger.debug( "Generating entity list..." );
		
		File entityFile = new File( outputDirectory + "/entities.txt" );
		FileWriter fw = null;
		try {

			fw = new FileWriter( entityFile );
			NSArray entityNames = model.entityNames();
			fw.write( entityNames.componentsJoinedByString( " " ) );
		}
		catch( Exception e ) {
			_logger.error( "ERROR while attempting to generate entity list file: " + e, e );
		}
		finally {
			if( fw != null ) {
				try {
					fw.close();
				}
				catch( Exception e ) {
					_logger.error( "ERROR while attempting to close file: " + e, e );
				}
			}
		}
		
		
		
        EOModelGroup originalGroup = EOModelGroup.defaultGroup();
        EOModelGroup mg = new EOModelGroup();
        mg.addModel( model );
        EOModelGroup.setDefaultGroup( mg );
		
        String[] subprotocols = new String[] { "FrontBase", "mysql", "openbase", "oracle", "postgresql", "sqlserver" };
		
        
        for( int i=0; i < subprotocols.length; ++i ) {
            
            _logger.debug( "Generating model files for subprotocol '" + subprotocols[i] + "'..." );
            if( ! subprotocols[i].equals( "openbase" ) )
                MDSDatabaseUtils.swapInPrototypesForSubprotocol( model, subprotocols[i], modelsDir );
			
			MDSDatabaseUtils.pushPrototypesToAttributes( model );

			EOEntity protoEntity = model.entityNamed( "EOPrototypes" );
			if( protoEntity != null )
				model.removeEntity( protoEntity );
            
			model.writeToFile( outputDirectory + "/" + AppProperties.DB_MODEL_NAME + "_" + subprotocols[i].toLowerCase() );

			if( protoEntity != null )
				model.addEntity( protoEntity );
        }
        
		
        EOModelGroup.setDefaultGroup( originalGroup );
		
        */
    }
	
    //---------------------------------------------------------------------------
    public static String generateSQL( String pathPrefix ) {
        EOModelGroup mg = EOModelGroup.defaultGroup();

        String sqlScript = "";

        NSArray models = mg.models();
        for( int i=0; i < models.count(); ++i ) {
            EOModel model = (EOModel) models.objectAtIndex(i);
            _logger.info("Model name: " + model.name() );
            if( model.name().equals("ldapclient"))
                continue;

            model.writeToFile( pathPrefix + model.name());
            EOSynchronizationFactory syncFactory = new EOSynchronizationFactory( EOAdaptor.adaptorWithModel( model ) );

            String[] syncOptValues = new String[] {
                "YES",
                "YES",
                "YES",
                "YES",
                "YES",
                "NO",
                "NO",
                "NO"
            };

            String[] syncOptKeys = new String[] {
                "CreateTablesKey",
                "DropTablesKey",
                "CreatePrimaryKeySupport",
                "DropPrimaryKeySupport",
                "PrimaryKeyConstraintsKey",
                "ForeignKeyConstraintsKey",
                "CreateDatabaseKey",
                "DropDatabaseKey"
            };

            NSDictionary syncOpts = new NSDictionary( syncOptValues, syncOptKeys );

            sqlScript += syncFactory.schemaCreationScriptForEntities( model.entities(), syncOpts);

        }
        return sqlScript;
    }

}
