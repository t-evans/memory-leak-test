//
//  MDSApplication.java
//  Helpdesk
//
//  Created by Nathan Hadfield on 5/18/07.
//  Copyright (c) 2007 __MyCompanyName__. All rights reserved.
//

package com.test;

import java.net.URL;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOMessage;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WORequestHandler;
import com.webobjects.appserver._private.WOComponentDefinition;
import com.webobjects.eoaccess.EODatabase;
import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eoaccess.MDSEntityDependencyOrderingDelegate;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEventCenter;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSNumberFormatter;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSTimeZone;
import com.webobjects.foundation._NSUtilities;

import er.extensions.appserver.ERXApplication;
import er.extensions.appserver.ERXMessageEncoding;
import er.extensions.appserver.ERXResponseRewriter;
import er.extensions.appserver.ERXResponseRewriter.Resource;
import er.extensions.appserver.ERXWOContext;
import er.extensions.eof.ERXConstant;
import er.extensions.eof.ERXDatabaseContextDelegate;
import er.extensions.eof.ERXDatabaseContextMulticastingDelegate;
import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXEntityClassDescription;
import er.extensions.eof.ERXObjectStoreCoordinatorPool;
import er.extensions.eof.ERXObjectStoreCoordinatorSynchronizer;
import er.extensions.eof.ERXObjectStoreCoordinatorSynchronizer.SynchronizerSettings;

public class MDSApplication extends ERXApplication {

	private static Logger _logger = Logger.getLogger( MDSApplication.class );

	public static final String KEY_IS_DB_UPDATE_IN_PROGRESS = "isDbUpdateInProgress";
	public static final String KEY_DB_UPDATE_SESSION = "dbUpdateSession";


	public static boolean _isAppConstructed = false;
    public static boolean _isRunningInMonitor = false;
    
    private boolean isSqlDebugMode = false;
    private boolean isTestMode = true;
    private boolean disableSnapshotRefCounting = false;
	private int dbConnectionCount;
	private boolean isWindows = false;
	private boolean isServlet = false;

	
	public static String[] _argv = null;
	public static void main( String args[], Class clazz ) {
		//Logger.getLogger(ERXModelGroup.class).setLevel( Level.DEBUG );

		_isRunningInMonitor = true;
		ERXApplication.main( args, clazz );
		_argv = args;
	}

	public MDSApplication() {
		super();

		// nh 1/21/09: WO says EOEventLoggingPassword can be set from command line, but it can't; have to fix it with this
		// (see http://www.mail-archive.com/webobjects-dev@lists.apple.com/msg19485.html)
		EOEventCenter.setPassword(System.getProperty("EOEventLoggingPassword"));
		
//		LoggerUtils.initLoggers();
		//Logger.getRootLogger().setLevel( Level.DEBUG );
		
		setSqlDebug( isSqlDebugMode );

        // Activate EOF's missing lock logging
        if( isTestMode ) {
        	NSLog.allowDebugLoggingForGroups(NSLog.DebugGroupMultithreading);
        	NSLog.debug.setAllowedDebugLevel( NSLog.DebugLevelInformational );
        	NSLog.allowDebugLoggingForGroups( NSLog.DebugGroupDatabaseAccess );
        	
        	// nh 1/27/09: Setting these properties here doesn't seem to do the trick; set them in the run configuration instead
        	// by copy/pasting this into the Java VM args:
        	//
        	//   -Der.component.clickToOpen=true -Der.extensions.ERXApplication.developmentMode=true -Dognl.debugSupport=true
        	//
//        	String clickToOpen = System.getProperty( "er.component.clickToOpen" );
//        	if ( MDSUtils.isEmptyOrNullString(clickToOpen) )
//        		System.setProperty( "er.component.clickToOpen", "true" );
//
//        	String devMode = System.getProperty( "er.extensions.ERXApplication.developmentMode" );
//        	if ( MDSUtils.isEmptyOrNullString(devMode) )
//        		System.setProperty( "er.extensions.ERXApplication.developmentMode", "true" );
//        	
//        	String debugSupport = System.getProperty( "ognl.debugSupport" );
//        	if ( MDSUtils.isEmptyOrNullString(debugSupport) )
//        		System.setProperty( "ognl.debugSupport", "true" );	// enable click-to-debug (doesn't seem to work; probably requires the WOOgnl framework
        }
        else {
        	// nh 9/10/10: Turning development mode off messes up code that alters filepaths based on
        	// whether we're running in dev mode (for bundle-less builds)
            //System.setProperty( "er.extensions.ERXApplication.developmentMode", "false" ); // This is mostly just to turn off the obnoxious ERXFileNotificationCenter debug statements
        }
        
		NSNumberFormatter.setDefaultLocalizesPattern( true );
		//com.webobjects.eoaccess.EODatabase.disableSnapshotRefCounting();		 // is this really necessary? prevents snapshots from being released...

		//System.setProperty( "er.extensions.ERXModelGroup.patchModelsOnLoad", "true" );
		
        System.setProperty( "java.awt.headless", "true" );  // does this really do anything at this point?

        // Fixes double connection bug from JDBCInfo (as explained in Hidden Wonder Gems presentation at WOWODC 2010)
        System.setProperty( "er.extensions.ERXJDBCAdaptor.className", "er.extensions.jdbc.ERXJDBCAdaptor");
        
        // Automatically update inverse relationships
        // nh 1/7/12: Currently this is incompatible with our Qualifiers because we create qualifiers that aren't in any ECs, 
        // so when we set certain properties, this causes the following error (triggered when going to advanced Client Search):
        //  
        // java.lang.RuntimeException: You crossed editing context boundaries attempting to set the 'techCreator' relationship of null (which is not in an editing context) to Jonathan Lew [admin] (in EC [clientQualifierBuilderEc] (created: 2012-01-07 14:55:12 Etc/GMT) com.macsdesign.util.MDSEditingContext@ef82188).
    	// at er.extensions.eof.ERXGenericRecord.checkMatchingEditingContexts(ERXGenericRecord.java:1451)
    	// at er.extensions.eof.ERXGenericRecord$InverseRelationshipUpdater.takeStoredValueForKey(ERXGenericRecord.java:1607)
    	// at er.extensions.eof.ERXGenericRecord.takeStoredValueForKey(ERXGenericRecord.java:1392)
        System.setProperty( "er.extensions.ERXEnterpriseObject.updateInverseRelationships", "false" );		
        
        // Properties to set up auto locking/unlocking -- http://lists.apple.com/archives/Webobjects-dev/2007/May/msg00578.html
        System.setProperty( "er.extensions.ERXEC.safeLocking", "true" );
        System.setProperty( "er.extensions.ERXAdaptorChannelDelegate.enabled", "true" );
        
        // Too late to set these properties at this point, as ERXExtensions registers the database context class in our parent constructor.
        // Possibly we could register for the ERXApplication.ApplicationDidCreateNotification.
//        System.setProperty( "er.extensions.ERXDatabaseContext.activate", "true" );
//        System.setProperty( "er.extensions.ERXDatabaseContext.className", MDSDatabaseContext.class.getName() );
        EODatabaseContext.setContextClassToRegister( MDSDatabaseContext.class );
        
        if( isTestMode )
        	System.setProperty( "er.extensions.ERXEC.traceOpenLocks", "true" );
        
        System.setProperty( "er.extensions.ERXEC.defaultAutomaticLockUnlock", "true" );
        
        // nh 1/3/11: Ran into a deadlock issue that seemed to be related to the ERXObjectStoreCoordinatorSynchronizer waiting on a lock for
        // the shared editing context. Hoping that setting this to true might resolve this.
        System.setProperty( "er.extensions.ERXEC.useSharedEditingContext", "false" ); // We don't use the shared ec (not sure exactly what this flag does)
        System.setProperty( "WOAcceptMalformedCookies", "YES");
        
        // TODO: nh 1/15/09: Was playing with these parameters trying to figure out a way to make the model 
        // validators work in HelpDeskDBLoader. No luck yet.
        //// For this property to have effect, it must be applied before ERXModelGroup is loaded 
        //System.setProperty( "er.extensions.ERXModelGroup.patchModelsOnLoad", "true" );
        //
        //if( isTestMode )		// wait until we've run the model validator in HelpDeskDBLoader to flatten the relationships; otherwise, we lose the prototype info
        //	System.setProperty( "er.extensions.ERXModelGroup.flattenPrototypes", "false" );
        
        // We shouldn't need to set er.extensions.ERXRaiseOnMissingEditingContextDelegate  to false. 
        // A null editing context delegate means that the editing context
        // was created using "new EOEditingContext()" rather than the utility methods ERXEC.newEditingContext() or
        // MDSEditingContext.newEditingContext(). However, we set it to false just to prevent an error from occurring in case
        // we do have this problem. (Wonder will provide the default delegate automatically, with a warning.) Set this to
        // true to get a stack trace when the missing delegate is encountered.
        System.setProperty( "er.extensions.ERXRaiseOnMissingEditingContextDelegate", "false" );
        
		if( maxCoordinators() > 0 )
				System.setProperty( "er.extensions.ERXObjectStoreCoordinatorPool.maxCoordinators", Integer.toString( maxCoordinators() ) );	// This should be configurable
		
		// MDSBrowser fixes a bug in ERXBasicBrowser that made version9() return true if the browser was actually version 8
		System.setProperty( "er.extensions.ERXBrowserFactory.BrowserClassName", "com.macsdesign.util.MDSBrowser" );
		
        NSNotificationCenter.defaultCenter().addObserver( this, new NSSelector<Object>( "applicationDidFinishLaunching", new Class[] { NSNotification.class } ), WOApplication.ApplicationDidFinishLaunchingNotification, null );
        
        // nh 2/21/12: WO 54 has a bug that prevents the 'applicationDidFinishLaunching' notification from being sent when running under Tomcat.
        // Workaround is to listen for 'applicationWillFinishLaunching'
        if( isRunningAsServlet() ) {	
        	NSNotificationCenter.defaultCenter().addObserver(this, new NSSelector<Object>("forceApplicationFinishedMessage", ERXConstant.NotificationClassArray),WOApplication.ApplicationWillFinishLaunchingNotification, null);
        }
        
        if (disableSnapshotRefCounting )
        	EODatabase.disableSnapshotRefCounting();
	}
	
    public final void forceApplicationFinishedMessage(NSNotification notification) {
		NSNotificationCenter.defaultCenter().postNotification(WOApplication.ApplicationDidFinishLaunchingNotification, new NSNotification("", null) );
    }
    
    public boolean isRunningAsServlet() {
    	// nh 2/21/12: Based on other WO code, it appears wasMainInvoked() is used to 
    	// determine whether running as a servlet. We could also do contextClassName().endsWith( "ServletContext" );
        return ! wasMainInvoked(); 
    }
    
	public void applicationDidFinishLaunching( NSNotification notification ) {
		
		// nh 6/13/12: This is causing exceptions when running under Mac OS X 10.7.4 Server.  
        //new MDSJarChecker( _argv ).loadAndCheckJars();

        // nh 12/14/11: There appears to be a bug in ERXObjectStoreCoordinatorSynchronizer.ProcessChangesQueue._process()
        // when EOEntity.globalIDForRow() returns null, resulting in an IllegalArgumentException. We patch EOObjectStoreCoordinatorSynchronzier
        // in 0JavaFoundationOverrides.jar to fix this.
       	ERXObjectStoreCoordinatorSynchronizer.initialize();
       	ERXObjectStoreCoordinatorPool.initialize();

       	ERXEntityClassDescription.registerDescription();
       	
       	// nh 12/14/11: We now add MDSDatabaseUtils.DisableUseDistinctDatabaseContextDelegate to the multicast 
       	// delegate configured in _installEntityDependencyOrderingDelegate(); otherwise, we'd replace this delegate.
       	//MDSDatabaseUtils.disableUseDistinctForFetching();
       	
    	// nh 7/11/08 disable synchronizer for now, until we figure out how to do locking properly 
       	// (doesn't seem to really disable the synchronizer... oh well)
       	ERXObjectStoreCoordinatorSynchronizer.synchronizer().setDefaultSettings( new SynchronizerSettings( true, true, true, true ) );	
        ERXEC.setFactory( new MDSEditingContext.Factory( ERXObjectStoreCoordinatorPool._pool(), ERXEC._factory() ) );	// now override with our factory, which inherits the MultiOSCFactory
        if( ! isWindows ) {
            // nh 7/18/08: This causes an "Unknown signal: HUP" when running under Tomcat on Windows
        	ERXEC.registerOpenEditingContextLockSignalHandler();	// intercepts HUP signals to show open locks info
        }
        
        //ERXBrowserFactory.setFactory( new MDSBrowserFactory() );

        if( isServlet ) {
        	ERXResponseRewriter.setDelegate( new ERXResponseRewriter.Delegate() {
        		public boolean responseRewriterShouldAddResource(String framework, String fileName) {
        			return true;
        		}
        	
        		public Resource responseRewriterWillAddResource(String framework, String fileName) {
        			_logger.info( "Returning resource '" + fileName + "' in framework '" + framework + "'" );
        			return new Resource( framework, fileName );
        		}
        	});
        }
        
        // We override ERXWOContext and ERXWOServletContext in order to support forced URL completion (needed for forced HTTPS)
        // and to remove the port from the URL if it is the default for the protocol (i.e., 80 or 443)
        rootLogger().info(  "contextClassName() = " + contextClassName() );
        
        // TODO
//        if( contextClassName().endsWith( "ServletContext" ) )
//        	setContextClassName( MDSServletContext.class.getName() );
//        else if( contextClassName().endsWith( "Context" ) )
//        	setContextClassName( MDSContext.class.getName() );
        rootLogger().info(  "new contextClassName() = " + contextClassName() );
        
        _installEntityDependencyOrderingDelegate();

        // nh 7/18/08: These *should* be defaults, but just in case... (at least the ERXMessageEncoding call seems to be required)
        WOMessage.setDefaultEncoding("UTF-8");
        WOMessage.setDefaultURLEncoding( "UTF-8" );
        ERXMessageEncoding.setDefaultEncodingForAllLanguages("UTF-8");
        
        
        // TODO
//        WOHelperFunctionTagRegistry.registerTagShortcut( MDSDivHelpLink.class.getSimpleName(), "help" );
//        WOHelperFunctionTagRegistry.registerTagShortcut( MDSHelpLabel.class.getSimpleName(), "label" );
        

        // nh 4/11/11: See http://www.mail-archive.com/webobjects-dev@lists.apple.com/msg16832.html -- 
        // Application.shouldRestoreSessionOnCleanEntry() seems to work fine to restore the session
        // without needing to set the default request handler to direct actions, although note that 
        // because WO sets the session cookie on the URL up to .woa, omitting the .woa causes a new
        // session to be created. 
        // 
        // I'm guessing that setting the default request handler to be direct actions makes it so you
        // can set the default component to return inside the direct action's default action, and not
        // have to check for a request for "Main" in Application.pageWithName() when you want to use
        // something other than Main for your point of entry -- which is a good idea, since Main 
        // can easily conflict with a Main class in other libraries.
        setDefaultRequestHandler( requestHandlerForKey( directActionRequestHandlerKey() ) );
        
        
        // nh 2/12/09: This thing seems to cause major problems with notifications across editing contexts.
        // Resulted in an issues where changes to the assigned tech wouldn't stick.
        //
        // poor man's long response handler for slow pages
     /*   registerRequestHandler( new ERXDelayedRequestHandler( 
        		5, // seconds between refreshes 
        		25, // seconds to wait for page 
        		120, // seconds until give up
        		"/helpdesk/css/delayedRequest.css" 
        		), ERXDelayedRequestHandler.KEY );
*/
        listPackages();
        
        // TODO
//        rootLogger().info("Java version = " + getProperty( "java.version" ) );
//        rootLogger().info("Java home = " + getProperty( "java.home" ) );
//        rootLogger().info("Java ext dirs = " + getProperty( "java.ext.dirs" ) );
//        rootLogger().info("Java classpath = " + getProperty( "java.class.path" ) );
//        rootLogger().info("Java temp dir = " + getProperty( "java.io.tmpdir" ) );
//        rootLogger().info("Unlimited crypto? " + UnlimitedStrengthCryptoDetector.isUnlimitedStrengthCryptoAvailable() );
        
        
        Class<?> mainClass = _NSUtilities.classWithName("Main");
        rootLogger().info("Main class: " + (mainClass == null ? "null" : mainClass.getName()) );
        
        com.ibm.icu.util.TimeZone ibmTimeZone = com.ibm.icu.util.TimeZone.getDefault();
        NSTimeZone nsTimeZone = NSTimeZone.defaultTimeZone();
        
        
        // TODO
        //com.ibm.icu.util.TimeZone safeNsTimeZone = MDSCalendarUtils.calendarSafeTimeZone( nsTimeZone ); 
        java.util.TimeZone javaTimeZone = java.util.TimeZone.getDefault();
//        rootLogger().info(
//    		"com.ibm.icu.util.TimeZone = " + ibmTimeZone.getDisplayName()  + " [" + ibmTimeZone.getID() + ": " + ibmTimeZone.getRawOffset() + "]" 
//    		+ " NSTimeZone = " + nsTimeZone.getDisplayName()  + " [" + nsTimeZone.getID() + ": " + nsTimeZone.getRawOffset() + "]"
//    		+ " DB-safe TimeZone = " + safeNsTimeZone.getDisplayName() + " [" + safeNsTimeZone.getID() + ": " + safeNsTimeZone.getRawOffset() + "]"
//    		+ " java.util.TimeZone = " + javaTimeZone.getDisplayName()  + " [" + javaTimeZone.getID() + ": " + javaTimeZone.getRawOffset() + "]"
//		);

        rootLogger().info("System properties: " + System.getProperties() );
        
        if( ! isJavaFoundationOverridesConfiguredSuccessfully() ) {
        	_logger.error( "ERROR: JavaFoundationOverrides.jar has not been installed correctly. It must be found in the Java classpath before JavaFoundation.jar and ERFoundation.jar. Without this jar, using square brackets ('[' and ']') in search fields may yield errors or incorrect results." );
        }
        
        EOEditingContext.setDefaultFetchTimestampLag(600000);
	}

	
	public boolean handleMalformedCookieString() {
		return true;
	}

	private static String stringUtilsClassName = "com.webobjects.foundation._NSStringUtilities";

	public boolean isJavaFoundationOverridesConfiguredSuccessfully() {
		boolean isConfigured = false;
		try {
			
			Class<?> stringUtilsClass = Class.forName( stringUtilsClassName );
			stringUtilsClass.getField("MDSJavaFoundationOverride");
			isConfigured = true;
		}
		catch( NoSuchFieldException e ) {
			// wrong file
		}
		catch( Exception e ) {
			_logger.error( "Error while checking for JavaFoundationOverrides.jar: " + e, e );
		}
		
		return isConfigured;
	}
	
	public String getNSStringUtilitiesJarPath() {
		String path = "[Not found]";
		try {
			Class<?> stringUtilsClass = Class.forName( stringUtilsClassName );
			java.net.URL u = stringUtilsClass.getResource("");
			path = u.toString();
		}
		catch( ClassNotFoundException e ) {
			_logger.error( "ERROR: Could not find class '" + stringUtilsClassName + "'" + e, e );
		}
		
		return path;
	}
	
	private static boolean _sqlDebug = false;
    public static void setSqlDebug (boolean b) {
		_sqlDebug = b;
        if (b) {
            NSLog.allowDebugLoggingForGroups(NSLog.DebugGroupModel
                                             | NSLog.DebugGroupSQLGeneration
                                             | NSLog.DebugGroupDatabaseAccess
                                             | NSLog.DebugGroupEnterpriseObjects);
            Logger erxLogger = Logger.getLogger("er.extensions.ERXAdaptorChannelDelegate.sqlLogging");
            erxLogger.setLevel( Level.DEBUG );
            System.setProperty( "er.extensions.ERXAdaptorChannelDelegate.trace.milliSeconds.warn", "0" );
            
        } else {
            NSLog.refuseDebugLoggingForGroups(NSLog.DebugGroupModel);
            NSLog.refuseDebugLoggingForGroups(NSLog.DebugGroupSQLGeneration);
            NSLog.refuseDebugLoggingForGroups(NSLog.DebugGroupDatabaseAccess);
        }
    } // setSqlDebug

	public static boolean sqlDebug(){
		return _sqlDebug;
	}
	
	public static boolean isRunningInMonitor() {
		return _isRunningInMonitor;
	}

	private void _installEntityDependencyOrderingDelegate() {
		if( false ) return;
        
		// This method is according to wonder-disc post by Chuck Hill on 8/24/07 (Subject: "Re: [Wonder-disc] MS SQL Server Ordering (redux)")
		
		// TODO: Re-enable this once we resolve the circular references in our model (e.g., Subscriber > Preference > Subscriber)
		// FIXME: Doesn't work in daemon -- throws the following:
		/*
		  com.webobjects.eoaccess.EOObjectNotAvailableException: databaseContextForModelNamed: cannot find model named dbwhd associated with this EOEditingContext
			at com.webobjects.eoaccess.EOUtilities.databaseContextForModelNamed(EOUtilities.java:769)o do
				at com.macsdesign.util.MDSApplication._installEntityDependencyOrderingDelegate(MDSApplication.java:99)
				at com.macsdesign.util.MDSApplication.<init>(MDSApplication.java:71)
				at com.macsdesign.whd.daemon.Application.<init>(Application.java:86)
				at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
				at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:39)
				at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:27)
				at java.lang.reflect.Constructor.newInstance(Constructor.java:494)
				at java.lang.Class.newInstance0(Class.java:350)
				at java.lang.Class.newInstance(Class.java:303)
				at com.webobjects.appserver.WOApplication.main(WOApplication.java:323)
				at com.macsdesign.whd.daemon.Application.main(Application.java:63)
		 */
		
		
		Object existingDelegate = EODatabaseContext.defaultDelegate();
		
		EODatabaseContext.setDefaultDelegate( null );
		// TODO - 
//        ERXDatabaseContextMulticastingDelegate.addDefaultDelegate( new MDSEntityDependencyOrderingDelegate() );
        // TODO - 
        //ERXDatabaseContextMulticastingDelegate.addDefaultDelegate( new MDSDatabaseUtils.DisableUseDistinctDatabaseContextDelegate() );
        ERXDatabaseContextMulticastingDelegate.addDefaultDelegate( ERXDatabaseContextDelegate.defaultDelegate() );
        
        // Set the delegate on the already created context
//        EOEditingContext ec = new EOEditingContext();
//        EODatabaseContext dbContext;
//        ec.lock();
//        try {
//            dbContext = EOUtilities.databaseContextForModelNamed( ec, DB_MODEL_NAME );
//            dbContext.lock();
//            try {
//                dbContext.setDelegate(EODatabaseContext.defaultDelegate());
//            }
//            finally {
//                dbContext.unlock();
//            }
//        }
//        finally {
//            ec.unlock();
//        }
        

	}
//	
//	@Override
//	public WOResponse dispatchRequest( WORequest request ) {
//		WOResponse response = super.dispatchRequest( request );
//		if( AjaxUtils.isAjaxRequest(request) ) {
//			
//			// nh 6/2/09: This is a hack for pre-Safari 3.0.4 browsers in which the browser charset doesn't
//			// default to utf-8 for XMLHttpRequests. Not sure why the charset header isn't getting set to
//			// utf-8 by default. (AjaxUtils.createResponse() sets it to utf-8.) 
//			//
//			// There's probably a better way to make this happen, but this seems to work okay for now...
//			
//			String contentType = response.headerForKey( "content-type" );
//			if( contentType == null )
//				contentType = "text/html; charset=utf-8";
//			else if( contentType.toLowerCase().indexOf( "charset" ) == -1 )
//				contentType += "; charset=utf-8";
//			response.setHeader( contentType, "content-type" );
//		}
//		return response;
//	}
	
//	@Override
//	public WOResponse handleActionRequestError(WORequest aRequest,
//			Exception exception, String reason, WORequestHandler handler,
//			String actionClassName, String actionName, Class actionClass,
//			WOAction actionInstance) {
//		
//		_logger.error( exception, exception );
//		
//		// Check in session if one was created for the action (to avoid possible deadlock)
//		// per http://bosyotech.blogspot.com/2009/08/how-to-fix-webobjects-random-session.html, which borrows from
//		// http://osdir.com/ml/web.webobjects.wonder-disc/2002-12/msg00035.html
//		if(actionInstance != null && actionInstance.context() != null &&  
//		   actionInstance.context().hasSession()) {  
//			sessionStore().checkInSessionForContext(actionInstance.context());  
//		}  
//		
//		WOContext aContext = new WOContext( aRequest );
//        ErrorPage nextPage = pageWithName( ErrorPage.class, aContext );
//        nextPage.headingMessage = "An error occurred while handling request " + aRequest.uri() + ":";
//        nextPage.stackTrace = MDSUtils.stackTraceForThrowable( exception );
//        nextPage.errorMessage = MDSUtils.beautifiedMessageForThrowable( exception );
//		nextPage.showBugReportStatus = false;
//		nextPage.showContinueButton = false;
//				
//    	WOResponse response = nextPage.generateResponse();
//    	response.setStatus( WOMessage.HTTP_STATUS_NOT_FOUND );
//    	return response;
//    }
	
	public int maxCoordinators() {
		int max = dbConnectionCount;
		
		if( max < 0 )
			max = 10;
		
		return max;
	}
	
	public static Logger rootLogger() {
		return _logger;
	}

	private static NSMutableDictionary _globalInfo = new NSMutableDictionary();
	public NSMutableDictionary globalInfo() {
		return _globalInfo;
	}


	public static void listPackages() {
		_logger.info( packagesListString() );
	}

	public static String packagesListString() {
		Package[] allPackages = Package.getPackages();
		String msg = "Loaded Packages: \n";

		for(int i = 0; i < allPackages.length; i++) {
			msg += "" + (i+1 )
			+ ": " + allPackages[i].getName()
			+ ": " + allPackages[i].getImplementationTitle() + "(" + allPackages[i].getImplementationVendor() + ")"
			+ ", version: " + allPackages[i].getImplementationVersion()
			+ ", spec: " + allPackages[i].getSpecificationTitle() + "(" + allPackages[i].getSpecificationVendor() + " - " + allPackages[i].getSpecificationVersion() + ")"
			+ "\n";
		}

		return msg;
	}


	public void addExternalComponentDefinition( String className ) {
		//_logger.info( "MDSApplication.addExternalComponentDefinition( " + className + " ) " );
		_dummyComponentDefinition( className );		// this call will add the class name to the dummy list
	}

	private static NSMutableDictionary _dummyComponentDefinitionsCache = new NSMutableDictionary();
	public WOComponentDefinition _dummyComponentDefinition( String className ) {

		// Method to create dummy component definitions. The class name has to be provided
		// because WO uses it during takeValuesFromRequest to identify which class to
		// receive form values. Called by _componentDefinition() and _componentDefinitionFromClassNamed()
		//
		// Dummy components are provided for components with custom template handlers so that
		// when the component requests its component definition (which in turn calls WOApplication.appliation()._componentDefinition(),
		// the call will not throw an exception complaining that the component definition cannot be found.
		//

 		if( className == null )
			className = "dummy_class";

		WOComponentDefinition dummyComponentDefinition = (WOComponentDefinition) _dummyComponentDefinitionsCache.objectForKey( className );
		if( dummyComponentDefinition == null ) {
			try {
				final long ComponentDefinitionDebugGroup = 67108868L;		// turn off the error message that file://dummy cannot be found
				boolean savedDebug = NSLog.debugLoggingAllowedForGroups( ComponentDefinitionDebugGroup );
				NSLog.refuseDebugLoggingForGroups( ComponentDefinitionDebugGroup );
				dummyComponentDefinition = new WOComponentDefinition( className, new URL("file://dummy"), null, null, null );
				if( savedDebug )
					NSLog.allowDebugLoggingForGroups( ComponentDefinitionDebugGroup );
				
				// dummyComponentDefinition.setCachingEnabled( false );  // nh 11/5/07: Tried this hoping to force the component to refresh from the plugin (if in test mode -- see PluginTemplateLoader)
				_dummyComponentDefinitionsCache.setObjectForKey( dummyComponentDefinition, className );
			}
			catch( Exception e ) {
				_logger.error( "INTERNAL ERROR: " + e, e );
				throw new RuntimeException( e );
			}
		}
		return dummyComponentDefinition;

	}


	public WOComponentDefinition _componentDefinition(String s, NSArray nsarray) {
		// Overrides the superclass method so that requests for components that were
		// not loaded at startup will not fail (otherwise, super._componentDefinition() fails
		// with a null pointer). Necessary for report plugins.

		//_logger.info( "MDSApplication._componentDefinition( " + s + " )" );
		//_logger.info( "  dummy component definitions cache: " + _dummyComponentDefinitionsCache.allKeys() );

		WOComponentDefinition def;
		try {
			WOComponentDefinition dummy = (WOComponentDefinition) _dummyComponentDefinitionsCache.objectForKey( s );
			if( dummy != null ) {
				//_logger.debug( "  Found dummy component definition." );
				def = dummy;
			}
			else {
				//_logger.info( "  No dummy component found for component " + s + ". Calling super method." );
				def = super._componentDefinition( s, nsarray );
				if( def == null ) {
					//_logger.info( "   Super method returned null definition. Caching a dummy component." );
					def = _dummyComponentDefinition( s );
				}
			}
		}
		catch( Exception e ) {
			_logger.info( "MDSApplication._componentDefinition: " + e, e );
			def = _dummyComponentDefinition( s );
		}
		return def;
	}

	public WOComponentDefinition _componentDefinitionFromClassNamed(String s) {
		// Overrides the superclass method so that requests for components that were
		// not loaded at startup will not fail (otherwise, super._componentDefinition() fails
		// with a null pointer). Necessary for report plugins.

		//_logger.debug( "MDSApplication._componentDefinitionFromClassNamed( " + s + " )" );

		WOComponentDefinition def;
		try {
			WOComponentDefinition dummy = (WOComponentDefinition) _dummyComponentDefinitionsCache.objectForKey( s );
			if( dummy != null ) {
				//_logger.info( "  Found dummy component definition." );
				def = dummy;
			}
			else {
				//_logger.info( "  No dummy component found for component " + s + ". Calling super method." );
				def = super._componentDefinitionFromClassNamed( s );
				if( def == null ) {
					//_logger.info( "   Super method returned null definition. Caching a dummy component." );
					def = _dummyComponentDefinition( s );
				}
			}
		}
		catch( Exception e ) {
			_logger.info( "MDSApplication._componentDefinitionFromClassNamed: " + e, e );
			def = _dummyComponentDefinition( s );
		}
		return def;
	}

    public WORequest createRequest(String aMethod, String aURL, String anHTTPVersion, NSDictionary someHeaders, NSData aContent, NSDictionary someInfo) {

    	WORequest request = super.createRequest( aMethod, aURL, anHTTPVersion, (NSDictionary<?,?>) someHeaders, aContent, someInfo );

    	//MDSRequest request = new MDSRequest( aMethod, aURL, anHTTPVersion, someHeaders, aContent, someInfo );
    	// We don't want WORequest to use these headers to override our database settings
    	request.removeHeadersForKey( "x-webobjects-servlet-server-name" );
    	request.removeHeadersForKey( "x-webobjects-server-name" );
    	request.removeHeadersForKey( "x-webobjects-servlet-server-port" );
    	request.removeHeadersForKey( "x-webobjects-server-port" );

    	//_logger.debug( "Appliation.createRequest(): headers = " + someHeaders );

    	return request;
    }
    
    public WOContext createContextForRequest( WORequest aRequest ) {

    	String appName = this.baseURL();
    	
    	// nh 1/22/09: We used to insert our own MDSContext here; instead, we now 
    	// specify the correct context class name in the constructor.
    	WOContext context = super.createContextForRequest( aRequest );
    	if( rootLogger().isTraceEnabled() && context != null )
    		rootLogger().trace( "createContextForRequest: context is " + context.getClass().getName() + ", contextClassName = " + contextClassName() );

		
		// nh 11/19/07: NOTE: Be careful not to call context.toString() before the the request's setDefaultFormValueEncoding()
		// method has been called. context.toString() loads and displays the request's form values, and the subsequent setDefaultFormValueEncoding(),
		// which is called in HelpdeskSession.takeValuesFromRequest(), clears the form values if the detected character encoding
		// differs from the new assignment. Form values are read in only once per request.
		//
		// Example call that broke ClientSyncingHelpdeskImporter on Support app (but not Demo):
		//  _logger.debug( "Application.createContextForRequest(): returning " + context == null ? "null" : context.getClass().getName()) );
/*
		if (ERXWOContext.currentContext() == null) {
			ERXWOContext.setCurrentContext(context);
		}
	*/	
    	
    	if( context instanceof ERXWOContext ) {
    	//	((ERXWOContext) context)._setGenerateCompleteResourceURLs( false );
    	}
    	
		context._url().setApplicationName( name() );	// nh 1/22/09: Resource Manager creates URLs based on the context's URL; but in
														// direct connect mode (which apparently we use when running as a servlet),
														// requests are processed regardless of whether the URL matches the app name.
														// To prevent XSS attacks in which the URL app name contains encoded JavaScript,
														// we force the context's url to be the application name.
		return context;
    }
    
  
   /*
   @Override
   public WORequestHandler requestHandlerForKey(String key ) {
	   WORequestHandler handler = super.requestHandlerForKey(key);
	   System.err.println( "MDSApplication.requestHandlerForKey(): Got " + handler + " for key '" + key + "'");
	   return handler;
   }
   */
   
   @Override
   public WORequestHandler handlerForRequest(WORequest request) {
	   
	   if( _logger.isEnabledFor( Priority.WARN ) ) {
		   String key = request.requestHandlerKey();
		   String uri = request.uri();
		   if( uri != null && uri.contains( "/wa/" ) && ! "wa".equals( key ) ) {
			   _logger.warn( "Invalid URL detected in request. Request '" + request.uri() + "' contains '/wa/' but requestHandlerKey is '" + key + "'." );
		   }
	   }
	   
	   WORequestHandler handler = super.handlerForRequest( request );
	   
	   //System.err.println( "MDSApplication.handlerForRequest(): key = '" + key + "' handler = " + handler + " uri = " + request.uri()  + " uriDecomposed = " + url.description() );
	   return handler;
   }
   
}
