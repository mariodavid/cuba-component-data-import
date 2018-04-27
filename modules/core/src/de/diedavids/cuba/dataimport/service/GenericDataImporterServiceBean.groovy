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
import javax.validation.ConstraintViolation

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

        importEntities(entities, importConfiguration, importLog)
    }

    private ImportLog importEntities(Collection<ImportEntityRequest> entities, ImportConfiguration importConfiguration, ImportLog importLog) {

        def importEntityMetaClass = metadata.getClass(importConfiguration.entityClass)
        def importEntityClass = importEntityMetaClass.javaClass
        EntityImportView importView = creteEntityImportView(importEntityClass, importConfiguration)
        Collection<ImportEntityRequest> importedEntities = []

        if (importConfiguration.transactionStrategy == ImportTransactionStrategy.TRANSACTION_PER_ENTITY) {
            entities.each { ImportEntityRequest importEntityRequest ->
                importSingleEntity(importEntityRequest, importView, importedEntities, importConfiguration, importLog)
            }
        } else {
            importAllEntities(entities, importView, importedEntities, importConfiguration, importLog)

        }

        importLog

    }

    private void importAllEntities(Collection<ImportEntityRequest> entities, EntityImportView importView, Collection<ImportEntityRequest> importedEntities, ImportConfiguration importConfiguration, ImportLog importLog) {
        entities.each { ImportEntityRequest importEntityRequest ->
            importSingleEntity(importEntityRequest, importView, importedEntities, importConfiguration, importLog)
        }

        try {
            entityImportExportAPI.importEntities(importedEntities*.entity, importView, true)
            importLog.entitiesProcessed = entities.size()
            importLog.entitiesImportSuccess = importedEntities.size()

        }
        catch (EntityValidationException e) {
            log.warn('Validation error while executing import with ImportTransactionStrategy.SINGLE_TRANSACTION. Transaction abort - no Entity is written.', e)
            importLog.entitiesProcessed = 0
            importLog.success = false
        }
    }

    private void importSingleEntity(ImportEntityRequest importEntityRequest, EntityImportView importView, Collection<ImportEntityRequest> importedEntities, ImportConfiguration importConfiguration, ImportLog importLog) {

        if (importConfiguration.uniqueConfigurations) {
            importConfiguration.uniqueConfigurations.each { UniqueConfiguration uniqueConfiguration ->

                def alreadyExistingEntity = uniqueEntityFinderService.findEntity(importEntityRequest.entity, uniqueConfiguration)

                if (!alreadyExistingEntity) {
                    doImportSingleEntity(importEntityRequest, importView, importedEntities, importConfiguration, importLog)
                }
                else if (alreadyExistingEntity && uniqueConfiguration.policy == UniquePolicy.UPDATE) {
                    importEntityRequest.entity = bindAttributes(importConfiguration, importEntityRequest.dataRow, alreadyExistingEntity)

                    doImportSingleEntity(importEntityRequest, importView, importedEntities, importConfiguration, importLog)
                }
                else {
                    importLog.entitiesUniqueConstraintSkipped++
                }
            }
        } else {
            doImportSingleEntity(importEntityRequest, importView, importedEntities, importConfiguration, importLog)
        }
    }

    private void doImportSingleEntity(ImportEntityRequest importEntityRequest, EntityImportView importView, Collection<ImportEntityRequest> importedEntities, ImportConfiguration importConfiguration, ImportLog importLog) {

        boolean entityShouldBeImported = executePreCommitScriptIfNecessary(importEntityRequest, importConfiguration)

        if (entityShouldBeImported) {

            if (importConfiguration.transactionStrategy == ImportTransactionStrategy.TRANSACTION_PER_ENTITY) {
                try {
                    entityImportExportAPI.importEntities([importEntityRequest.entity], importView, true)
                    importLog.entitiesImportSuccess++
                }
                catch (EntityValidationException e) {
                    importEntityRequest.constraintViolations = e.constraintViolations
                    importLog.entitiesImportValidationError++
                    importLog.success = false

                }
            }

            importedEntities << importEntityRequest
        }
        else {
            importLog.entitiesPreCommitSkipped++
        }

        importLog.entitiesProcessed++

    }

    private Binding createPreCommitBinding(ImportEntityRequest importEntityRequest, ImportConfiguration importConfiguration) {
        new Binding(
                entity: importEntityRequest.entity,
                dataRow: importEntityRequest.dataRow,
                dataManager: dataManager,
                importConfiguration: importConfiguration,
        )
    }


    private boolean executePreCommitScriptIfNecessary(ImportEntityRequest importEntityRequest, ImportConfiguration importConfiguration) {
        Binding preCommitBinding = createPreCommitBinding(importEntityRequest, importConfiguration)
        def preCommitScript = importConfiguration.preCommitScript
        try {
            if (preCommitScript) {
                return scripting.evaluateGroovy(preCommitScript, preCommitBinding)
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
        importLog.entitiesProcessed = 0
        importLog.entitiesImportSuccess = 0
        importLog.entitiesImportValidationError = 0
        importLog.entitiesPreCommitSkipped = 0
        importLog.entitiesUniqueConstraintSkipped = 0
        importLog.success = true
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


    Collection<ImportEntityRequest> createEntities(ImportConfiguration importConfiguration, ImportData importData) {
        importData.rows.collect {
            createEntityFromRow(importConfiguration, it)
        }
    }

    ImportEntityRequest createEntityFromRow(ImportConfiguration importConfiguration, DataRow dataRow) {
        Entity entityInstance = createEntityInstance(importConfiguration)

        new ImportEntityRequest(
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

class ImportEntityRequest {
    Entity entity
    DataRow dataRow
    Set<ConstraintViolation> constraintViolations = []

    boolean isSuccess() {
        constraintViolations.isEmpty()
    }
}