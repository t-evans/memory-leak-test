package com.test;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOMessage;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSTimeZone;
import com.webobjects.foundation._NSUtilities;

import er.extensions.appserver.ERXApplication;
import er.extensions.appserver.ERXMessageEncoding;
import er.extensions.appserver.ERXResponseRewriter;
import er.extensions.appserver.ERXResponseRewriter.Resource;
import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXEntityClassDescription;
import er.extensions.eof.ERXObjectStoreCoordinatorPool;
import er.extensions.eof.ERXObjectStoreCoordinatorSynchronizer;
import er.extensions.eof.ERXObjectStoreCoordinatorSynchronizer.SynchronizerSettings;

public class Application extends ERXApplication {
	public static void main(String[] argv) {
		ERXApplication.main(argv, Application.class);
	}

	public Application() {
		ERXApplication.log.info("Welcome to " + name() + " !");
		
        NSNotificationCenter.defaultCenter().addObserver( this, new NSSelector<Object>( "applicationDidFinishLaunching", new Class[] { NSNotification.class } ), WOApplication.ApplicationDidFinishLaunchingNotification, null );
        
		setAllowsConcurrentRequestHandling(true);	
	}

	public void applicationDidFinishLaunching( NSNotification notification ) {
		
       	ERXObjectStoreCoordinatorSynchronizer.initialize();
       	ERXObjectStoreCoordinatorPool.initialize();

       	ERXEntityClassDescription.registerDescription();
       	
       	ERXObjectStoreCoordinatorSynchronizer.synchronizer().setDefaultSettings( new SynchronizerSettings( true, true, true, true ) );
	}
}
