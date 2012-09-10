//
//  MDSFrontBasePlugIn.java
//
//  Created by Nathan Hadfield on Mon Dec 02 2002.
//  Copyright (c) 2002 MacsDesign Studio. All rights reserved.
//

//
//  To use this plugin, either make the following call
//
//	 com.webobjects.jdbcadaptor.JDBCPlugIn.setPlugInNameForSubprotocol( "MDSFrontBasePlugIn", "openbase" );
//
//  or set the 'plugin' parameter of the model's connection dictionary to 'MDSFrontBasePlugIn'.
//
//  NOTE: If set, the 'plugin' parameter of the connection dictionary will override the class set by
//  JDBCPlugIn.setPlugInNameForSubprotocol
//

package com.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.FieldPosition;
import java.util.Collection;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;

//import com.macsdesign.whd.AppProperties;
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
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSPropertyListSerialization;
import com.webobjects.foundation.NSTimeZone;
import com.webobjects.foundation.NSTimestampFormatter;
import com.webobjects.jdbcadaptor.FrontBasePlugIn;
import com.webobjects.jdbcadaptor.JDBCAdaptor;
import com.webobjects.jdbcadaptor.JDBCContext;
import com.webobjects.jdbcadaptor._FrontBasePlugIn;

public class MDSFrontBasePlugIn extends _FrontBasePlugIn {

	public MDSFrontBasePlugIn(JDBCAdaptor jdbcadaptor) {
		super(jdbcadaptor);
		// TODO Auto-generated constructor stub
	}
	
//	private static Logger _logger = LoggerUtils.getLogger( MDSFrontBasePlugIn.class );
//	
//	public static class PlugInExtensions extends MDSPlugInExtensions {
//		
//		public PlugInExtensions( EOAdaptor adaptor ) {
//			super( adaptor );
//		}
//
//		@Override
//		public EOSchemaGenerationOptions schemaCreationOptions() {
//			EOSchemaGenerationOptions opts = new EOSchemaGenerationOptions();
//			opts.setCreateTables( true );
//			opts.setPrimaryKeyConstraints( true );
//			opts.setForeignKeyConstraints( true );
//			return opts;
//        }
//		
//		@Override
//        public EOSchemaGenerationOptions columnInsertionOptions() {
//			EOSchemaGenerationOptions opts = new EOSchemaGenerationOptions();
//			opts.setManageForeignKeyConstraints( true );
//			opts.setManagePrimaryKeyConstraints( true );
//			return opts;
//        }
//		
//		@Override
//        public EOSchemaGenerationOptions columnConversionOptions() {
//			EOSchemaGenerationOptions opts = new EOSchemaGenerationOptions();
//			opts.setManageForeignKeyConstraints( true );
//			opts.setManagePrimaryKeyConstraints( true );
//			return opts;
//        }
//		
//		@Override
//		public boolean columnConversionRequiresIndexInfo() {
//			return false;
//		}
//		
//		@Override
//		public void updatePrimaryKeyGeneratorForEntity( EOAdaptorChannel adaptorChannel, String entityName ) {
//            // set fb for "STATUS_TYPE" ("STATUS_TYPE_ID")
//			
//            EOEntity entity = MDSDatabaseUtils.entityForEntityNamed( entityName );
//			
//            String sqlStr = "set unique for \"" + entity.externalName() + "\" (\""
//                + MDSDatabaseUtils.pkColumnNameForEntity( entity ) + "\")";
//			
//            EOSQLExpression sqlExpr = new FrontBasePlugIn.FrontbaseExpression( entity );
//            sqlExpr.setStatement( sqlStr );
//            MDSDatabaseUtils.executeSQLStatementsUsingAdaptorChannel( new NSArray<EOSQLExpression>( sqlExpr ), false, adaptorChannel );
//        }
//		
//		private static IndexExpressionFactory.ExpressionFormatter _indexExpressionFormatter = new IndexExpressionFactory.ExpressionFormatter() {
//            public String expressionForAttribute( EOAttribute attribute ) {
//                String table = attribute.entity().externalName();
//                String fkColumn = attribute.columnName();
//				
//                return "CREATE INDEX ON \"" + table + "\" (\"" + fkColumn + "\")";
//            }
//        };
//		
//		@Override
//		public NSArray indexStatementsForRelationship( EORelationship relationship ) {
//            //NSArray statements = IndexExpressionFactory.indexStatementsForRelationship( adaptor(), relationship, _indexExpressionFormatter );
//            NSArray statements = new NSArray();
//            return statements;
//		}
//		
//		@Override
//		public NSArray indexStatementsForAttribute( EOAttribute attribute ) {
//			NSArray statements = IndexExpressionFactory.indexStatementsForAttribute( adaptor(), attribute, _indexExpressionFormatter );
//			return statements;
//		}
//
//		@Override
//		public NSArray preUpdateInitializationStatements() {
//            NSMutableArray statements = new NSMutableArray();
//            EOSQLExpressionFactory expFactory = adaptor().expressionFactory();
//            // IndexInfo gets created and deleted when querying indexes. If something goes wrong
//            // and it doesn't get deleted, then subsequent index queries will fail and the app 
//            // will attempt to recreate them, resulting in duplicate indexes. After several updates
//            // the database will run out of index capacity.
//            statements.addObject( expFactory.expressionForString( "DROP VIEW IndexInfo RESTRICT" ) );
//
//
//            // If the EMAIL_HISTORY_ENTRY table is large, the "delete from..." statemement fails in
//            // FB (even via front base manager). Need to drop the that table and the EMAIL_DATA_OBJECT
//            // table.
//            try {
//	            DatabaseInfo dbInfo = DatabaseInfo.latestDatabaseInfo();
//	            if( dbInfo != null && dbInfo.version() != null && MDSUtils.compareVersions( dbInfo.version(), "10.2.0.41" ) < 0 ) {
//		            statements.addObject( expFactory.expressionForString( "DROP TABLE EMAIL_DATA_OBJECT RESTRICT" ) );
//		            statements.addObject( expFactory.expressionForString( "DROP TABLE EMAIL_HISTORY_ENTRY RESTRICT" ) );
//	            }
//            }
//            catch( Exception e ) {
//            	_logger.error( "Unable to check db for whether the EMAIL_HISTORY_ENTRY table needs to be dropped.", e );
//            }
//            return statements;
//        }
//		
//		@Override
//		public boolean reportsLowerCaseIdentifiers() { return false; }
//        public ModelValidator.Validator modelValidator() {
//            return null;
//        }
//		
//		
//		private NSDictionary _extractTableInfoForEntity( EOAdaptorChannel adaptorChannel, EOEntity entity ) throws SQLException {
//			//EOAdaptorChannel channel = MDSDatabaseUtils.adaptorChannelForObjectStoreCoordinatorAndModel( osc, entity.model() );
//			Connection connection = ((JDBCContext) adaptorChannel.adaptorContext()).connection();
//			
//			//_logger.info( "Extracting table info for table " + entity.externalName() + "..." );
//			Statement statement = connection.createStatement();
//			statement.execute( "extract table \"" + entity.externalName() + "\"" );
//			String msg = ((com.frontbase.jdbc.FBJStatement) statement).getMessage();
//
//			//_logger.info( "Retrieved table info:\n" + msg );
//			
//			// nh 4/7/06: The current FB Driver appears to have a bug that causes cache hit and lookup values
//			// to be returned as unquoted negatives (if they exceed a certain limit?). This breaks
//			// NSPropertyListSerialization.dictionaryForString(), which expects negative values
//			// to be quoted. To fix it, we parse through the string and quote any unquoted negative
//			// numbers.
//			msg = msg.replaceAll( "=\\s*(-\\d+)\\s*;", "= \"$1\";" );
//
//			return NSPropertyListSerialization.dictionaryForString( msg );
//		}
//		
//		@Override
//		public NSArray statementsToMakeColumnNullable( EOAdaptorChannel adaptorChannel, EOAttribute attribute ) {
//			
//			NSMutableArray statements = new NSMutableArray();
//			
//			NSDictionary infoDict;
//			try {
//				infoDict = _extractTableInfoForEntity( adaptorChannel, attribute.entity() );
//			}
//			catch( SQLException e ) {
//				throw new RuntimeException( e );
//			}
//			
//			String tableName = attribute.entity().externalName();
//			if( infoDict == null )
//				throw new RuntimeException( "Unable to extract meta data for table '" + tableName + "'" );
//			
//			// look through all CHECK constraints for one on the given attribute
//			NSArray checks = (NSArray) infoDict.objectForKey( "CHECK" );
//			//_logger.info( "Checks: " + checks );
//			if( checks != null ) {
//				boolean colFound = false;
//				for( Enumeration checkEnum = checks.objectEnumerator(); !colFound && checkEnum.hasMoreElements(); ) {
//					NSDictionary check = (NSDictionary) checkEnum.nextElement();
//					String cond = (String) check.objectForKey( "COND" );
//					String constraintName = (String) check.objectForKey( "NAME" );
//					
//					if( cond != null && constraintName != null ) {
//						Matcher matcher = _checkNotNullPattern.matcher( cond );
//						if( matcher.matches() ) {
//							String colName = matcher.group(1);
//							if( colName.equalsIgnoreCase( attribute.columnName() ) ) {
//								colFound = true;
//								EOSQLExpressionFactory expFactory = adaptor().expressionFactory();
//								statements.addObject( expFactory.expressionForString( "alter table \"" + tableName + "\""
//																					  + " drop constraint \"" + constraintName + "\" cascade" ) );
//
//							}
//						}
//					}
//				}
//				if( ! colFound ) {
//					_logger.warn( "Could not obtain name of not-null constraint for column \"" + tableName + "\".\"" + attribute.columnName() + "\"" );
//				}
//			}
//			
//			return statements;
//		}
//        
//		private static Pattern _checkNotNullPattern;
//		static {
//			try {
//				// COND = "\"CLIENT_ID\" IS NOT NULL"
//				_checkNotNullPattern = Pattern.compile( "(?i)\"(\\S+?)\" IS NOT NULL" );
//			}
//			catch( PatternSyntaxException e ) {
//				throw new RuntimeException( e );
//			}
//		}
//		
//		@Override
//		public NSArray nullableColumnsDetectedForEntity( EOAdaptorChannel adaptorChannel, EOEntity entity ) throws SQLException {
//			
//			NSDictionary infoDict = _extractTableInfoForEntity( adaptorChannel, entity );
//			//_logger.info( "Table data: " + infoDict );
//			NSMutableArray columns = new NSMutableArray();
//			NSArray columnInfoArray = (NSArray) infoDict.objectForKey( "COLUMNS" );
//			if( columnInfoArray != null ) {
//				for( Enumeration colInfoEnum = columnInfoArray.objectEnumerator(); colInfoEnum.hasMoreElements(); ) {
//					NSDictionary colInfo = (NSDictionary) colInfoEnum.nextElement();
//					String colName = (String) colInfo.objectForKey( "NAME" );
//					if( colName != null )
//						columns.addObject( colName );
//				}
//			}
//			
//			NSArray checks = (NSArray) infoDict.objectForKey( "CHECK" );
//			//_logger.info( "Checks: " + checks );
//			if( checks != null ) {
//				for( Enumeration checkEnum = checks.objectEnumerator(); checkEnum.hasMoreElements(); ) {
//					NSDictionary check = (NSDictionary) checkEnum.nextElement();
//					String cond = (String) check.objectForKey( "COND" );
//					String constraintName = (String) check.objectForKey( "NAME" );
//					//_logger.info( "Constraint " + constraintName + ": " + cond );
//					// COND = "\"CLIENT_ID\" IS NOT NULL"
//					
//					if( cond != null && constraintName != null ) {
//						Matcher matcher = _checkNotNullPattern.matcher( cond );
//						if( matcher.matches() ) 
//							columns.removeObject( matcher.group(1) );
//					}
//				}
//			}
//			
//			return columns;
//		}
//		
//	}
//	
//    /*
//    public static class MDSFrontBaseExpression extends FrontBasePlugin.FrontbaseExpression {
//        public MDSFrontBaseExpression( EOEntity entity ) {
//            super( entity );
//        }
//        
//        public String assembleSelectStatementWithAttributes( NSArray attributes,
//                                                             boolean lock,
//                                                             EOQualifier qualifier,
//                                                             NSArray fetchOrder,
//                                                             String selectString,
//                                                             String columnList,
//                                                             String tableList,
//                                                             String whereClause,
//                                                             String joinClause,
//                                                             String orderByClause,
//                                                             String lockClause ) {
//            
//            //System.out.println( "WHERE = " + whereClause );
//            //whereClause = whereClause.replaceAll( "= NULL", "is NULL" );
//			//System.out.println("  changed to " + whereClause );
//            String clause = super.assembleSelectStatementWithAttributes( attributes,
//                                                         lock,
//                                                         qualifier,
//                                                         fetchOrder,
//                                                         selectString,
//                                                         columnList,
//                                                         tableList,
//                                                         whereClause,
//                                                         joinClause,
//                                                         orderByClause,
//                                                         lockClause );
//            //System.out.println("statement = " + clause );
//            return clause;
//        }
//    }
//	 */
//	@Override
//    public Class defaultExpressionClass() {
//        return MDSFrontBaseSQLExpression.class;
//    }
//	
//	/*
//	    
//	public static class MDSFrontBaseExpressionFactory extends JDBCExpressionFactory {
//		public MDSFrontBaseExpressionFactory( EOAdaptor adaptor ) {
//			super( adaptor );
//		}
//		
//		public EOSQLExpression createExpression( EOEntity entity ) {
//			return new MDSFrontBaseSQLExpression( entity );
//		}
//	}
//	 */
//	
//	public static class MDSFrontBaseSQLExpression extends FrontBasePlugIn.FrontbaseExpression {
//		public MDSFrontBaseSQLExpression( EOEntity entity ) {
//			super( entity );
//			//_logger.info( "MDSFrontBaseSQLExpression( " + (entity == null ? "null" : entity.name()) + " )..." );
//		}
//		/*
//		
//		public String sqlStringForQualifier( EOQualifierSQLGeneration qualifier ) {
//			String str = super.sqlStringForQualifier( qualifier );
//			_logger.info( "MDSFrontBaseSQLExpression.sqlStringForQualifier(): " + str );
//			return str;
//		}
//		
//		public String sqlStringForKeyValueQualifier(EOKeyValueQualifier qualifier) {
//			String str = super.sqlStringForKeyValueQualifier( qualifier );
//			_logger.info( "MDSFrontBaseSQLExpression.sqlStringForKeyValueQualifier(): " + str, new Exception( "STACK TRACE" ) );
//			return str;
//		}
//		
//		public String sqlStringForKeyComparisonQualifier(EOKeyComparisonQualifier qualifier) {
//			String str = super.sqlStringForKeyComparisonQualifier( qualifier );
//			_logger.info( "MDSFrontBaseSQLExpression.sqlStringForKeyComparisonQualifier(): " + str );
//			return str;
//		}
//		 */
//		
//		@Override
//		public String formatValueForAttribute( Object obj, EOAttribute eoattribute ) {
//			
//			// Format timestamps using UTC. Depends upon the JDBC connection using UTC for the session's time zone.
//			if( obj != null && obj != NSKeyValueCoding.NullValue ) {
//				if(eoattribute.valueFactoryMethod() != null && eoattribute.valueFactoryMethod().implementedByObject(obj) && eoattribute.adaptorValueConversionMethod().implementedByObject(obj) )
//                    obj = eoattribute.adaptorValueByConvertingAttributeValue(obj);
//				
//				//_logger.info( "MDSFrontBasePlugIn.formatValueForAttribute(): eoattribute.name = " + eoattribute.name() + " eoattribute.externalType = " + eoattribute.externalType() );
//                switch(FrontBasePlugIn.internalTypeForExternal(eoattribute.externalType())) {
//					case 16: // TIMESTAMP
//					{
//						NSTimestampFormatter f = new NSTimestampFormatter("%Y-%m-%d %H:%M:%S.%F");
//						f.setDefaultFormatTimeZone( NSTimeZone.getGMT() );
//						StringBuffer time = new StringBuffer("TIMESTAMP '");
//						f.format(obj, time, new FieldPosition(0));
//						time.append("'");
//						return time.toString();
//					}
//						
//					default:
//						return super.formatValueForAttribute( obj, eoattribute );
//				}
//					
//			}
//			else 
//				return super.formatValueForAttribute( obj, eoattribute );
//		}
//		
//	}
//	
//    public MDSFrontBasePlugIn( JDBCAdaptor adaptor ) {
//        super( adaptor );
//		//adaptor.setExpressionClassName( MDSFrontBaseSQLExpression.class.getName(), adaptor.getClass().getName() );
//    }
//	 
//	@Override
//	public NSDictionary jdbcInfo() {
//		/*NSDictionary jdbcInfo = super.jdbcInfo();
//		System.out.println("JDBCINFO = " + jdbcInfo );
//		return jdbcInfo;
//		*/
//		
//		return MDSDatabaseUtils.jdbcInfoForPlugIn( this, "models/JDBCInfoFrontBase.plist" );
//	}
//
//	@Override
//    public EOSchemaSynchronizationFactory createSchemaSynchronizationFactory() {
//        return new MDSFrontBaseSynchronizationFactory( super.adaptor() );
//    }
//    
//	/*
//	public EOSQLExpressionFactory createExpressionFactory() {
//		_logger.info( "MDSFrontBasePlugIn.createExpressionFactory()..." );
//		return new MDSFrontBaseExpressionFactory( adaptor() );
//	}
//	 */
//	
//    public static class MDSFrontBaseSynchronizationFactory
//    extends FrontBasePlugIn.FrontbaseSynchronizationFactory
//    implements MDSPlugInExtensionsProvider {
//        public MDSFrontBaseSynchronizationFactory( EOAdaptor adaptor ) {
//            super( adaptor );
//        }
//
//		
//		private MDSPlugInExtensions _plugInExtensions;
//		@Override
//		public MDSPlugInExtensions plugInExtensions() {
//			if( _plugInExtensions == null )
//				_plugInExtensions = new PlugInExtensions( adaptor() );
//			return _plugInExtensions;
//		}
//
//		@Override
//        public NSArray foreignKeyConstraintStatementsForRelationship( EORelationship relationship ) {
//            NSMutableArray statements = new NSMutableArray();
//            statements.addObjectsFromArray( super.foreignKeyConstraintStatementsForRelationship( relationship ) );
//            //statements.addObjectsFromArray( indexStatementsForRelationship( relationship ) );
//            return statements;
//        }
//         
//
//		@Override
//		public NSArray createTableStatementsForEntityGroup( NSArray entities ) {
//			NSArray statements = super.createTableStatementsForEntityGroup( entities );
//			return statements;
//		}
//         
//        /** @TypeInfo EOSQLExpression */
//		@Override
//        public NSArray schemaCreationStatementsForEntities( NSArray entities, EOSchemaGenerationOptions options ) {
//
//            // override primary keys to start at 0 instead of 1000000
//            NSMutableArray statements = new NSMutableArray( super.schemaCreationStatementsForEntities( entities, options ) );
//
//            for( int i=0, iCount=entities.count(); i < iCount; ++i ) {
//                EOEntity entity = (EOEntity) entities.objectAtIndex(i);
//                String sqlStr = "SET UNIQUE=0 FOR \"" + entity.externalName() + "\"";
//                EOSQLExpression sqlExpr = new FrontBasePlugIn.FrontbaseExpression( entity );
//                sqlExpr.setStatement( sqlStr );
//                statements.addObject( sqlExpr );
//            }
//
//            return statements;
//        }
//
//        /** @TypeInfo EOSQLExpression */
//        @Override
//        public NSArray statementsToInsertColumnForAttribute( EOAttribute attribute, EOSchemaGenerationOptions options ) {
//
//            EOSQLExpression sqlExpr = new FrontBasePlugIn.FrontbaseExpression( attribute.entity() );
//
//            String sqlString = "ALTER TABLE \"" + attribute.entity().externalName()
//                + "\" ADD COLUMN " + sqlExpr.sqlStringForAttribute( attribute )
//                + " " + sqlExpr.columnTypeStringForAttribute( attribute );
//
//            sqlExpr.setStatement( sqlString );
//            return new NSArray( sqlExpr );
//        }
//
//        /** @TypeInfo EOSQLExpression */
//        @Override
//        public NSArray statementsToConvertColumnType( String columnName, String tableName,
//                                                      EOSchemaSynchronization.ColumnTypes type,
//                                                      EOSchemaSynchronization.ColumnTypes newType,
//                                                      EOSchemaGenerationOptions options ) {
//            // WARNING: This method will only enlarge a column to the width specified in newType
//            EOSQLExpression sqlExpr = null;
//            if( ! (type.name().equalsIgnoreCase( newType.name() ) ) ) {
//                throw new RuntimeException("WARNING: New column type is different;"
//                                           + " update not supported by MDSFrontBasePlugIn.");
//            }
//            else {
//                if( type.name().equalsIgnoreCase("VARCHAR") ) {
//                    sqlExpr = new FrontBasePlugIn.FrontbaseExpression( null );
//                    String sqlString = "ALTER COLUMN \"" + tableName
//                        + "\".\"" + columnName + "\" to "
//                        + newType.name() + "(" + newType.width() +")";
//
//                    sqlExpr.setStatement( sqlString );
//                }
//                if( type.name().equalsIgnoreCase( "DECIMAL" ) ) {
//                    sqlExpr = new FrontBasePlugIn.FrontbaseExpression( null );
//                    String sqlString = "ALTER COLUMN \"" + tableName
//                    	+ "\".\"" + columnName + "\" to "
//                        + newType.name() + " (" + newType.precision() + "," + newType.scale() + ")";
//
//                    sqlExpr.setStatement( sqlString );
//                }
//                /* nh 3/13/03: no need for precision checking of non-string columns
//                else {
//                    throw new RuntimeException("Conversion not supported by MDSFrontBasePlugIn: "
//                                               + new MDSDBUpdater.MDSColumnTypes( type )
//                                               + " to " + new MDSDBUpdater.MDSColumnTypes( newType ) );
//
//                }
//                */
//            }
//
//            NSArray statements = null;
//            if( sqlExpr != null )
//                statements = new NSArray( sqlExpr );
//            return statements;
//
//        }
//
//    }

}

