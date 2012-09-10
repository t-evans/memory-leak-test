// DO NOT EDIT.  Make changes to DataStore2.java instead.
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
public abstract class _DataStore2 extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "DataStore2";

  // Solr UserInfo


  // Attribute Keys
  public static final ERXKey<NSData> DATA = new ERXKey<NSData>("data");
  // Relationship Keys
  public static final ERXKey<com.test.entity.DataContainer> DATA_CONTAINER = new ERXKey<com.test.entity.DataContainer>("dataContainer");

  // Attributes
  public static final String DATA_KEY = DATA.key();
  // Relationships
  public static final String DATA_CONTAINER_KEY = DATA_CONTAINER.key();

  private static Logger LOG = Logger.getLogger(_DataStore2.class);

  public DataStore2 localInstanceIn(EOEditingContext editingContext) {
    DataStore2 localInstance = (DataStore2)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public NSData data() {
    return (NSData) storedValueForKey(_DataStore2.DATA_KEY);
  }

  public void setData(NSData value) {
    if (_DataStore2.LOG.isDebugEnabled()) {
    	_DataStore2.LOG.debug( "updating data from " + data() + " to " + value);
    }
    takeStoredValueForKey(value, _DataStore2.DATA_KEY);
  }

  public com.test.entity.DataContainer dataContainer() {
    return (com.test.entity.DataContainer)storedValueForKey(_DataStore2.DATA_CONTAINER_KEY);
  }
  
  public void setDataContainer(com.test.entity.DataContainer value) {
    takeStoredValueForKey(value, _DataStore2.DATA_CONTAINER_KEY);
  }

  public void setDataContainerRelationship(com.test.entity.DataContainer value) {
    if (_DataStore2.LOG.isDebugEnabled()) {
      _DataStore2.LOG.debug("updating dataContainer from " + dataContainer() + " to " + value);
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	setDataContainer(value);
    }
    else if (value == null) {
    	com.test.entity.DataContainer oldValue = dataContainer();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _DataStore2.DATA_CONTAINER_KEY);
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, _DataStore2.DATA_CONTAINER_KEY);
    }
  }
  

  public static DataStore2 createDataStore2(EOEditingContext editingContext) {
    DataStore2 eo = (DataStore2) EOUtilities.createAndInsertInstance(editingContext, _DataStore2.ENTITY_NAME);    
    return eo;
  }

  public static ERXFetchSpecification<DataStore2> fetchSpec() {
    return new ERXFetchSpecification<DataStore2>(_DataStore2.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<DataStore2> fetchAllDataStore2s(EOEditingContext editingContext) {
    return _DataStore2.fetchAllDataStore2s(editingContext, null);
  }

  public static NSArray<DataStore2> fetchAllDataStore2s(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _DataStore2.fetchDataStore2s(editingContext, null, sortOrderings);
  }

  public static NSArray<DataStore2> fetchDataStore2s(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<DataStore2> fetchSpec = new ERXFetchSpecification<DataStore2>(_DataStore2.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<DataStore2> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static DataStore2 fetchDataStore2(EOEditingContext editingContext, String keyName, Object value) {
    return _DataStore2.fetchDataStore2(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static DataStore2 fetchDataStore2(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<DataStore2> eoObjects = _DataStore2.fetchDataStore2s(editingContext, qualifier, null);
    DataStore2 eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one DataStore2 that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static DataStore2 fetchRequiredDataStore2(EOEditingContext editingContext, String keyName, Object value) {
    return _DataStore2.fetchRequiredDataStore2(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static DataStore2 fetchRequiredDataStore2(EOEditingContext editingContext, EOQualifier qualifier) {
    DataStore2 eoObject = _DataStore2.fetchDataStore2(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no DataStore2 that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static DataStore2 localInstanceIn(EOEditingContext editingContext, DataStore2 eo) {
    DataStore2 localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
