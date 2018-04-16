package de.diedavids.cuba.dataimport.service

import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.chile.core.model.MetaPropertyPath
import com.haulmont.chile.core.model.Range
import com.haulmont.cuba.core.app.importexport.EntityImportExportAPI
import com.haulmont.cuba.core.app.importexport.EntityImportView
import com.haulmont.cuba.core.app.importexport.ReferenceImportBehaviour
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.validation.EntityValidationException
import de.diedavids.cuba.dataimport.binding.EntityBinder
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.*
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import javax.inject.Inject

@Slf4j
@Service(GenericDataImporterService.NAME)
class GenericDataImporterServiceBean implements GenericDataImporterService {

    @Inject
    Metadata metadata

    @Inject
    EntityImportExportAPI entityImportExportAPI

    @Inject
    EntityBinder dataImportEntityBinder


    @Inject
    UniqueEntityFinderService uniqueEntityFinderService

    @Override
    ImportLog doDataImport(ImportConfiguration importConfiguration, ImportData importData) {

        def entities = createEntities(importConfiguration, importData)

        ImportLog importLog = createImportLog(importConfiguration)

        try {
            Collection<Entity> importEntities = importEntities(entities, importConfiguration)
            importLog.entitiesProcessed = importEntities.size()
            importLog.success = true
        }
        catch (EntityValidationException e) {
            log.error('Import failed due to validation errors of one or more entities', e)
            importLog.entitiesProcessed = 0
            importLog.success = false
        }
        importLog
    }

    private Collection<Entity> importEntities(Collection<BindedEntity> entities, ImportConfiguration importConfiguration) {

        def importEntityMetaClass = metadata.getClass(importConfiguration.entityClass)
        def importEntityClass = importEntityMetaClass.javaClass
        EntityImportView importView = creteEntityImportView(importEntityClass, importConfiguration)
        Collection<Entity> importedEntities = []

        entities.each { BindedEntity bindedEntity ->
            importEntity(bindedEntity, importView, importedEntities, importConfiguration)
        }

        importedEntities

    }

    private List<UniqueConfiguration> importEntity(bindedEntity, EntityImportView importView, Collection<Entity> importedEntities, ImportConfiguration importConfiguration) {
        importConfiguration.uniqueConfigurations.each { UniqueConfiguration uniqueConfiguration ->

            def alreadyExistingEntity = uniqueEntityFinderService.findEntity(bindedEntity.entity, uniqueConfiguration)

            if (!alreadyExistingEntity) {
                entityImportExportAPI.importEntities([bindedEntity.entity], importView, true)
                importedEntities << bindedEntity.entity
            }

            if (alreadyExistingEntity && uniqueConfiguration.policy == UniquePolicy.UPDATE) {
                def alreadyExistingBindedEntity = bindAttributes(importConfiguration, bindedEntity.dataRow, alreadyExistingEntity)
                entityImportExportAPI.importEntities([alreadyExistingBindedEntity], importView, true)
                importedEntities << bindedEntity.entity
            }

        }
    }

    private ImportLog createImportLog(ImportConfiguration importConfiguration) {
        ImportLog importLog = metadata.create(ImportLog)
        importLog.configuration = importConfiguration
        importLog
    }

    private EntityImportView creteEntityImportView(Class importEntityClass, ImportConfiguration importConfiguration) {
        EntityImportView importView = new EntityImportView(importEntityClass)
                .addLocalProperties()
        addAssociationPropertiesToImportView(importConfiguration, importView)

        importView
    }

    private void addAssociationPropertiesToImportView(ImportConfiguration importConfiguration, importView) {
        importConfiguration.importAttributeMappers.each { ImportAttributeMapper importAttributeMapper ->
            def importEntityClassName = importConfiguration.entityClass

            def entityAttribute = importAttributeMapper.entityAttribute - (importEntityClassName + '.')
            MetaPropertyPath path = metadata.getClass(importEntityClassName).getPropertyPath(entityAttribute)

            if (isAssociatedAttribute(path)) {
                def associationMetaProperty = path.metaProperties[0]
                def associationMetaPropertyName = associationMetaProperty.name
                if (associationMetaProperty.type == MetaProperty.Type.ASSOCIATION && associationMetaProperty.range.cardinality == Range.Cardinality.MANY_TO_ONE) {
                    importView.addManyToOneProperty(associationMetaPropertyName, ReferenceImportBehaviour.IGNORE_MISSING)
                }
            }
        }
    }

    private boolean isAssociatedAttribute(MetaPropertyPath path) {
        path.metaProperties.size() > 1
    }


    Collection<BindedEntity> createEntities(ImportConfiguration importConfiguration, ImportData importData) {
        importData.rows.collect {
            createEntityFromRow(importConfiguration, it)
        }
    }

    BindedEntity createEntityFromRow(ImportConfiguration importConfiguration, DataRow dataRow) {
        Entity entityInstance = createEntityInstance(importConfiguration)

        new BindedEntity(
                entity: bindAttributes(importConfiguration, dataRow, entityInstance),
                dataRow: dataRow
        )
    }

    Entity bindAttributes(ImportConfiguration importConfiguration, DataRow dataRow, Entity entity) {
        dataImportEntityBinder.bindAttributes(importConfiguration, dataRow, entity)
    }

    private Entity createEntityInstance(ImportConfiguration importConfiguration) {
        metadata.create(importConfiguration.entityClass)
    }
}

class BindedEntity {
    Entity entity
    DataRow dataRow
}