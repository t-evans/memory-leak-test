// DO NOT EDIT.  Make changes to DataStore.java instead.
package com.test.entity;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import java.math.*;
import java.util.*;
import org.apache.log4j.Logger;

import er.extensions.eof.*;
import er.extensions.foundation.*;

@SuppressWarnings("all")
public abstract class _DataStore extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "DataStore";

  // Attribute Keys
  public static final ERXKey<NSData> DATA = new ERXKey<NSData>("data");
  // Relationship Keys
  public static final ERXKey<com.test.entity.DataContainer> DATA_CONTAINER = new ERXKey<com.test.entity.DataContainer>("dataContainer");

  // Attributes
  public static final String DATA_KEY = DATA.key();
  // Relationships
  public static final String DATA_CONTAINER_KEY = DATA_CONTAINER.key();

  private static Logger LOG = Logger.getLogger(_DataStore.class);

  public DataStore localInstanceIn(EOEditingContext editingContext) {
    DataStore localInstance = (DataStore)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSData data() {
    return (NSData) storedValueForKey(_DataStore.DATA_KEY);
  }

  public void setData(NSData value) {
    if (_DataStore.LOG.isDebugEnabled()) {
    	_DataStore.LOG.debug( "updating data from " + data() + " to " + value);
    }
    takeStoredValueForKey(value, _DataStore.DATA_KEY);
  }

  public com.test.entity.DataContainer dataContainer() {
    return (com.test.entity.DataContainer)storedValueForKey(_DataStore.DATA_CONTAINER_KEY);
  }
  
  public void setDataContainer(com.test.entity.DataContainer value) {
    takeStoredValueForKey(value, _DataStore.DATA_CONTAINER_KEY);
  }

  public void setDataContainerRelationship(com.test.entity.DataContainer value) {
    if (_DataStore.LOG.isDebugEnabled()) {
      _DataStore.LOG.debug("updating dataContainer from " + dataContainer() + " to " + value);
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	setDataContainer(value);
    }
    else if (value == null) {
    	com.test.entity.DataContainer oldValue = dataContainer();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _DataStore.DATA_CONTAINER_KEY);
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, _DataStore.DATA_CONTAINER_KEY);
    }
  }
  

  public static DataStore createDataStore(EOEditingContext editingContext, NSData data
) {
    DataStore eo = (DataStore) EOUtilities.createAndInsertInstance(editingContext, _DataStore.ENTITY_NAME);    
		eo.setData(data);
    return eo;
  }

  public static ERXFetchSpecification<DataStore> fetchSpec() {
    return new ERXFetchSpecification<DataStore>(_DataStore.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<DataStore> fetchAllDataStores(EOEditingContext editingContext) {
    return _DataStore.fetchAllDataStores(editingContext, null);
  }

  public static NSArray<DataStore> fetchAllDataStores(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _DataStore.fetchDataStores(editingContext, null, sortOrderings);
  }

  public static NSArray<DataStore> fetchDataStores(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<DataStore> fetchSpec = new ERXFetchSpecification<DataStore>(_DataStore.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<DataStore> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static DataStore fetchDataStore(EOEditingContext editingContext, String keyName, Object value) {
    return _DataStore.fetchDataStore(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static DataStore fetchDataStore(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<DataStore> eoObjects = _DataStore.fetchDataStores(editingContext, qualifier, null);
    DataStore eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one DataStore that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static DataStore fetchRequiredDataStore(EOEditingContext editingContext, String keyName, Object value) {
    return _DataStore.fetchRequiredDataStore(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static DataStore fetchRequiredDataStore(EOEditingContext editingContext, EOQualifier qualifier) {
    DataStore eoObject = _DataStore.fetchDataStore(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no DataStore that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static DataStore localInstanceIn(EOEditingContext editingContext, DataStore eo) {
    DataStore localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
