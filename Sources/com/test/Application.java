package com.test;

import er.extensions.appserver.ERXApplication;
import er.extensions.eof.ERXObjectStoreCoordinatorSynchronizer;
import er.extensions.eof.ERXObjectStoreCoordinatorSynchronizer.SynchronizerSettings;

public class Application extends ERXApplication {
	public static void main(String[] argv) {
		ERXApplication.main(argv, Application.class);
	}

	public Application() {
		ERXApplication.log.info("Welcome to " + name() + " !");

       	// Turning the synchronizer on seems to trigger the memory leak.
       	ERXObjectStoreCoordinatorSynchronizer.synchronizer().setDefaultSettings( new SynchronizerSettings( true, true, true, true ) );
        
		setAllowsConcurrentRequestHandling(true);	
	}
}
