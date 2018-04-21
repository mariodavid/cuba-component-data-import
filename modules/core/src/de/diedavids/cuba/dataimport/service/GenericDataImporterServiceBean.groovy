package de.diedavids.cuba.dataimport.service

import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.chile.core.model.MetaPropertyPath
import com.haulmont.chile.core.model.Range
import com.haulmont.cuba.core.app.importexport.EntityImportExportAPI
import com.haulmont.cuba.core.app.importexport.EntityImportView
import com.haulmont.cuba.core.app.importexport.ReferenceImportBehaviour
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.Scripting
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
    Scripting scripting

    @Inject
    DataManager dataManager


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

    private List<UniqueConfiguration> importEntity(BindedEntity bindedEntity, EntityImportView importView, Collection<Entity> importedEntities, ImportConfiguration importConfiguration) {

        if (importConfiguration.uniqueConfigurations) {
            importConfiguration.uniqueConfigurations.each { UniqueConfiguration uniqueConfiguration ->

                def alreadyExistingEntity = uniqueEntityFinderService.findEntity(bindedEntity.entity, uniqueConfiguration)

                if (!alreadyExistingEntity) {
                    doImportEntity(bindedEntity, importView, importedEntities, importConfiguration)
                }

                if (alreadyExistingEntity && uniqueConfiguration.policy == UniquePolicy.UPDATE) {
                    bindedEntity.entity = bindAttributes(importConfiguration, bindedEntity.dataRow, alreadyExistingEntity)
                    doImportEntity(bindedEntity, importView, importedEntities, importConfiguration)
                }

            }
        }
        else {
            doImportEntity(bindedEntity, importView, importedEntities, importConfiguration)
        }
    }

    private void doImportEntity(BindedEntity bindedEntity, EntityImportView importView, Collection<Entity> importedEntities, ImportConfiguration importConfiguration) {

        def binding = new Binding(
                entity: bindedEntity.entity,
                dataRow: bindedEntity.dataRow,
                dataManager: dataManager,
                importConfiguration: importConfiguration,
        )

        boolean entityShouldBeImported = executePreCommitScriptIfNecessary(importConfiguration, binding)

        if (entityShouldBeImported) {
            entityImportExportAPI.importEntities([bindedEntity.entity], importView, true)
            importedEntities << bindedEntity.entity
        }

    }



    private boolean executePreCommitScriptIfNecessary(ImportConfiguration importConfiguration, Binding binding) {
        def preCommitScript = importConfiguration.preCommitScript
        try {
            if (preCommitScript) {
                return scripting.evaluateGroovy(preCommitScript, binding)
            }
            return true
        }
        catch (Exception e) {
            log.error("Error while executing pre commit script: ${e.getClass()}", e)
            return false
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