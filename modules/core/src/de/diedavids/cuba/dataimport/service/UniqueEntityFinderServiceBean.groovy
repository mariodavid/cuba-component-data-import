package de.diedavids.cuba.dataimport.service

import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.View
import de.diedavids.cuba.dataimport.data.EntityAttributeValue
import de.diedavids.cuba.dataimport.data.EntityAttributeValueFactory
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.entity.UniqueConfiguration
import org.springframework.stereotype.Service

import javax.inject.Inject

@Service(UniqueEntityFinderService.NAME)
class UniqueEntityFinderServiceBean implements UniqueEntityFinderService {

    @Inject
    DataManager dataManager

    @Inject
    SimpleDataLoader simpleDataLoader

    @Inject
    EntityAttributeValueFactory entityAttributeValueFactory


    @Override
    Entity findEntity(Entity entity, Collection<UniqueConfiguration> uniqueConfigurations, View targetView) {

        Entity foundEntity = null

        uniqueConfigurations.each { UniqueConfiguration uniqueConfiguration ->
            Collection<Entity> foundEntities = findUniqueEntities(entity, uniqueConfiguration, targetView)

            if (foundEntities) {
                foundEntity = foundEntities.first()
            }
        }
        foundEntity
    }

    @Override
    Entity findEntity(Entity entity, UniqueConfiguration uniqueConfiguration, View targetView) {

        Entity foundEntity = null

        Collection<Entity> foundEntities = findUniqueEntities(entity, uniqueConfiguration, targetView)

        if (foundEntities) {
            foundEntity = foundEntities.first()
        }
        foundEntity
    }

    private Collection<Entity> findUniqueEntities(Entity entity, UniqueConfiguration uniqueConfiguration, View requiredView) {
        Collection<EntityAttributeValue> entityAttributeValues = createEntityAttributeValues(entity, uniqueConfiguration)
        simpleDataLoader.loadAllByAttributes(entity.class, entityAttributeValues, requiredView)
    }

    private Collection<EntityAttributeValue> createEntityAttributeValues(Entity entity, UniqueConfiguration uniqueConfiguration) {
        def entityAttributes = uniqueConfiguration.entityAttributes*.entityAttribute

        Collection<EntityAttributeValue> entityAttributeValues = entityAttributes.collect {
            entityAttributeValueFactory.createEntityAttributeValue(entity, it)
        }

        entityAttributeValues
    }
}


