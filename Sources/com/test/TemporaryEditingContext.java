package com.test;


import org.apache.log4j.Logger;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOObjectStoreCoordinator;

import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXObjectStoreCoordinator;

public class TemporaryEditingContext {

	private static Logger _logger = Logger.getLogger( TemporaryEditingContext.class );
	
	public static abstract class Client {
		abstract public void executeWithEditingContext( EOEditingContext ec ) throws Throwable;
	}
	
	public static void execute( EOObjectStoreCoordinator osc, Client client ) throws Throwable {
		
		EOEditingContext stagingEc = ERXEC.newEditingContext( osc );
		try {
			stagingEc.lock();
			client.executeWithEditingContext( stagingEc );
		}
		finally {
			try {
				stagingEc.unlock();
				stagingEc.dispose();
				stagingEc = null;
			}
			catch( Exception e ) {
				_logger.error( "Error while unlocking temporary ec: " + e, e );
			}
		}
	}
	
	public static void executeWithDedicatedOSC( boolean shouldClose, String ecName, Client client ) throws Throwable {
		
		ERXObjectStoreCoordinator osc = new ERXObjectStoreCoordinator( shouldClose );
		try {
			osc.lock();
			EOEditingContext stagingEc = ERXEC.newEditingContext( osc );
			try {
				stagingEc.lock();
				client.executeWithEditingContext( stagingEc );
			}
			finally {
				try {
					stagingEc.unlock();
					stagingEc.dispose();
					stagingEc = null;
				}
				catch( Exception e ) {
					_logger.error( "Error while unlocking temporary ec: " + e, e );
				}
			}
		}
		finally {
			osc.unlock();
			osc.dispose();
			osc = null;
		}
	}
}

