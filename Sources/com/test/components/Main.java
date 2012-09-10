package com.test.components;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import com.test.entity.DataContainer;
import com.test.entity.DataStore;
import com.test.entity.DataStore2;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOObjectStore;
import com.webobjects.foundation.NSData;

import er.extensions.components.ERXComponent;
import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXObjectStoreCoordinator;

public class Main extends ERXComponent {
	public Main(WOContext context) {
		super(context);
	}

	public WOActionResults createDataStore() throws IOException, MessagingException {
		File emailFile = new File("Resources/email.eml");
		javax.mail.Message message = convertEmlToMessage( emailFile );
		
		EOObjectStore osc = new ERXObjectStoreCoordinator(true);
		EOEditingContext ec = ERXEC.newEditingContext(osc);
		ec.lock();
		try {
			DataContainer container = (DataContainer) EOUtilities.createAndInsertInstance(ec, DataContainer.class.getSimpleName());
			ec.insertObject(container);
			
			DataStore dataStore = (DataStore) EOUtilities.createAndInsertInstance(ec, DataStore.class.getSimpleName());
			ec.insertObject(dataStore);
			dataStore.setDataContainer(container);
			
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			message.writeTo( byteStream );
			NSData rawEmail = new NSData( byteStream.toByteArray() );
			dataStore.setData(rawEmail);
			
	
			ec.saveChanges();
		}
		finally {
			ec.unlock();
			ec.dispose();
			osc.dispose();
			ec = null;
			osc = null;
		}
		return null;
	}

	private static Session _dummySession;
	private static Session dummySession() {
		if ( _dummySession == null ) {
			Properties props = new Properties();
			props.put("mail.host", "smtp.dummyDomain.com");
			props.put("mail.transport.protocol", "smtp");
			_dummySession = Session.getDefaultInstance(props, null);
		}
		return _dummySession;
	}

	private Message convertEmlToMessage(File emailFile) {
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream( emailFile );
			return new MimeMessage( dummySession(), fileInputStream );
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public WOActionResults createDataStore2() {
		EOObjectStore osc = new ERXObjectStoreCoordinator(true);
		EOEditingContext ec = ERXEC.newEditingContext(osc, true);
		DataContainer container = new DataContainer();
		ec.insertObject(container);
		container.setName("Test");
		
		DataStore2 dataStore = new DataStore2();
		ec.insertObject(dataStore);
		DataStore2 dataStore2 = new DataStore2();
		ec.insertObject(dataStore2);
		
		dataStore.addObjectToBothSidesOfRelationshipWithKey(container, "dataContainer");
		dataStore2.addObjectToBothSidesOfRelationshipWithKey(container, "dataContainer");
		
		NSData data = null;
		NSData data2 = null;
		try {
			data = new NSData(new File("Resources/email.eml"));
			data2 = new NSData(new File("Resources/email2.eml"));
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataStore.setData(data);
		dataStore2.setData(data2);
		
		ec.saveChanges();
		ec.dispose();
		osc.dispose();
		ec = null;
		osc = null;
		return null;
	}
	
	public WOActionResults createEmailHistoryEntry() throws MessagingException, IOException {
//		File emailFile = new File("Resources/email.eml");
//		javax.mail.Message message = convertEmlToMessage( emailFile );
//		
//		EOObjectStore osc = new ERXObjectStoreCoordinator(true);
//		EOEditingContext ec = ERXEC.newEditingContext(osc);
//		ec.lock();
//		try {
//			EmailHistoryEntry historyEntry = (EmailHistoryEntry) EOUtilities.createAndInsertInstance( ec, EmailHistoryEntry.class.getSimpleName() );
//			EmailDataObject emailData = (EmailDataObject) EOUtilities.createAndInsertInstance( ec, EmailDataObject.class.getSimpleName() );
//			emailData.setEmailHistoryEntry(historyEntry);
//			
//			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//			message.writeTo( byteStream );
//			NSData rawEmail = new NSData( byteStream.toByteArray() );
//			emailData.setRawEmail(rawEmail);
//			
//			ec.saveChanges();
//		}
//		finally {
//			ec.dispose();
//			osc.dispose();
//			ec = null;
//			osc = null;
//		}
		return null;
	}
}
