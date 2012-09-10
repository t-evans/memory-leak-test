//
//  MDSPostgreSQLPlugIn.java
//  Helpdesk
//
//  Created by Nathan Hadfield on 5/10/05.
//  Copyright (c) 2005 __MyCompanyName__. All rights reserved.
//

//
//  To use this plugin, either make the following call
//
//	 com.webobjects.jdbcadaptor.JDBCPlugIn.setPlugInNameForSubprotocol( "MDSOpenBasePlugIn", "openbase" );
//
//  or set the 'plugin' parameter of the model's connection dictionary to 'MDSOpenBasePlugIn'.
//
//  NOTE: If set, the 'plugin' parameter of the connection dictionary will override the class set by
//  JDBCPlugIn.setPlugInNameForSubprotocol
//

package com.test;

import java.sql.SQLException;

import org.apache.log4j.Logger;

//import com.macsdesign.whd.DatabaseInfo;
import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eoaccess.EOSQLExpressionFactory;
import com.webobjects.eoaccess.synchronization.EOSchemaGenerationOptions;
import com.webobjects.eoaccess.synchronization.EOSchemaSynchronization;
import com.webobjects.eoaccess.synchronization.EOSchemaSynchronizationFactory;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.jdbcadaptor.JDBCAdaptor;
import com.webobjects.jdbcadaptor.MySQLPlugIn;
import com.webobjects.jdbcadaptor.PostgresqlExpression;
import com.webobjects.jdbcadaptor.PostgresqlPlugIn;
import com.webobjects.jdbcadaptor.PostgresqlSynchronizationFactory;

public class MDSPostgreSQLPlugIn extends PostgresqlPlugIn {

	public MDSPostgreSQLPlugIn(JDBCAdaptor adaptor) {
		super(adaptor);
		// TODO Auto-generated constructor stub
	}
//	protected static Logger _logger = LoggerUtils.getLogger( MDSPostgreSQLPlugIn.class );
//
//	public static class PlugInExtensions extends MDSPlugInExtensions {
//
//		public PlugInExtensions( EOAdaptor adaptor ) {
//			super( adaptor );
//		}
//
//		public static final int MAX_IDENTIFIER_LENGTH = 63;
//
//        @Override
//		public EOSchemaGenerationOptions schemaCreationOptions() {
//        	EOSchemaGenerationOptions opts = new EOSchemaGenerationOptions();
//			opts.setCreateTables( true );
//			opts.setCreatePrimaryKeySupport( true );
//			opts.setPrimaryKeyConstraints( true );
//			opts.setForeignKeyConstraints( true );
//			return opts;
//        }
//		
//        @Override
//        public EOSchemaGenerationOptions columnInsertionOptions() {
//        	EOSchemaGenerationOptions opts = new EOSchemaGenerationOptions();
//        	opts.setManageForeignKeyConstraints( true );
//			opts.setManagePrimaryKeyConstraints( true );
//			opts.setManagePrimaryKeySupport( true );
//			return opts;
//        }
//		
//        @Override
//        public EOSchemaGenerationOptions columnConversionOptions() {
//        	EOSchemaGenerationOptions opts = new EOSchemaGenerationOptions();
//        	opts.setManageForeignKeyConstraints( true );
//        	opts.setManagePrimaryKeyConstraints( true );
//        	opts.setManagePrimaryKeySupport( true );
//			return opts;
//        }
//        
//        @Override
//        public boolean columnConversionRequiresIndexInfo() {
//        	return false;
//        }
//        
//
//		@Override
//		public NSArray statementsToMakeColumnNullable( EOAdaptorChannel adaptorChannel, EOAttribute attribute ) {
//
//			NSMutableArray statements = new NSMutableArray();
//
//			EOSQLExpressionFactory expFactory = adaptor().expressionFactory();
//			EOSQLExpression expr = expFactory.expressionForEntity( attribute.entity() );
//			String sqlStr = "alter table " + attribute.entity().externalName() + " alter column "
//				+ attribute.columnName() + " drop not null";
//			//_logger.info( "Statement to make attribute '" + attribute.entity().name() + "." + attribute.name() + "' nullable: " + sqlStr );
//			statements.addObject( expFactory.expressionForString( sqlStr ) );
//
//			return statements;
//
//		}
//
//		@Override
//		public NSArray nullableColumnsDetectedForEntity( EOAdaptorChannel adaptorChannel, EOEntity entity ) throws SQLException {
//			return MDSPlugInUtils.nullableColumnsDetectedForEntity( adaptorChannel, entity, reportsLowerCaseIdentifiers() );
//		}
//
//
//		@Override
//		public ModelValidator.Validator modelValidator() {
//			// MySQL requires PKs to not allow nulls; we do this check in the default validator
//			return new ModelValidator.Validator() {
//				@Override
//				public String name() { return "PostgreSQL"; }
//				@Override
//				public String subprotocol() { return "postgresql"; }
//
//				@Override
//				public void validateAttribute( EOAttribute attribute ) throws IllegalStateException {
//					String indexName = _indexNameForAttribute( attribute );
//					if( indexName.length() > MAX_IDENTIFIER_LENGTH )
//						throw new IllegalStateException( "Potential index name '" + indexName + "' exceeds PostgreSQL max of "
//														 + MAX_IDENTIFIER_LENGTH + " characters." );
//
//					//_logger.info( "name = " + attribute.entity().name() + "." + attribute.name() + " externalType = " + attribute.externalType() + " width = " + attribute.width() + " prototype = " + attribute.prototype().externalType() + " prototype width = " + attribute.prototype().width() );
//					
//					// nh 10/18/10: For some reason, the validators seem to use the OpenBase prototypes
//					if( "text".equalsIgnoreCase( attribute.prototype().name() ) ) {
//						
//						// nh 10/18/10: Have to disable this check for now because it seems to use the OpenBase prototype, which has a width
//						//if( attribute.prototype().width() > 0 )
//						//	throw new IllegalStateException( "PostgreSQL 'text' prototype must not have an External Width. (Results in syntax error.)" );
//						
//						if( attribute.overridesPrototypeDefinitionForKey( "width" ) )
//							throw new IllegalStateException( "PostgreSQL syntax error: 'text' data types must not have a width." );
//					}
//				}
//
//			};
//		}
//
//		@Override
//		public boolean reportsLowerCaseIdentifiers() { return true; }
//
//		@Override
//		public NSArray preUpdateInitializationStatements() {
//			NSMutableArray statements = new NSMutableArray();
//			EOSQLExpressionFactory expFactory = adaptor().expressionFactory();
//			statements.addObject( expFactory.expressionForString( "drop table tmp_table" ) );
//			statements.addObject( expFactory.expressionForString( "drop function id_max()" ) );
//
//            // FB has trouble deleting the EMAIL_HISTORY_ENTRY table when it is too large. Throwing
//            // this in for the non-FB DBs also, just in case (it's faster than deleting anyway).
//            try {
//	            DatabaseInfo dbInfo = DatabaseInfo.latestDatabaseInfo();
//	            if( dbInfo != null && dbInfo.version() != null && MDSUtils.compareVersions( dbInfo.version(), "10.2.0.41" ) < 0 ) {
//		            statements.addObject( expFactory.expressionForString( "DROP TABLE EMAIL_DATA_OBJECT" ) );
//		            statements.addObject( expFactory.expressionForString( "DROP SEQUENCE EMAIL_DATA_OBJECT_seq" ) );
//		            statements.addObject( expFactory.expressionForString( "DROP TABLE EMAIL_HISTORY_ENTRY" ) );
//		            statements.addObject( expFactory.expressionForString( "DROP SEQUENCE EMAIL_HISTORY_ENTRY_seq" ) );
//	            }
//            }
//            catch( Exception e ) {
//            	_logger.error( "Unable to check db for whether the EMAIL_HISTORY_ENTRY table needs to be dropped.", e );
//            }
//			return statements;
//		}
//
//		private static String _indexNameForAttribute( EOAttribute attribute ) {
//			String table = attribute.entity().externalName();
//			String fkColumn = attribute.columnName();
//			return table + "_" + fkColumn + "_idx";
//		}
//
//		private static IndexExpressionFactory.ExpressionFormatter _indexExpressionFormatter = new IndexExpressionFactory.ExpressionFormatter() {
//			public String expressionForAttribute( EOAttribute attribute ) {
//				String table = attribute.entity().externalName();
//				String fkColumn = attribute.columnName();
//				return "create index " + _indexNameForAttribute( attribute ) + " on " + table + " (" + fkColumn + ")";
//			}
//		};
//
//		@Override
//		public NSArray indexStatementsForRelationship( EORelationship relationship ) {
//			NSArray statements = NSArray.EmptyArray;	// nh 4/17/08: New PostgreSqlPlugin from Wonder appears to create the index statements for us
//			// NSArray statements = IndexExpressionFactory.indexStatementsForRelationship( adaptor(), relationship, _indexExpressionFormatter );
//			return statements;
//		}
//
//		@Override
//		public NSArray indexStatementsForAttribute( EOAttribute attribute ) {
//			NSArray statements = NSArray.EmptyArray;	 // nh 4/17/08: New PostgreSqlPlugin from Wonder appears to create the index statements for us
//			// NSArray statements = IndexExpressionFactory.indexStatementsForAttribute( adaptor(), attribute, _indexExpressionFormatter );
//			return statements;
//		}
//
//		@Override
//		public void updatePrimaryKeyGeneratorForEntity( EOAdaptorChannel adaptorChannel, String entityName ) {
//			EOEntity entity = MDSDatabaseUtils.entityForEntityNamed( entityName );
//			EOSQLExpressionFactory expFactory = adaptor().expressionFactory();
//			
//			NSMutableArray<EOSQLExpression> sqlStatements = new NSMutableArray<EOSQLExpression>();
//			String seqName = entity.primaryKeyRootName() + "_SEQ";
//			String primaryKeyName = MDSDatabaseUtils.pkColumnNameForEntity( entity );
//			sqlStatements.addObject( expFactory.expressionForString( "SELECT setval( '" + seqName + "', max(" + primaryKeyName + "), true ) FROM " + entity.externalName() + "" ) );
//			
//			MDSDatabaseUtils.executeSQLStatementsUsingAdaptorChannel( sqlStatements, false, adaptorChannel );
//		}
//
//
//	}
//
//    public MDSPostgreSQLPlugIn( JDBCAdaptor adaptor ) {
//        super( adaptor );
//    }
//
//	@Override
//	public NSDictionary jdbcInfo() {
//		return MDSDatabaseUtils.jdbcInfoForPlugIn( this, "models/JDBCInfoPostgreSQL.plist" );
//	}
//	
//	@Override
//    public EOSchemaSynchronizationFactory createSchemaSynchronizationFactory() {
//        return new MDSPostgreSQLSynchronizationFactory( super.adaptor() );
//
//    }
//
//    public static class MDSPostgreSQLSynchronizationFactory
//		extends PostgresqlSynchronizationFactory
//		implements MDSPlugInExtensionsProvider {
//			public MDSPostgreSQLSynchronizationFactory( EOAdaptor adaptor ) {
//				super( adaptor );
//			}
//
//			private MDSPlugInExtensions _plugInExtensions;
//			@Override
//			public MDSPlugInExtensions plugInExtensions() {
//				if( _plugInExtensions == null )
//					_plugInExtensions = new PlugInExtensions( adaptor() );
//				return _plugInExtensions;
//			}
//
//
//			@Override
//			public NSArray foreignKeyConstraintStatementsForRelationship( EORelationship relationship ) {
//				NSMutableArray statements = new NSMutableArray();
//				statements.addObjectsFromArray( super.foreignKeyConstraintStatementsForRelationship( relationship ) );
//				statements.addObjectsFromArray( plugInExtensions().indexStatementsForRelationship( relationship ) );
//				_logger.info("MDSPostgreSQLPlugIn.foreignKeyConstraintStatementsForRelationship() returning:\n" + statements );
//				return statements;
//			}
//
//
//			@Override
//			public NSArray createTableStatementsForEntityGroup( NSArray entities ) {
//				NSArray statements = super.createTableStatementsForEntityGroup( entities );
//				return statements;
//			}
//
//			/** @TypeInfo EOSQLExpression */
//			@Override
//			public NSArray statementsToInsertColumnForAttribute( EOAttribute attribute, EOSchemaGenerationOptions options ) {
//
//				EOSQLExpression sqlExpr = new PostgresqlExpression( attribute.entity() );
//
//				String sqlString = "ALTER TABLE " + attribute.entity().externalName()
//					+ " ADD COLUMN " + sqlExpr.sqlStringForAttribute( attribute )
//					+ " " + sqlExpr.columnTypeStringForAttribute( attribute );
//
//				sqlExpr.setStatement( sqlString );
//				return new NSArray( sqlExpr );
//			}
//
//			/** @TypeInfo EOSQLExpression */
//			@Override
//			public NSArray statementsToConvertColumnType( String columnName, String tableName,
//														  EOSchemaSynchronization.ColumnTypes type,
//														  EOSchemaSynchronization.ColumnTypes newType,
//														  EOSchemaGenerationOptions options ) {
//				// WARNING: This method will only enlarge a column to the width specified in newType
//				EOSQLExpression sqlExpr = null;
//				if( "varchar".equalsIgnoreCase( type.name() ) && "text".equalsIgnoreCase( newType.name() ) ) {
//					// allow modification of varchars into text
//					sqlExpr = new MySQLPlugIn.MySQLExpression( null );
//					String sqlString = "ALTER TABLE " + tableName
//						+ " ALTER COLUMN " + columnName
//						+ " TYPE " + newType.name();
//
//					sqlExpr.setStatement( sqlString );
//				}
//				else if( type.name().equalsIgnoreCase( "serial" ) && newType.name().equalsIgnoreCase( "int4" ) )  {
//					// nh 4/17/08: ignore; new driver appears to report primary keys as 'serial' even though they were created using 'int4'
//					_logger.info( "Ignored. 'serial' and 'int4' are compatible." );
//				}
//				else if( ! (type.name().equalsIgnoreCase( newType.name() ) ) ) {
//					throw new RuntimeException("WARNING: New column type " + new MDSDBUpdater.MDSColumnTypes( newType ) + " is different type than "
//											   + "existing column type " + new MDSDBUpdater.MDSColumnTypes( type ) + "; "
//											   + " update not supported by MDSPostgreSQLPlugIn.");
//				}
//				else {
//					if( type.name().equalsIgnoreCase("varchar") ) {
//						sqlExpr = new PostgresqlExpression( null );
//						String sqlString = "ALTER TABLE " + tableName
//							+ " ALTER COLUMN " + columnName
//							+ " TYPE varchar(" + newType.width() + ")";
//
//						sqlExpr.setStatement( sqlString );
//					}
//					if( type.name().equalsIgnoreCase("numeric") ) {
//						sqlExpr = new PostgresqlExpression( null );
//						String sqlString = "ALTER TABLE " + tableName
//							+ " ALTER COLUMN " + columnName
//							+ " TYPE numeric(" + newType.precision() + "," + newType.scale() + ")";
//
//						sqlExpr.setStatement( sqlString );
//					}
//				}
//
//				NSArray statements = null;
//				if( sqlExpr != null )
//					statements = new NSArray( sqlExpr );
//				return statements;
//
//			}
//
//
//		}

}
