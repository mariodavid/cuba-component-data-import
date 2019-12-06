package de.diedavids.cuba.dataimport.data;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.View;

import java.util.Collection;
import java.util.UUID;

public interface SimpleDataLoader {


    String NAME = "ddcdi_SimpleDataLoader";

    <E extends Entity> E load(Class<E> entityClass, UUID id);
    <E extends Entity> E load(Class<E> entityClass, UUID id, String view);

    <E extends Entity> E loadByProperty(Class<E> entityClass, String propertyPath, Object propertyValue);
    <E extends Entity> E loadByProperty(Class<E> entityClass, String propertyPath, Object propertyValue, String view);

    <E extends Entity> E loadByReference(Class<E> entityClass, String propertyPath, Entity reference);
    <E extends Entity> E loadByReference(Class<E> entityClass, String propertyPath, Entity reference, String view);

    <E extends Entity> Collection<E> loadAll(Class<E> entityClass);
    <E extends Entity> Collection<E> loadAll(Class<E> entityClass, String view);


    <E extends Entity> Collection<E> loadAllByAttributes(Class<E> entityClass, Collection<EntityAttributeValue> entityAttributeValues);
    <E extends Entity> Collection<E> loadAllByAttributes(Class<E> entityClass, Collection<EntityAttributeValue> entityAttributeValues, String view);
    <E extends Entity> Collection<E> loadAllByAttributes(Class<E> entityClass, Collection<EntityAttributeValue> entityAttributeValues, View view);

    <E extends Entity> Collection<E> loadAllByProperty(Class<E> entityClass, String propertyPath, Object propertyValue);
    <E extends Entity> Collection<E> loadAllByProperty(Class<E> entityClass, String propertyPath, Object propertyValue, String view);


}
