package de.diedavids.cuba.dataimport.data;

import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Entity;

import java.util.Collection;
import java.util.UUID;

public interface SimpleDataLoader {


    String NAME = "ddcdi_SimpleDataLoader";

    <E extends BaseUuidEntity> E load(Class<E> entityClass, UUID id);
    <E extends BaseUuidEntity> E load(Class<E> entityClass, UUID id, String view);

    <E extends BaseUuidEntity> E loadByProperty(Class<E> entityClass, String propertyPath, Object propertyValue);
    <E extends BaseUuidEntity> E loadByProperty(Class<E> entityClass, String propertyPath, Object propertyValue, String view);

    <E extends BaseUuidEntity> E loadByReference(Class<E> entityClass, String propertyPath, BaseUuidEntity reference);
    <E extends BaseUuidEntity> E loadByReference(Class<E> entityClass, String propertyPath, BaseUuidEntity reference, String view);

    <E extends BaseUuidEntity> Collection<E> loadAll(Class<E> entityClass);
    <E extends BaseUuidEntity> Collection<E> loadAll(Class<E> entityClass, String view);


    <E extends Entity> Collection<E> loadAllByAttributes(Class<E> entityClass, Collection<EntityAttributeValue> entityAttributeValues);

    <E extends BaseUuidEntity> Collection<E> loadAllByProperty(Class<E> entityClass, String propertyPath, Object propertyValue);
    <E extends BaseUuidEntity> Collection<E> loadAllByProperty(Class<E> entityClass, String propertyPath, Object propertyValue, String view);


}
