// DO NOT EDIT.  Make changes to DataContainer.java instead.
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
public abstract class _DataContainer extends  ERXGenericRecord {
  public static final String ENTITY_NAME = "DataContainer";

  // Solr UserInfo


  // Attribute Keys
  public static final ERXKey<Integer> ID = new ERXKey<Integer>("id");
  public static final ERXKey<String> MESSAGE = new ERXKey<String>("message");
  public static final ERXKey<String> NAME = new ERXKey<String>("name");
  // Relationship Keys
  public static final ERXKey<com.test.entity.DataStore2> DATA_STORE2S = new ERXKey<com.test.entity.DataStore2>("dataStore2s");
  public static final ERXKey<com.test.entity.DataStore> DATA_STORES = new ERXKey<com.test.entity.DataStore>("dataStores");
  public static final ERXKey<com.test.entity.DataStore> MAIN_DATA_STORE = new ERXKey<com.test.entity.DataStore>("mainDataStore");

  // Attributes
  public static final String ID_KEY = ID.key();
  public static final String MESSAGE_KEY = MESSAGE.key();
  public static final String NAME_KEY = NAME.key();
  // Relationships
  public static final String DATA_STORE2S_KEY = DATA_STORE2S.key();
  public static final String DATA_STORES_KEY = DATA_STORES.key();
  public static final String MAIN_DATA_STORE_KEY = MAIN_DATA_STORE.key();

  private static Logger LOG = Logger.getLogger(_DataContainer.class);

  public DataContainer localInstanceIn(EOEditingContext editingContext) {
    DataContainer localInstance = (DataContainer)EOUtilities.localInstanceOfObject(editingContext, this);
    if (localInstance == null) {
      throw new IllegalStateException("You attempted to localInstance " + this + ", which has not yet committed.");
    }
    return localInstance;
  }

  public Integer id() {
    return (Integer) storedValueForKey(_DataContainer.ID_KEY);
  }

  public void setId(Integer value) {
    if (_DataContainer.LOG.isDebugEnabled()) {
    	_DataContainer.LOG.debug( "updating id from " + id() + " to " + value);
    }
    takeStoredValueForKey(value, _DataContainer.ID_KEY);
  }

  public String message() {
    return (String) storedValueForKey(_DataContainer.MESSAGE_KEY);
  }

  public void setMessage(String value) {
    if (_DataContainer.LOG.isDebugEnabled()) {
    	_DataContainer.LOG.debug( "updating message from " + message() + " to " + value);
    }
    takeStoredValueForKey(value, _DataContainer.MESSAGE_KEY);
  }

  public String name() {
    return (String) storedValueForKey(_DataContainer.NAME_KEY);
  }

  public void setName(String value) {
    if (_DataContainer.LOG.isDebugEnabled()) {
    	_DataContainer.LOG.debug( "updating name from " + name() + " to " + value);
    }
    takeStoredValueForKey(value, _DataContainer.NAME_KEY);
  }

  public com.test.entity.DataStore mainDataStore() {
    return (com.test.entity.DataStore)storedValueForKey(_DataContainer.MAIN_DATA_STORE_KEY);
  }
  
  public void setMainDataStore(com.test.entity.DataStore value) {
    takeStoredValueForKey(value, _DataContainer.MAIN_DATA_STORE_KEY);
  }

  public void setMainDataStoreRelationship(com.test.entity.DataStore value) {
    if (_DataContainer.LOG.isDebugEnabled()) {
      _DataContainer.LOG.debug("updating mainDataStore from " + mainDataStore() + " to " + value);
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	setMainDataStore(value);
    }
    else if (value == null) {
    	com.test.entity.DataStore oldValue = mainDataStore();
    	if (oldValue != null) {
    		removeObjectFromBothSidesOfRelationshipWithKey(oldValue, _DataContainer.MAIN_DATA_STORE_KEY);
      }
    } else {
    	addObjectToBothSidesOfRelationshipWithKey(value, _DataContainer.MAIN_DATA_STORE_KEY);
    }
  }
  
  public NSArray<com.test.entity.DataStore2> dataStore2s() {
    return (NSArray<com.test.entity.DataStore2>)storedValueForKey(_DataContainer.DATA_STORE2S_KEY);
  }

  public NSArray<com.test.entity.DataStore2> dataStore2s(EOQualifier qualifier) {
    return dataStore2s(qualifier, null, false);
  }

  public NSArray<com.test.entity.DataStore2> dataStore2s(EOQualifier qualifier, boolean fetch) {
    return dataStore2s(qualifier, null, fetch);
  }

  public NSArray<com.test.entity.DataStore2> dataStore2s(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<com.test.entity.DataStore2> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = new EOKeyValueQualifier(com.test.entity.DataStore2.DATA_CONTAINER_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray<EOQualifier> qualifiers = new NSMutableArray<EOQualifier>();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
      }

      results = com.test.entity.DataStore2.fetchDataStore2s(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = dataStore2s();
      if (qualifier != null) {
        results = (NSArray<com.test.entity.DataStore2>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<com.test.entity.DataStore2>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }
  
  public void addToDataStore2s(com.test.entity.DataStore2 object) {
    includeObjectIntoPropertyWithKey(object, _DataContainer.DATA_STORE2S_KEY);
  }

  public void removeFromDataStore2s(com.test.entity.DataStore2 object) {
    excludeObjectFromPropertyWithKey(object, _DataContainer.DATA_STORE2S_KEY);
  }

  public void addToDataStore2sRelationship(com.test.entity.DataStore2 object) {
    if (_DataContainer.LOG.isDebugEnabled()) {
      _DataContainer.LOG.debug("adding " + object + " to dataStore2s relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	addToDataStore2s(object);
    }
    else {
    	addObjectToBothSidesOfRelationshipWithKey(object, _DataContainer.DATA_STORE2S_KEY);
    }
  }

  public void removeFromDataStore2sRelationship(com.test.entity.DataStore2 object) {
    if (_DataContainer.LOG.isDebugEnabled()) {
      _DataContainer.LOG.debug("removing " + object + " from dataStore2s relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	removeFromDataStore2s(object);
    }
    else {
    	removeObjectFromBothSidesOfRelationshipWithKey(object, _DataContainer.DATA_STORE2S_KEY);
    }
  }

  public com.test.entity.DataStore2 createDataStore2sRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName( com.test.entity.DataStore2.ENTITY_NAME );
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, _DataContainer.DATA_STORE2S_KEY);
    return (com.test.entity.DataStore2) eo;
  }

  public void deleteDataStore2sRelationship(com.test.entity.DataStore2 object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _DataContainer.DATA_STORE2S_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllDataStore2sRelationships() {
    Enumeration<com.test.entity.DataStore2> objects = dataStore2s().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteDataStore2sRelationship(objects.nextElement());
    }
  }

  public NSArray<com.test.entity.DataStore> dataStores() {
    return (NSArray<com.test.entity.DataStore>)storedValueForKey(_DataContainer.DATA_STORES_KEY);
  }

  public NSArray<com.test.entity.DataStore> dataStores(EOQualifier qualifier) {
    return dataStores(qualifier, null, false);
  }

  public NSArray<com.test.entity.DataStore> dataStores(EOQualifier qualifier, boolean fetch) {
    return dataStores(qualifier, null, fetch);
  }

  public NSArray<com.test.entity.DataStore> dataStores(EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings, boolean fetch) {
    NSArray<com.test.entity.DataStore> results;
    if (fetch) {
      EOQualifier fullQualifier;
      EOQualifier inverseQualifier = new EOKeyValueQualifier(com.test.entity.DataStore.DATA_CONTAINER_KEY, EOQualifier.QualifierOperatorEqual, this);
    	
      if (qualifier == null) {
        fullQualifier = inverseQualifier;
      }
      else {
        NSMutableArray<EOQualifier> qualifiers = new NSMutableArray<EOQualifier>();
        qualifiers.addObject(qualifier);
        qualifiers.addObject(inverseQualifier);
        fullQualifier = new EOAndQualifier(qualifiers);
      }

      results = com.test.entity.DataStore.fetchDataStores(editingContext(), fullQualifier, sortOrderings);
    }
    else {
      results = dataStores();
      if (qualifier != null) {
        results = (NSArray<com.test.entity.DataStore>)EOQualifier.filteredArrayWithQualifier(results, qualifier);
      }
      if (sortOrderings != null) {
        results = (NSArray<com.test.entity.DataStore>)EOSortOrdering.sortedArrayUsingKeyOrderArray(results, sortOrderings);
      }
    }
    return results;
  }
  
  public void addToDataStores(com.test.entity.DataStore object) {
    includeObjectIntoPropertyWithKey(object, _DataContainer.DATA_STORES_KEY);
  }

  public void removeFromDataStores(com.test.entity.DataStore object) {
    excludeObjectFromPropertyWithKey(object, _DataContainer.DATA_STORES_KEY);
  }

  public void addToDataStoresRelationship(com.test.entity.DataStore object) {
    if (_DataContainer.LOG.isDebugEnabled()) {
      _DataContainer.LOG.debug("adding " + object + " to dataStores relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	addToDataStores(object);
    }
    else {
    	addObjectToBothSidesOfRelationshipWithKey(object, _DataContainer.DATA_STORES_KEY);
    }
  }

  public void removeFromDataStoresRelationship(com.test.entity.DataStore object) {
    if (_DataContainer.LOG.isDebugEnabled()) {
      _DataContainer.LOG.debug("removing " + object + " from dataStores relationship");
    }
    if (er.extensions.eof.ERXGenericRecord.InverseRelationshipUpdater.updateInverseRelationships()) {
    	removeFromDataStores(object);
    }
    else {
    	removeObjectFromBothSidesOfRelationshipWithKey(object, _DataContainer.DATA_STORES_KEY);
    }
  }

  public com.test.entity.DataStore createDataStoresRelationship() {
    EOClassDescription eoClassDesc = EOClassDescription.classDescriptionForEntityName( com.test.entity.DataStore.ENTITY_NAME );
    EOEnterpriseObject eo = eoClassDesc.createInstanceWithEditingContext(editingContext(), null);
    editingContext().insertObject(eo);
    addObjectToBothSidesOfRelationshipWithKey(eo, _DataContainer.DATA_STORES_KEY);
    return (com.test.entity.DataStore) eo;
  }

  public void deleteDataStoresRelationship(com.test.entity.DataStore object) {
    removeObjectFromBothSidesOfRelationshipWithKey(object, _DataContainer.DATA_STORES_KEY);
    editingContext().deleteObject(object);
  }

  public void deleteAllDataStoresRelationships() {
    Enumeration<com.test.entity.DataStore> objects = dataStores().immutableClone().objectEnumerator();
    while (objects.hasMoreElements()) {
      deleteDataStoresRelationship(objects.nextElement());
    }
  }


  public static DataContainer createDataContainer(EOEditingContext editingContext, Integer id
) {
    DataContainer eo = (DataContainer) EOUtilities.createAndInsertInstance(editingContext, _DataContainer.ENTITY_NAME);    
		eo.setId(id);
    return eo;
  }

  public static ERXFetchSpecification<DataContainer> fetchSpec() {
    return new ERXFetchSpecification<DataContainer>(_DataContainer.ENTITY_NAME, null, null, false, true, null);
  }

  public static NSArray<DataContainer> fetchAllDataContainers(EOEditingContext editingContext) {
    return _DataContainer.fetchAllDataContainers(editingContext, null);
  }

  public static NSArray<DataContainer> fetchAllDataContainers(EOEditingContext editingContext, NSArray<EOSortOrdering> sortOrderings) {
    return _DataContainer.fetchDataContainers(editingContext, null, sortOrderings);
  }

  public static NSArray<DataContainer> fetchDataContainers(EOEditingContext editingContext, EOQualifier qualifier, NSArray<EOSortOrdering> sortOrderings) {
    ERXFetchSpecification<DataContainer> fetchSpec = new ERXFetchSpecification<DataContainer>(_DataContainer.ENTITY_NAME, qualifier, sortOrderings);
    fetchSpec.setIsDeep(true);
    NSArray<DataContainer> eoObjects = fetchSpec.fetchObjects(editingContext);
    return eoObjects;
  }

  public static DataContainer fetchDataContainer(EOEditingContext editingContext, String keyName, Object value) {
    return _DataContainer.fetchDataContainer(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static DataContainer fetchDataContainer(EOEditingContext editingContext, EOQualifier qualifier) {
    NSArray<DataContainer> eoObjects = _DataContainer.fetchDataContainers(editingContext, qualifier, null);
    DataContainer eoObject;
    int count = eoObjects.count();
    if (count == 0) {
      eoObject = null;
    }
    else if (count == 1) {
      eoObject = eoObjects.objectAtIndex(0);
    }
    else {
      throw new IllegalStateException("There was more than one DataContainer that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static DataContainer fetchRequiredDataContainer(EOEditingContext editingContext, String keyName, Object value) {
    return _DataContainer.fetchRequiredDataContainer(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
  }

  public static DataContainer fetchRequiredDataContainer(EOEditingContext editingContext, EOQualifier qualifier) {
    DataContainer eoObject = _DataContainer.fetchDataContainer(editingContext, qualifier);
    if (eoObject == null) {
      throw new NoSuchElementException("There was no DataContainer that matched the qualifier '" + qualifier + "'.");
    }
    return eoObject;
  }

  public static DataContainer localInstanceIn(EOEditingContext editingContext, DataContainer eo) {
    DataContainer localInstance = (eo == null) ? null : ERXEOControlUtilities.localInstanceOfObject(editingContext, eo);
    if (localInstance == null && eo != null) {
      throw new IllegalStateException("You attempted to localInstance " + eo + ", which has not yet committed.");
    }
    return localInstance;
  }
}
