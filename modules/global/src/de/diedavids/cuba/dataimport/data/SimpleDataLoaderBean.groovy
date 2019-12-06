package de.diedavids.cuba.dataimport.data

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.LoadContext
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.View
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.inject.Inject

@CompileStatic
@Component(SimpleDataLoader.NAME)
class SimpleDataLoaderBean implements SimpleDataLoader {

    @Inject
    DataManager dataManager

    @Inject
    Metadata metadata


    @Override
    <E extends Entity> E load(Class<E> entityClass, UUID id, String view = '_local') {

        LoadContext<E> loadContext = getLoadContext(entityClass)
                .setId(id)
                .setView(view) as LoadContext<E>

        dataManager.load(loadContext) as E
    }


    @Override
    <E extends Entity> E loadByProperty(Class<E> entityClass, String propertyPath, Object propertyValue, String view = '_local') {

        LoadContext.Query query = createQueryByProperty(getMetaClassName(entityClass), propertyPath, propertyValue)
        def loadContext = getLoadContext(entityClass)
                .setQuery(query)
                .setView(view)

        dataManager.load(loadContext) as E
    }


    @Override
    <E extends Entity> E loadByReference(Class<E> entityClass, String propertyPath, Entity reference, String view = '_local') {
        LoadContext.Query query = createQueryByReference(getMetaClassName(entityClass), propertyPath, reference)
        def loadContext = getLoadContext(entityClass)
                .setQuery(query)
                .setView(view)

        dataManager.load(loadContext) as E
    }


    @Override
    <E extends Entity> Collection<E> loadAllByProperty(Class<E> entityClass, String propertyPath, Object propertyValue, String view = '_local') {
        LoadContext.Query query = createQueryByProperty(getMetaClassName(entityClass), propertyPath, propertyValue)
        LoadContext loadContext = getLoadContext(entityClass)
                .setQuery(query)
                .setView(view)
        dataManager.loadList(loadContext) as Collection<E>
    }

    @Override
    <E extends Entity> Collection<E> loadAll(Class<E> entityClass, String view = '_local') {
        LoadContext.Query query = createQueryForSelectAll(getMetaClassName(entityClass))
        LoadContext loadContext = getLoadContext(entityClass)
                .setQuery(query)
                .setView(view)
        dataManager.loadList(loadContext) as Collection<E>
    }

    protected LoadContext.Query createQueryForSelectAll(String metaClassName) {
        LoadContext.createQuery(getSelectPart(metaClassName))
    }

    protected LoadContext.Query createQueryByReference(String metaClassName, String propertyPath, Entity reference) {
        def queryString = "${getSelectPart(metaClassName)} where e.${propertyPath}.id = :referenceId"
        LoadContext.createQuery(queryString).setParameter('referenceId', reference.id)
    }

    protected LoadContext.Query createQueryByProperty(String metaClassName, String propertyPath, Object propertyValue) {
        def queryString = "${getSelectPart(metaClassName)} where e.${propertyPath} = :propertyValue"
        LoadContext.createQuery(queryString).setParameter('propertyValue', propertyValue)
    }

    protected LoadContext.Query createQueryByEntityAttributeValues(String metaClassName, Collection<EntityAttributeValue> entityAttributeValues) {

        def queryString = "${getSelectPart(metaClassName)} where "

        def conditionParts = entityAttributeValues.withIndex().collect { EntityAttributeValue entityAttributeValue, int i ->
            "e.${entityAttributeValue.entityAttribute} = :propertyValue$i"
        }

        queryString += conditionParts.join(' and ')

        LoadContext.Query query = LoadContext.createQuery(queryString)

        entityAttributeValues.eachWithIndex { EntityAttributeValue entityAttributeValue, int i ->
            query.setParameter("propertyValue$i", entityAttributeValue.value)
        }

        query
    }


    @Override
    <E extends Entity> Collection<E> loadAllByAttributes(Class<E> entityClass, Collection<EntityAttributeValue> entityAttributeValues) {

        LoadContext.Query query = createQueryByEntityAttributeValues(getMetaClassName(entityClass), entityAttributeValues)
        LoadContext loadContext = getLoadContext(entityClass)
                .setQuery(query)

        dataManager.loadList(loadContext) as Collection<E>
    }

    @Override
    <E extends Entity> Collection<E> loadAllByAttributes(Class<E> entityClass, Collection<EntityAttributeValue> entityAttributeValues, String view) {
        LoadContext.Query query = createQueryByEntityAttributeValues(getMetaClassName(entityClass), entityAttributeValues)
        LoadContext loadContext = getLoadContext(entityClass)
                .setQuery(query)
                .setView(view)

        dataManager.loadList(loadContext) as Collection<E>
    }

    @Override
    <E extends Entity> Collection<E> loadAllByAttributes(Class<E> entityClass, Collection<EntityAttributeValue> entityAttributeValues, View view) {
        LoadContext.Query query = createQueryByEntityAttributeValues(getMetaClassName(entityClass), entityAttributeValues)
        LoadContext loadContext = getLoadContext(entityClass)
                .setQuery(query)
                .setView(view)

        dataManager.loadList(loadContext) as Collection<E>
    }

    protected String getSelectPart(String metaClassName) {
        "SELECT e FROM ${metaClassName} e"
    }

    protected LoadContext getLoadContext(Class entityClass) {
        LoadContext.create(entityClass)
    }

    protected LoadContext getLoadContext(MetaClass entityClass) {
        new LoadContext(entityClass)
    }


    protected String getMetaClassName(Class entityClass) {
        metadata.getClass(entityClass).name
    }

}
