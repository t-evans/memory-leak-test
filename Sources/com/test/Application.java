package com.test;

import org.apache.log4j.Logger;

import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOSessionStore;
import com.webobjects.foundation.NSLock;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSNotification;


public class Application extends MDSApplication {
//	public static void main(String[] argv) {
//		ERXApplication.main(argv, Application.class);
//	}
//
//	public Application() {
//		ERXApplication.log.info("Welcome to " + name() + " !");
//		/* ** put your initialization code in here ** */
//		setAllowsConcurrentRequestHandling(true);
//		
//	}	
	private static Logger _logger = Logger.getLogger(  MDSApplication.class );


    public NSMutableDictionary<String,Object> bindings = new NSMutableDictionary<String,Object>();
    //public boolean isHosted;
    public String version;
    public String clientKey;

    public static final int PART_TEMPLATE = 1;
    public static final int INVENTORY_TEMPLATE = 2;
    public static final int CLIENT_TEMPLATE = 3;
    public static final int ASSET_TEMPLATE = 4;

    // hasLoadedLdap is a tempoary kluge until we can figure out how to enable
    // ldap adaptor reconnection; for now, it indicates to LDAPConfigPanel whether
    // ldap connection settings will take effect immediately or at application restart
    public boolean hasLoadedLdap = false;
    public boolean hasPerformedStartupDataUpdate = false;
    private boolean concurrentRequestsAllowed = true;


	public static class SessionLimitExceededException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	public String JSONRPCRequestHandlerKey() { return "jrpc"; }

    // Prevents isDatabaseConnectionValidated() from returning until the application has had a chance 
    // to perform post-launch initialization (applicationDidFinishLaunching()). Otherwise, the login
    // page could call isDatabaseConnectionValidated() before applicationDidFinishLaunching() has completed,
    // and think that the "database update in progress" dialog should be presented.
    //
    // The lock is set in the constructor and unlocked when applicationDidFinishLaunching is finished
    public static NSLock applicationLaunchLock = new NSLock(); 
    
    
	public static void main(String argv[]) {


        // NOTE: main does not get called when run as a servlet


		//NSLog.allowDebugLoggingForGroups( 0xffffff );
		//NSLog.debug.setAllowedDebugLevel( NSLog.DebugLevelDetailed );
		//NSLog.debug.setIsVerbose( true );

        //setup(argv);	// Needed when inheriting ERXApplication
        //NSProperties.setPropertiesFromArgv( argv );

		// Prevent "
		// http://en.wikibooks.org/wiki/WebObjects/Web_Applications/Development/Custom_Error_Handling
		// says this has to be put in main, not the constructor.
		System.setProperty("WODisplayExceptionPages", "true");
        MDSApplication.main(argv, Application.class);
    }
	
	/*
	 // This override can be helpful for debugging...
	public WOComponent pageWithName( String name, WOContext context ) {
		_logger.debug( "Application.pageWithName( '" + name + "', <context> )" );
		WOComponent page = super.pageWithName( name, context );
		if( page == null ) {
			_logger.debug( "  page = null" );
		}
		else {
			WOComponentDefinition def = page._componentDefinition();
			if( def == null ) {
				_logger.debug ( "  page._componentDefinition = null" );
			}
			else {
				_logger.debug( "  Page name = " + def.name() + " pathURL = " + def.pathURL() + " baseURL = " + def.baseURL() +  " componentClass = " + def.componentClass().getName() );
			}
		}
		return page;
	}
	*/

//    protected Class<HelpdeskSession> _sessionClass() {
//    	return HelpdeskSession.class;
//    }

//    @Override
//    public WOSession createSessionForRequest(WORequest worequest) {
//    	//_logger.error( "Creating new session for request: " + worequest.uri(), new Exception( "STACK TRACE") );
//
//    	WOSession session;
//
//    	// route requests use RestSession
//    	if( ERXRouteRequestHandler.Key.equals( worequest.requestHandlerKey() ) )
//    		session = new RestSession();
//    	else
//    		session = super.createSessionForRequest(worequest);
//    	
//    	return session;
//    }
//    
//    public WOResponse dispatchRequest( WORequest aRequest ) {
//    	    	
//    	WOResponse aResponse = null;
//    	
//        LoggerUtils.initNdcIfNecessary();
//
//        if( aResponse == null ) {
//
//        	if( aRequest.uri().endsWith( "/" ) ) {
//        		// remove the trailing slash
//        		// nh 2/27/07: Is there a better way to remove the trailing slash than to create an entirely new WORequest?
//        		aRequest = new WORequest( aRequest.method(), aRequest.uri().substring( 0, aRequest.uri().length()-1), aRequest.httpVersion(), aRequest.headers(), aRequest.content(), aRequest.userInfo() );
//        	}
//
//        	NDC.push( aRequest.applicationNumber() + " (" + aRequest.sessionID() + ")" );
//        	
//
//        	// nh 5/24/12: Recommendation by Chuck Hill http://www.mail-archive.com/webobjects-dev@lists.apple.com/msg04150.html
//        	// to ensure exceptions get handled by the custom exception handler.
//            try {
//            	aResponse = super.dispatchRequest( aRequest );
//            }
//            catch(Throwable t) {
//            	aResponse = WOApplication.application().handleException (new NSForwardException(t), createContextForRequest( aRequest ));
//            }
//
//    		NDC.pop();
//
//        	// nh 10/22/08: Trying to figure out a way to make the session cookie 
//        	// use the base domain, without the host. This doesn't seem to work.
//        	// 
//        	// See http://www.esus.be/blog/?p=3
//        	// and http://www.nabble.com/Share-session-cookie-across-subdomains- td16787390.html
//
//        	/*
//	        WOCookie foundCookie = null;
//	        for( WOCookie aCookie: (NSArray<WOCookie>) aResponse.cookies() ) {
//	        	if( "JSESSIONID".equals( aCookie.name() ) ) {
//	        		foundCookie = aCookie;
//	        		break;
//	        	}
//	        }
//	
//	        if( foundCookie != null ) {
//	        	aResponse.removeCookie( foundCookie );
//	        	foundCookie.setDomain( ".mycomain.com" );
//	        	aResponse.addCookie( foundCookie );
//	        }
//        	 */
//        }
//
//        return aResponse;
//    }
//
//    public WOActionResults invokeAction( WORequest aRequest, WOContext aContext ) {
//        //NDC.push( "invoke action: " + HostAddressFinder.hostName() );
//        if( aContext.senderID() != null && ! aRequest._hasFormValues() ) {
//           // _logger.debug( "  No form values; calling takeValuesFromRequest() anyway..." );
//            takeValuesFromRequest( aRequest, aContext );
//        }
//        
//    	boolean isFromAjaxLongResponsePage = "1".equals( aRequest.stringFormValueForKey( MDSAjaxLongResponsePage.LONG_RESPONSE_REDIRECT_FLAG ) );
//    	if( ! isFromAjaxLongResponsePage )
//    		return super.invokeAction( aRequest, aContext );
//    	else
//    		return null;
//    }
//
//    public void takeValuesFromRequest( WORequest request, WOContext context ) {
//    	
//    	boolean isFromAjaxLongResponsePage = "1".equals( request.stringFormValueForKey( MDSAjaxLongResponsePage.LONG_RESPONSE_REDIRECT_FLAG ) );
//    	if( ! isFromAjaxLongResponsePage )
//    		super.takeValuesFromRequest( request, context );
//    }

    
    public Application() {
        super();

        applicationLaunchLock.lock();
        
        // nn 10/19/10: This notification isn't needed because we override the applicationDidFinishLaunching method that is notified by MDSApplication
        //NSNotificationCenter.defaultCenter().addObserver( this, new NSSelector( "applicationDidFinishLaunching", new Class[] { NSNotification.class } ), WOApplication.ApplicationDidFinishLaunchingNotification, null );
    }


    /** 
     * Overrides MDSApplication.applicationDidFinishLaunching(), so we have to call super. Notification is set up in MDSApplication constructor.
     */
    @Override
	public void applicationDidFinishLaunching( NSNotification notification ) {
    	try {
	    	_logger.info( "Application.applicationDidFinishLaunching()..." );
	
	    	super.applicationDidFinishLaunching( notification );
	
	        //NotificationLogger notificationLogger = new NotificationLogger();		// for development only; shows all notification messages
	
			//_logger.error( "NSTimeZone.abbreviationDictionary: " + NSTimeZone.abbreviationDictionary() );
	
	        _logger = Logger.getLogger( Application.class );
	
	
//	        if( ! AppProperties.isVersionValid() ) {
//	            _logger.fatal("ERROR: Application files are corrupt.");
//	            System.exit(1);     // someone monkeyed with the version #
//	        }
	
//	        _logger.info("Welcome to " + this.name() + " " + AppProperties.appVersion() + "!");
	
	
//			if ( AppProperties.isTestMode() )
//				_logger.info("RUNNING IN TEST MODE!");
	
	        //System.out.println("baseURL = " + baseURL() );
	        //System.out.println("ApplicationBaseURL = " + applicationBaseURL() );
	        //System.out.println("cgiAdaptorURL = " + cgiAdaptorURL() );
	
//	        setSqlDebug( AppProperties.isSqlDebugMode() );
	
	
	        // allowsConcurrentRequestHandling must be false for Playback Manager to work...
	        // ...also, this may be part of the cause of our db problems? (nh 2/2/04)
	        boolean concurrentRequestsAllowed = this.concurrentRequestsAllowed;
//	        _logger.info( "Concurrent request handling is " + (concurrentRequestsAllowed ? "enabled" : "disabled") + ". (Specified with -D" + AppProperties.CONCURRENT_REQUESTS + "=<true|false>)" );
	        setAllowsConcurrentRequestHandling( concurrentRequestsAllowed );
	
//	        int connectionCount = maxCoordinators();
//	        if( connectionCount == 0 )
//	            _logger.info( "Using a single database connection. (Specified with -D" + AppProperties.DB_CONNECTIONS + "=<count>)" );
//	        else
//	            _logger.info( "Using up to " + connectionCount + " database connections." );
	
			setIncludeCommentsInResponses( true );	// need comments in order to use ie css hack
	        setPageRefreshOnBacktrackEnabled( true );
	        setPageCacheSize( 10 );     // 30 is default anyway
	
//	        version = AppProperties.appVersion();
	
			// refresh the custom field defs cache whenever the db is updated
			// TODO: Optimize this to only refresh when the custom fields are updated
	
	 /*
	        // Configure LEWOFramework's JSON-RPC handler
	        LEWOJSONRPCRegistry.singleton().addPackageForActions( "com.macsdesign.whd.jsonrpc.actions" );
	        registerRequestHandler( new LEWOJSONRPCRequestHandler(), JSONRPCRequestHandlerKey() );
	*/
	        // parse WHD-specific JVM options
//	        if( AppProperties.isHosted() ) {
//	            _logger.info("Running hosted application. Max clients per subscriber = " + AppProperties.maxClients() );
//				if( ! AppProperties.isValidHosted() ) {
//					_logger.fatal( "ERROR: Unauthorized attempt to run in hosted mode." );
//					System.exit(1);     // tried to spoof as hosted
//				}
//	        }
	
//	        if( AppProperties.isDemo() ) _logger.info("Running in demo mode.");
	
			//loadPlugins();
	
//			_logger.info( "Using PDF fonts folder " + AppProperties.fontsFolder() + "..." );
//			com.macsdesign.widgets.FOComponent.initializeFop( AppProperties.fontsFolder() );
//	
//	        Keyboard.in.pause("About to check db connection...");
	
	        // TODO - 
	        HelpDeskDBLoader.loadModelsWithPrototypeReplacement();
	        
	        
	        registerQualifierSupportClasses();
	        registerOgnlHelpderFunctions(); 
//	        registerRouteRequestHandlers();
	        
	        // Results in call to performRefesh() whenever a refresh flag is incremented (basically, whenever setup changes)
//	        RefreshFlagHandler.addToRefreshFlagListeners( "Instance cache refresher", this, null );

//	        // Main returns an ErrorPage preventing normal application login if daemonMode is DEDICATED
//	        switch( AppProperties.daemonMode() ) {
//	        	case DEDICATED:
//	        		_logger.warn( "WHDDaemonMode=\"dedicated\": Application will provide daemon services only.");
//	        		DaemonScheduler.scheduler().startTimers();
//	        		break;
//	        		
//	        	case BACKGROUND:
//	        		_logger.warn( "WHDDaemonMode=\"background\": Daemon services will run in the background.");
//	        		DaemonScheduler.scheduler().startTimers();
//	        		DaemonMonitor.monitor().startActivityCheckPulse();
//	        		break;
//	        		
//	        	case NONE:
//	        		_logger.warn( "WHDDaemonMode=\"none\": Daemon services will not be activated.");
//	        		DaemonMonitor.monitor().startActivityCheckPulse();
//	        		break;
//	        }
	        //LoggerUtils.initLoggers();

    	}
    	finally {
    		applicationLaunchLock.unlock();
    	}
	        
		// ApplicationSetupValidator waits for applicationLaunchLock (so that it knows the models have been loaded), 
    	// so we can't perform the check until the lock has been released. Otherwise, we'd get deadlock.
    	// 
//        try {
//        	ApplicationSetupValidator.instance().checkApplicationIsReady();
//        }
//        catch( ErrorMessageException e ) {
//        	_logger.error( e );
//        }
        
    }

	private void registerQualifierSupportClasses() {
		// This enables sql generation for the ExistsInRelationshipQualifier (which can create sql for finding empty toMany relationships)

	    // TODO
//		QualifierGenerationSupport.setSupportForClass(new ExistsInRelationshipQualifierSupport(), ExistsInRelationshipQualifier.class);
//		QualifierGenerationSupport.setSupportForClass(new InSubqueryQualifierSupport(), InSubqueryQualifier.class); // Allows the ExistsInRelationshipQualifier to work with non-flattened relationships
		
		// This registers a ComparisonSupport class that makes all EOQualifiers do MDSUtils.eoEqualsEo()
		// comparisons on EOEnterpriseObjects.
		// TODO
//		EOEnterpriseObjectComparisonSupport eoComparisonSupport = new EOEnterpriseObjectComparisonSupport();
//		ComparisonSupport.setSupportForClass( eoComparisonSupport, EOEnterpriseObject.class );		// nh -- Using interfaces doesn't seem to work: EOQualifier.ComparisonSupport.supportForClass() just seems to iterate through classes
//		ComparisonSupport.setSupportForClass( eoComparisonSupport, EOGenericRecord.class );		// Should we limit this to just MDSGenricRecord? Or go further up and apply it to EOCustomObject?
//		ComparisonSupport.setSupportForClass( eoComparisonSupport, NSArray.class ); // Need to register it for arrays too, in case it is an array of EOs
	}

//    public void registerRouteRequestHandlers() {
//    	
//    	System.setProperty( "ERXRest.defaultFormat", ERXRestFormat.json().name() );
//    	
//    	// For format options, see ERXRestFormatDelegate documentation 
//    	// (Supports properties ERXRest.idKey, ERXRest.nilKey, ERXRest.writeNilKey, 
//    	// ERXRest.pluralEntityNames, ERXRest.typeKey, ERXRest.writeTypeKey)
//
//    	// Override default registrations to use MDSRestFormatDelegate, which fixes
//    	// a bug causing internal names to not be replaced with external names:
//		ERXRestFormat.registerFormatNamed(new ERXJSONRestParser(), new ERXJSONRestWriter(), new MDSRestFormatDelegate(), ERXRestFormat.JSON_KEY, "application/json");
//		ERXRestFormat.registerFormatNamed(new ERXJSONRestParser(), new ERXJSONRestWriter(), new MDSRestFormatDelegate(), ERXRestFormat.JS_KEY, "text/js");
//		ERXRestFormat.registerFormatNamed(new ERXPListRestParser(), new ERXPListRestWriter(), new ERXRestFormatDelegate(), ERXRestFormat.PLIST_KEY, "text/plist");
//		ERXRestFormat.registerFormatNamed(new ERXBinaryPListRestParser(), new ERXBinaryPListRestWriter(), new MDSRestFormatDelegate(), "bplist", "application/x-plist");
//		ERXRestFormat.registerFormatNamed(new ERXXmlRestParser(), new ERXXmlRestWriter(), new MDSRestFormatDelegate("id", "type", "nil", true, true, true, true), ERXRestFormat.RAILS_KEY, "application/xml", "text/xml");
//		ERXRestFormat.registerFormatNamed(new ERXXmlRestParser(), new ERXXmlRestWriter(), new MDSRestFormatDelegate(), ERXRestFormat.XML_KEY, "application/xml", "text/xml");
//		ERXRestFormat.registerFormatNamed(null, new ERXSimpleRestWriter(), new MDSRestFormatDelegate(), ERXRestFormat.HTML_KEY, "text/html");
//		ERXRestFormat.registerFormatNamed(new ERXJSONRestParser(), new ERXSproutCoreRestWriter(), new MDSRestFormatDelegate("guid", "type", "nil", true, true, false, false), ERXRestFormat.SPROUTCORE_KEY, "application/sc");
//		ERXRestFormat.registerFormatNamed(new ERXFormRestParser(), new ERXJSONRestWriter(), new MDSRestFormatDelegate(), ERXRestFormat.FORM_KEY, "application/x-www-form-urlencoded");
//
//		// Now add our own registrations
//    	ERXRestFormat.registerFormatNamed(new ERXJSONRestParser(), new SenchaRestWriter(), new MDSRestFormatDelegate("id", "type", "nil", true, true, false, false), "sencha", "application/sencha");
//
//    	
// 		ERXRestNameRegistry.registry().setExternalNameForInternalName("Ticket", "JobTicket");
// 		ERXRestNameRegistry.registry().setExternalNameForInternalName("RequestType", "ProblemType");
// 		ERXRestNameRegistry.registry().setExternalNameForInternalName("Email", "RestEmail"); 
//
// 	   // Register a route request handler and use the WO URL naming conventions (capitalized entity names, singular form, camel case -- i.e. /Company.plist)
// 		//ERXRouteRequestHandler routeRequestHandler = new ERXRouteRequestHandler(ERXRouteRequestHandler.WO);
// 		ERXRouteRequestHandler routeRequestHandler = new ERXRouteRequestHandler(new NameFormat(false, true, NameFormat.Case.CamelCase) {
// 			public String formatEntityNamed(String entityName, boolean pluralizeIfNecessary) {
// 				// default pluralizer turns "Tech" into "Teches" :-)
// 				String name;
// 				if( Tech.ENTITY_NAME.equals( entityName ) )
// 					name = Tech.ENTITY_NAME + "s";
// 				else
// 					name = super.formatEntityNamed( entityName, pluralizeIfNecessary );
// 				return name;
// 			}
// 		});
//
// 		IERXRestDelegate.Factory.setDelegateForEntityNamed( new ClientRestDelegate(), Client.ENTITY_NAME );
// 		routeRequestHandler.addRoute( new ERXRoute( Client.ENTITY_NAME, "/Clients/search", ERXRoute.Method.Get, ClientController.class, "search" ) );	// Prevents the search action from being interpreted as a client ID 
// 		routeRequestHandler.addRoute( new ERXRoute( Client.ENTITY_NAME, "/Clients/{client:Client}", ERXRoute.Method.Get, ClientController.class, "show" ) );
// 		routeRequestHandler.addDefaultRoutes(Client.ENTITY_NAME);
// 		routeRequestHandler.addDefaultRoutes(JobTicket.ENTITY_NAME);
// 		routeRequestHandler.addDefaultRoutes(Location.ENTITY_NAME);
// 		routeRequestHandler.addDefaultRoutes(StatusType.ENTITY_NAME);
// 		
// 		// Adding default routes for TicketNote logs a warning because it isn't actually an entity, it's just an interface that inherits EOGenericRecord
// 		// (There's probably a better way to add controller routes for it that won't log the warning.) 
// 		_logger.error( "The error message 'Entity TicketNote not found in the default model group!' may be ignored." );
// 		routeRequestHandler.addDefaultRoutes(TicketNote.ENTITY_NAME);
// 		routeRequestHandler.addDefaultRoutes(TechNote.ENTITY_NAME);
// 		routeRequestHandler.addDefaultRoutes(BillingRate.ENTITY_NAME);
// 		routeRequestHandler.addDefaultRoutes(Preference.ENTITY_NAME);
// 		routeRequestHandler.addDefaultRoutes(Room.ENTITY_NAME);
// 		routeRequestHandler.addDefaultRoutes(Department.ENTITY_NAME);
// 		routeRequestHandler.addDefaultRoutes(Tech.ENTITY_NAME);
// 		routeRequestHandler.addDefaultRoutes(PriorityType.ENTITY_NAME);
// 		routeRequestHandler.addDefaultRoutes(ProblemType.ENTITY_NAME);
// 		routeRequestHandler.addDefaultRoutes(CustomFieldDefinition.ENTITY_NAME);
// 		routeRequestHandler.addDefaultRoutes(TicketAttachment.ENTITY_NAME);
// 		routeRequestHandler.addDefaultRoutes(TicketBulkAction.ENTITY_NAME);
// 		routeRequestHandler.addRoute(new ERXRoute(RestEmail.ENTITY_NAME, "/Email", ERXRoute.Method.Post, RestEmailController.class, "create"));
// 		routeRequestHandler.addRoute(new ERXRoute("SessionHandler", "/Session", ERXRoute.Method.Post, RestSessionController.class, "create") );
// 		routeRequestHandler.addRoute(new ERXRoute("SessionHandler", "/Session", ERXRoute.Method.Get, RestSessionController.class, "create") );
// 		routeRequestHandler.addRoute(new ERXRoute("SessionHandler", "/Session", ERXRoute.Method.Delete, RestSessionController.class, "destroy") );
// 		routeRequestHandler.addRoute(new ERXRoute("SAML2", "/saml/metadata", ERXRoute.Method.Get, SamlController.class, "metadata" ) );
//
// 		SESnapshotExplorer.register( routeRequestHandler );
// 		ERXRouteRequestHandler.register(routeRequestHandler);
//    }
    

    /**
     * OGNL Helper functions are generally registered automatically if you name the helper class 
     * with the word "Helper" at the end, but if there are conflicts with the names of some library
     * helper, or if you don't want to use the name "Helper", you can add your specific class to
     * the registry here.
     */
	public void registerOgnlHelpderFunctions() {
		
		//WOHelperFunctionRegistry registry = WOHelperFunctionRegistry.registry();
		
		// 2010-10-20 - TE - For some reason, CollectionHelper doesn't work with a WO deployment.
		// Haven't been able to figure out why yet. Wonder spits out the error:
		//
		//		Exception invoking valueInComponent on WOOgnlAssociation with keyPath '@ognl.helperfunction.WOHelperFunctionRegistry@registry()._helperInstanceForFrameworkNamed(#this, "sorted", "listDG.displayedObjects", "WHDFunctions").sorted(listDG.displayedObjects,'firstName')'
		//		Caused by: ognl.MethodFailedException: Method "_helperInstanceForFrameworkNamed" failed for object ognl.helperfunction.WOHelperFunctionRegistry@42ab23aa [sun.misc.InvalidJarIndexException: Invalid index]
		//
        //WOHelperFunctionRegistry.registry().setHelperInstanceForClassInFrameworkNamed(new com.macsdesign.whd.util.helper.CollectionHelper(), java.util.Collection.class, "app");

//		WOHelperFunctionRegistry.registry().setHelperInstanceForClassInFrameworkNamed(new com.macsdesign.whd.util.helper.CertificateHelper(), NSData.class, "app");

	}

	/**
	 * By default, the default action causes a new session to be created, even
	 * if there is an existing session for the cookie session ID. We don't want 
	 * them to have to log in again if the session is still valid, so we change
	 * this to not create a new session.
	 */
	@Override
	public boolean shouldRestoreSessionOnCleanEntry(WORequest aRequest) {
		WOSessionStore aSessionStore = this.sessionStore();
		Session aSession = null;
		try {
			if( aRequest != null && aRequest.sessionID() != null )
				aSession = (Session)aSessionStore.restoreSessionWithID(aRequest.sessionID(), aRequest);
		}
		catch( Exception ignored ) { }

		// If the session is not null then its still in the SessionStore and can be restored.
		if(aSession != null) {
			return true;
		}
		
		//If the session no longer exists, return false so that the user will get a
		//new session instead of the session timed out error page
		return false;
	}
	
//    /** 
//     * Prevents direct component access; taken from Practical WebObjects, p. 137.
//     * Changes default page to WHDMain, in order to avoid name conflicts with classes named "Main" in other libraries.
//     * (Changing default page: http://support.apple.com/kb/TA45423)
//     */
//	@Override
//	public WOComponent pageWithName( String pageName, WOContext context ) {
//
//		boolean isComponentRequestWithNullSenderID = (context != null && context.senderID() == null) && (componentRequestHandlerKey().equals( context.request().requestHandlerKey()));
//		boolean isMainRequest = (pageName == null || pageName.equals( "Main" ));
//		if( isComponentRequestWithNullSenderID || isMainRequest ) {
//			pageName = WHDMain.class.getSimpleName();
//		}
//		
//		return super.pageWithName( pageName, context );
//	}
	   

//	private void loadPlugins() {
//		try {
//			
//			String pluginRoot = ERXApplication.isDevelopmentModeSafe() ? "build/" :  WOApplication.application().path() + "/../";
//			File reportPluginsFolder =  new File( System.getProperty( "WHDReportPlugInDir", pluginRoot + "ReportPlugIns" ) );
//			File discoveryPluginsFolder = new File( System.getProperty( "WHDDiscoveryPlugInDir", pluginRoot + "DiscoveryPlugIns" ) );
//			File widgetPluginsFolder = new File( System.getProperty( "WHDDashboardPlugInDir", pluginRoot + "DashboardPlugIns" ) );
//			ReportRegistry.getDefaultInstance().loadPlugins( reportPluginsFolder );
//			DiscoveryRegistry.getDefaultInstance().loadPlugins( discoveryPluginsFolder );
//			WidgetRegistry.getDefaultInstance().loadPlugins( widgetPluginsFolder, new NSArray<File>( reportPluginsFolder ) );
//		}
//		catch( Exception e ) {
//			_logger.error( "ERROR while attempting to load plugins: " + e, e );
//		}
//	}





//    public boolean isHosted() {
//        return AppProperties.isHosted();
//    }
//    
//    public boolean isHostedStandalone() {
//        return AppProperties.isHostedStandalone();
//    }
//
//    public boolean isStandardDeployment() {
//    	return AppProperties.isStandardDeployment();
//    }
//    
//    public boolean isSupportApp() {
//    	return AppProperties.isSupportApp();
//    }
//
//    @Override
//    public WOResponse handlePageRestorationErrorInContext( WOContext context ) {
//		if( context != null && context.request() != null && context.request().formValues() != null ) {
//			// Remove password in case we bombed during a login
//			NSMutableDictionary<String,NSArray<Object>> formValues = context.request().formValues().mutableClone();
//			if( formValues.objectForKey( "password" ) != null )
//				formValues.setObjectForKey( new NSArray<Object>("*****"), "password");
//			if( formValues.objectForKey( "md5Password" ) != null )
//				formValues.setObjectForKey( new NSArray<Object>("*****"), "md5Password" );
//		}
//		String requestedPage = (String) (context.request().formValueForKey( WORequest.PageNameKey ));
//		_logger.error("Page restoration error when requesting page '" + requestedPage + "'" );
//		
//		// TODO: Strip any passwords from context and request
//		_logger.error("Context = " + (context == null ? null : MDSUtils.stripPasswordsFromText( context.toString() ) ) );
//		_logger.error("Request = " + (context == null ? null : context.request() == null ? null : MDSUtils.stripPasswordsFromText( context.request().toString() ) ) );
//		return pageWithName( requestedPage, context ).generateResponse();
//    }

/*
    public boolean usingLdap() {
        return false;
    }
*/


/*    public boolean isHosted()
    {
        return isHosted;
    }
*/

//
//    public boolean isDemoMode()
//    {
//        return AppProperties.isDemo();
//    }
//
//    public boolean isTestMode()
//    {
//        return AppProperties.isTestMode();
//    }
    

//    @Override
//    public WOResponse handleSessionRestorationErrorInContext(WOContext aContext){
//    	
//    	WOResponse response = super.handleSessionRestorationErrorInContext(aContext);
//    	
//    	try {
//    		if (AjaxUtils.isAjaxRequest(aContext.request())) {
//    			response = createResponseInContext(aContext);
//    			String defaultUrl = aContext.directActionURLForActionNamed( "sessionExpired", null ); 	// does this get the default (i.e., login) action?
//
//    			//String defaultUrl = UrlUtils.directActionUrl(aContext, "sessionExpired", Boolean.FALSE, false);
//    			response.appendContentString("<script>document.location.href='" + defaultUrl + "';</script>");
//    		}
//    		else {
//    			_logger.debug( "Application.handleSessionRestorationErrorInContext()", new Exception( "STACK TRACE" ) );
//
//    			// nh 8/9/08: For some reason, invoking the direct action here results in the Main page continually complaining losing its session
//    			// when attempting to log in. Not sure why. For now, this code duplicates most of DirectAction.sessionExpiredAction().
//    			//
//    			// response = (WOResponse) new DirectAction( aContext.request() ).sessionExpiredAction();
//
//    			// nh 3/11/09: Not sure if this helps or not, but wondering if it prevents an error that might be caused
//    			// by an expired session. The error we're seeing is an occasional ClassCastException where pageForName() returns Main 
//    			// instead of the expected page.
//    			// -- UPDATE: This results in the login page never getting a new session, continually prompting the user to login again due
//    			// to session expiration. Leaving it here to document that it doesn't work.
//    			//
//               	//aContext = new WOContext( new WORequest( "GET", "/", "HTTP/1.0", null, null, null ) );
//    			
//    			WHDMain main = pageWithName(WHDMain.class, aContext);
//    			//((Main)main).displayMessage("Your session has expired.  Please log in again.", 3, false );
//    			NSDictionary<String,String> translationDict = LocalizationUtils.translationDictionaryForRequest( aContext.request() ).dictWithSubstitutions();
//    			String msg = (String)translationDict.objectForKey("login.msg.sessionExpired");
//    			//main.displayMessage(msg, 3, false );
//    			main.dialogMessageObject.displayErrorMessage(msg);
//    			response = main.generateResponse();
//    		}
//    	}
//    	catch( Throwable t ) {
//    		_logger.error( "SERIOUS UNTRAPPED ERROR IN APPLICATION: " + t, t );
//    		System.err.println( "Serious untrapped error in application: " + t );
//    		t.printStackTrace( System.err );
//    	}
//    	return response;
//    }


	/**
	 * <P>This method, given a context will return an appropriate
	 * complete entry point to the application so that people can
	 * enter the application again sans their previous session.</P>
	 * 
	 * <p>This method may return null if there was no possible
	 * entry-point configured.</p>
	 */

//    public WOResponse handleException( Exception e, WOContext aContext ) {
//        WOComponent errorPage = null;
//        String failureToSendMessage = null;
//		String logDescription = null;
//
//		_logger.error( e, e );
//		
//		if( e instanceof SessionLimitExceededException ) {
//			// TODO: This call to pageWithName borks out with an InvocationTargetException when
//			// running under Tomcat. Currently only affects freebie users, but we should probably
//			// one day figure out what causes it.
//			WHDMain main = pageWithName( WHDMain.class, aContext);
//			//((Main)main).displayMessage("Your session has expired.  Please log in again.", 3, false );
//			String msg = "This session is no longer active because a single-tech license permits only one tech session at a time.";
//			//main.displayMessage(msg, 3, false );
//			main.dialogMessageObject.displayErrorMessage(msg);
//
//			_logger.debug( "Application.handleSessionRestorationErrorInContext()", new Exception( "STACK TRACE" ) );
//			WOResponse response = main.generateResponse();
//			return response;
//		}
//
//        if( false ) {
//            // TODO: (Currently we don't do validation anyway)
//			//if (exceptionDescription.indexOf("EOValidationException") > -1) {
//            //    _logger.info("Validation error: " + e );
//            // errorPage = pageWithName("ValErrorPage",aContext);
//		}
//        else {
//
//			_logger.debug( "Application.handleException()", new Exception( "STACK TRACE" ) );
//
//            Throwable t = MDSUtils.originalException( e );
//
//            // ------------------------------------------
//            // get the exception description.
//            // ------------------------------------------
//            logDescription = "";
//
//            if( t instanceof JDBCAdaptorException ) {
//                logDescription = "SQLExceptions:\n\n";
//                SQLException sqlEx = ((JDBCAdaptorException) t).sqlException();
//                while( sqlEx != null ) {
//                    logDescription += "Error Code: " + sqlEx.getErrorCode() + " "
//					+ "SQLState: " + sqlEx.getSQLState() + "\n"
//					+ sqlEx.toString() + "\n\n";
//                    sqlEx = sqlEx.getNextException();
//                }
//            }
//
//			_logger.error( "UNHANDLED EXCEPTION: " + logDescription, t );
//
//            logDescription += new NSForwardException(t).stackTrace();
//
//            // email the exception description
//            try {
//                Session session = (Session) aContext.session();
//                // Get a Preference object, which hopefully has a valid e-mail address
//                String sender = "unknown@unknown.com";
//                String license = "";
//                Preference aPreference = session.aPreference();
//
//
//                if( aPreference == null && ! AppProperties.isHosted() ) {
//                    // if couldn't get a Preference from session, get the first one in the database
//                    // If app is hosted, we're sunk because we can't tell whether they have bug reports enabled or not.
//                    NSArray<Preference> preferences = MDSDatabaseUtils.objectsForEntityNamed( MDSEditingContext.newEditingContext( "handleExceptionEc" ), Preference.class );
//                    if( preferences.count() > 0 )
//                        aPreference = (Preference) preferences.objectAtIndex(0);
//                }
//
//                if( aPreference != null ) {
//                    SmtpServer smtpServer = SmtpServer.defaultSmtpServerForSubscriber( aPreference.subscriber() );
//                    String fromAddress = smtpServer.fromAddress();
//					if ( ! MDSUtils.isEmptyOrNullString( fromAddress ) )
//                    	sender = fromAddress;
//                    license = aPreference.whdLicenseName();
//                }
//
//                if( AppProperties.isHosted() ) {
//                    sender = "do_not_reply@webhelpdesk.com";
//                    license += " (Hosted)";
//                }
//                else if( isDemoMode() ) {
//                    sender = "do_not_reply@webhelpdesk.com";
//                    license += " (Demo)";
//                }
//
//                String subject = "WHD v" + version() + " Bug Report from " + license;
//                String header = "Web Help Desk Exception Report\n"
//                    + "Version: " + version() + "\n"
//                    + "License: " + license + "\n"
//                    + "Host: " + aContext.request().headerForKey( "host" ) + "\n"
//                    + "Date: " + aPreference.defaultBusinessZone().dateTimeFormatter().format( new NSTimestamp() )
//                    + " " + aPreference.defaultBusinessZone().dateTimeFormatter().defaultFormatTimeZone().abbreviation() + "\n";
//
//                String contextString = "Context:\n  " + aContext + "\n";
//
//                JavaSystemProperties props = new JavaSystemProperties( aContext );
//                String javaInfo = "System Properties:\n"
//                    + "  Java version: " + props.javaVersion() + "\n"
//                    + "  Java home: " + props.javaHome() + "\n"
//                    + "  Java ext dirs: " + props.javaExtDirs() + "\n"
//                    + "  Java classpath: " + props.javaClasspath() + "\n"
//                    + "  Java graphics env: " + props.javaGraphics() + "\n"
//                    + "  JVM memory: " + props.javaMemoryUsage() + "\n"
//                    + "  Database URL: " + HelpDeskDatabaseConfigurationProvider.currentDescriptor().url();
//
//                String message = header + "\n\n"
//                    + logDescription + "\n\n"
//                    + javaInfo + "\n\n"
//                    + packagesListString() + "\n\n"
//                    + contextString + "\n\n";
//
//                // TO DO: Only send if preference to send exceptions is turned on
//                if( aPreference != null && aPreference.allowBugReportsBoolean() ) {
//                    _logger.info( "Attempting to send bug report to WHD_BugReports@solarwinds.com...");
//                    _logger.info( "  Sender: " + sender );
//                    _logger.info( "  Subject: " + subject );
//                    SmtpServer smtpServer = SmtpServer.defaultSmtpServerForSubscriber( aPreference.subscriber() );
//                    EmailMessageController.composeGenericEmail( sender, smtpServer.friendlyName(),
//																  new NSArray<String>("WHD_BugReports@solarwinds.com"), null, null,
//																  subject, message,  aPreference.charset(), smtpServer );
//                    _logger.info( "Report sent successfully." );
//
//                }
//                else {
//                    if( aPreference == null ) {
//                        _logger.warn( "Bug Report not sent; couldn't find Preference entity for this user." );
//                    }
//                    else {
//                        _logger.warn( "Bug Report not sent; disabled." );
//                    }
//                }
//
//            }
//            catch( Throwable ex ) {
//                ex = MDSUtils.originalException( ex );
//                _logger.error("Error occurred while attempting to generate bug report for exception '" + t + "':\n"
//							  + ex );
//                failureToSendMessage = ex.toString();
//            }
//
//            
//            // return an error page to the user.
//            ErrorPage nextPage = pageWithName( ErrorPage.class, aContext );
//            nextPage.errorMessage = t.toString();
//            nextPage.failureToSendMessage = failureToSendMessage;
//			nextPage.stackTrace = logDescription;
//            errorPage = nextPage;
//
//        }
//        WOResponse response = errorPage.generateResponse();
//        return response;
//	}
//
//
//    public String version()
//    {
//        return version;
//    }
//
//    public void setClientKey( String newClientKey )
//    {
//        clientKey = newClientKey;
//    }
//
//    public String clientKey()
//    {
//        return clientKey;
//    }
//
//    public boolean isUsingUserNameForLoginAttribute() {
//        Number loginAttributeId = Integer.valueOf(Preference.LOGIN_ATTRIBUTE_EMAIL);
//        if( ! AppProperties.isHosted() ) {
//        	EOEditingContext ec = MDSEditingContext.newEditingContext( "applicationEc" );
//            NSArray<Preference> prefs = MDSDatabaseUtils.objectsForEntityNamed( ec, Preference.class );
//            if( prefs.count() > 0 ) {
//                loginAttributeId = prefs.objectAtIndex(0).loginAttribute();
//                if( loginAttributeId == null )
//                    loginAttributeId = Integer.valueOf(Preference.LOGIN_ATTRIBUTE_EMAIL);
//            }
//        }
//        boolean isUsingUserName = (Integer.valueOf(Preference.LOGIN_ATTRIBUTE_USER_NAME)).equals( loginAttributeId );
//        //System.out.println(" isUsingUserNameForLoginAttribute = " + isUsingUserName );
//        return isUsingUserName;
//    }
//
//    private MDSHtmlFormatter _mdsHtmlFormatter = null;
//    public synchronized MDSHtmlFormatter htmlFormatter() {
//        if( _mdsHtmlFormatter == null )
//            _mdsHtmlFormatter = new MDSHtmlFormatter();
//        return _mdsHtmlFormatter;
//    }
//
//    private MDSWhitespaceOnlyHtmlFormatter _mdsWhitespaceOnlyHtmlFormatter = null;
//    public synchronized MDSWhitespaceOnlyHtmlFormatter whitespaceOnlyHtmlFormatter() {
//        if( _mdsWhitespaceOnlyHtmlFormatter == null )
//            _mdsWhitespaceOnlyHtmlFormatter = new MDSWhitespaceOnlyHtmlFormatter();
//        return _mdsWhitespaceOnlyHtmlFormatter;
//    }
//
//    private MDSMinutesFormatter _mdsMinutesFormatter = null;
//	/** @deprecated -- should create a minutes formatter based on a business zone	*/
//    public synchronized MDSMinutesFormatter minutesFormatter() {
//        if( _mdsMinutesFormatter == null )
//            _mdsMinutesFormatter = new MDSMinutesFormatter();
//        return _mdsMinutesFormatter;
//    }
//
//    private MDSUrlSanitizerFormatter _mdsUrlSanitizerFormatter = null;
//    public synchronized MDSUrlSanitizerFormatter urlSanitizerFormatter() {
//        if( _mdsUrlSanitizerFormatter == null )
//            _mdsUrlSanitizerFormatter = new MDSUrlSanitizerFormatter();
//        return _mdsUrlSanitizerFormatter;
//    }
//
//    private MDSUpperCaseFormatter _mdsUpperCaseFormatter = null;
//    public synchronized MDSUpperCaseFormatter mdsUpperCaseFormatter() {
//        if( _mdsUpperCaseFormatter == null )
//            _mdsUpperCaseFormatter = new MDSUpperCaseFormatter();
//        return _mdsUpperCaseFormatter;
//    }
//
//	public WordBreakerFormatter _wordBreakerFormatter = null;
//	public synchronized WordBreakerFormatter wordBreakerFormatter() {
//		if( _wordBreakerFormatter == null )
//			_wordBreakerFormatter = new WordBreakerFormatter( 60 );
//		return _wordBreakerFormatter;
//	}
//
//	// IMPORTANT! Do not remove or rename bbCodeFormatter, bbCodeTextFormatter, or bbCodeFOFormatter;
//	// they are obtained in many pages by calling WOApplication.application.valueForKey( "bbCodeTextFormatter" )
//	private BBCodeTextFormatter _bbCodeTextFormatter = null;
//	public BBCodeTextFormatter bbCodeTextFormatter() {
//		if( _bbCodeTextFormatter == null ) {
//			_bbCodeTextFormatter = new BBCodeTextFormatter();
//		}
//		return _bbCodeTextFormatter;
//	}
//
//	private BBCodeFormatter _bbCodePermissiveFormatter = null;
//	public BBCodeFormatter bbCodePermissiveFormatter() {
//		if( _bbCodePermissiveFormatter == null ) {
//			_bbCodePermissiveFormatter = new BBCodeFormatter( false, true );
//			
//		}
//		return _bbCodePermissiveFormatter;
//	}
//
//	private BBCodeFormatter _bbCodeFormatter = null;
//	public BBCodeFormatter bbCodeFormatter() {
//		if( _bbCodeFormatter == null ) {
//			_bbCodeFormatter = new BBCodeFormatter( false );
//		}
//		return _bbCodeFormatter;
//	}
//
//	private BBCodeFOFormatter _bbCodeFOFormatter = null;
//	public BBCodeFOFormatter bbCodeFOFormatter() {
//		if( _bbCodeFOFormatter == null ) {
//			_bbCodeFOFormatter = new BBCodeFOFormatter();
//		}
//		return _bbCodeFOFormatter;
//	}
//	
//	private Format _newlineToHtmlBreakFormatter = null;
//	public Format newlineToHtmlBreakFormatter() {
//		if ( _newlineToHtmlBreakFormatter == null ) {
//			_newlineToHtmlBreakFormatter = NewlineToHtmlBreakFormatter.instance();
//		}
//		return _newlineToHtmlBreakFormatter;
//	}



//	public void setSessionStore(WOSessionStore wosessionstore) {
//		super.setSessionStore( wosessionstore );
//		//_logger.debug( "Set session store of class " + wosessionstore.getClass().getName(), new Exception( "STACK TRACE" ) );
//	}
//	public WOSession restoreSessionWithID( String aSessionID, WOContext aContext ) {
//		/*
//		 // This won't work because we don't want to abort all sessions, only those of techs logged in
//		 // after another tech logs in successfully (when running under a 1-tech license)
//		if( activeSessionId() != null ) {
//			if( aSessionID != null && ! aSessionID.equals( activeSessionId() ) )
//				throw new RuntimeException( "Session is no longer active." );
//		}
//		 _logger.debug( "Application.restoreSessionWithID(): Using session store of class " + sessionStore().getClass().getName() + "..." );
//		 _logger.debug( "Current sessions: " + ((com.webobjects.appserver._private.WOServerSessionStore)sessionStore())._sessions() );
//		 _logger.debug( "Current sessions (using static method): " + ((com.webobjects.appserver._private.WOServerSessionStore)WOSessionStore.serverSessionStore())._sessions() );
//		 */
//
//		if( _inactiveSessions.containsObject( aSessionID ) ) {
//			throw new SessionLimitExceededException();
//		}
//
//		return super.restoreSessionWithID( aSessionID, aContext );
//	}
//
//	private static String _activeSessionId;
//	public String activeSessionId() {
//		return _activeSessionId;
//	}
//
//	private static NSMutableArray<String> _inactiveSessions = new NSMutableArray<String>();
//	public void setActiveSessionId( String sessionId ) {
//		_logger.warn( "Replacing active session " + _activeSessionId + " with " + sessionId );
//
//		if( sessionId != null ) {
//			if( _activeSessionId != null && ! _activeSessionId.equals( sessionId ) ) {
//				_inactiveSessions.addObject( _activeSessionId );
//				//_logger.debug( "Current sessions: " + ((com.webobjects.appserver._private.WOServerSessionStore)sessionStore())._sessions() );
//				WOSession session = sessionStore().removeSessionWithID( _activeSessionId );
//				if( session != null ) {
//					_logger.debug( "Terminating session..." );
//					session.terminate();
//				}
//				else
//					_logger.debug( "Could not get session object for termination." );
//			}
//		}
//
//		_activeSessionId = sessionId;
//	}
//
//	/**
//	 * Overrides the parent method to fix problem where ProcessChangesQueue has already
//	 * been shut down.
//	 */
//	@Override
//	public void terminate() {
//		//NSNotificationCenter.defaultCenter().postNotification("ApplicationWillTerminateNotification", this);
//		try {
//			super.terminate();
//		}
//		catch( IllegalStateException e ) {
//			// likely because the ProcessChangesQueue was already shut down
//			_logger.warn( "Unable to shut down ProcessChangesQueue", e );
//			
//			// Remove the ApplicationWillTerminateNotification observer & try again
//			NSNotificationCenter.defaultCenter().removeObserver(null, "ApplicationWillTerminateNotification", null);
//			super.terminate();
//		}
//	}
//	
//	
//	public void performRefresh() {
//		synchronized( clientStylesheetCache ) {
//			clientStylesheetCache.removeAllObjects();
//		}
//	}
//
//	
//	private static NSMutableDictionary<Number,String> clientStylesheetCache = new NSMutableDictionary<Number,String>();
//	public String cachedClientStylesheetForSubscriberId( Number subscriberId ) {
//		synchronized( clientStylesheetCache ) {
//			return /*AppProperties.isTestMode() ? null :*/ clientStylesheetCache.objectForKey( subscriberId );
//		}
//	}
//	public String cacheClientStylesheet( Stylesheet stylesheet, Number subscriberId ) {
//		synchronized( clientStylesheetCache ) {
//			String generatedStylesheet = StylesheetHelper.generateStylesheet(stylesheet);
//			if( generatedStylesheet != null )
//				clientStylesheetCache.setObjectForKey( generatedStylesheet, subscriberId );
//			else
//				clientStylesheetCache.removeObjectForKey( subscriberId );
//			
//			return generatedStylesheet;
//		}
//	}
//	
//	public boolean outgoingMailHistoryEnabled() {
//		return AppProperties.outgoingMailHistoryEnabled();
//	}
}
