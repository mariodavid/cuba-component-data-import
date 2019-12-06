package de.diedavids.cuba.dataimport.service

import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.chile.core.model.MetaPropertyPath
import com.haulmont.chile.core.model.Range
import com.haulmont.cuba.core.app.importexport.EntityImportExportAPI
import com.haulmont.cuba.core.app.importexport.EntityImportView
import com.haulmont.cuba.core.app.importexport.ReferenceImportBehaviour
import com.haulmont.cuba.core.entity.BaseGenericIdEntity
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.*
import com.haulmont.cuba.core.global.validation.EntityValidationException
import de.diedavids.cuba.dataimport.binding.EntityBinder
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.*
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import javax.inject.Inject
import javax.persistence.PersistenceException
import javax.validation.ConstraintViolation
import java.util.function.Consumer

@SuppressWarnings('MethodSize')
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

    @Inject
    TimeSource timeSource

    @Inject
    EntityStates entityStates

    @Inject
    EntityImportViewToViewConverter entityImportViewToViewConverter


    @Override
    ImportExecution doDataImport(
            ImportConfiguration importConfiguration,
            ImportData importData,
            Map<String, Object> defaultValues = [:]
    ) {
        doDataImport(importConfiguration, importData, defaultValues, null)
    }

    @Override
    ImportExecution doDataImport(
            ImportConfiguration importConfiguration,
            ImportData importData,
            Map<String, Object> defaultValues,
            Consumer<EntityImportView> importViewCustomization
    ) {

        ImportExecution importExecution = createImportExecution(importConfiguration)

        try {
            Collection<ImportEntityRequest> entities = createEntities(importConfiguration, importData, defaultValues)

            try {
                return importEntities(entities, importConfiguration, importExecution, importViewCustomization)
            }
            catch (Exception e) {
                logError(importExecution, 'Error while importing the data: ' + e.message, ImportExecutionDetailCategory.GENERAL, e)
                resetImportExecution(importExecution)
                return saveImportExecution(importExecution)
            }
        }
        catch (Exception e) {
            logError(importExecution, 'Error while binding the data: ' + e.message, ImportExecutionDetailCategory.DATA_BINDING, e)
            resetImportExecution(importExecution)
            return saveImportExecution(importExecution)
        }
    }

    private ImportExecution importEntities(
            Collection<ImportEntityRequest> entities,
            ImportConfiguration importConfiguration,
            ImportExecution importExecution,
            Consumer<EntityImportView> importViewCustomization
    ) {

        def importEntityMetaClass = metadata.getClass(importConfiguration.entityClass)
        def importEntityClass = importEntityMetaClass.javaClass
        EntityImportView importView = createEntityImportView(importEntityClass, importConfiguration)


        importViewCustomization?.accept(importView)

        Collection<ImportEntityRequest> importedEntities = []

        if (importConfiguration.transactionStrategy == ImportTransactionStrategy.TRANSACTION_PER_ENTITY) {
            importAllEntitiesInMultipleTransactions(entities, importView, importedEntities, importConfiguration, importExecution)
        } else {
            importAllEntitiesInOneTransaction(entities, importView, importedEntities, importConfiguration, importExecution)
        }


        saveImportExecution(importExecution)
    }

    private ImportExecution saveImportExecution(ImportExecution importExecution) {

        importExecution.finishedAt = timeSource.currentTimestamp()

        CommitContext commitContext = new CommitContext()
        commitContext.addInstanceToCommit(importExecution)

        importExecution.details.each {
            commitContext.addInstanceToCommit(it)
        }
        dataManager.commit(commitContext)

        dataManager.reload(importExecution, 'importExecution-with-details-view')
    }

    protected void importAllEntitiesInMultipleTransactions(
            Collection<ImportEntityRequest> entities,
            EntityImportView importView,
            Collection<ImportEntityRequest> importedEntities,
            ImportConfiguration importConfiguration,
            ImportExecution importExecution
    ) {
        try {
            entities.each { ImportEntityRequest importEntityRequest ->
                importSingleEntity(importEntityRequest, importView, importedEntities, importConfiguration, importExecution)
            }
        }
        catch (ImportUniqueAbortException e) {
            def message = "Unique violation occurred with Unique Policy ABORT for entity: ${e.importEntityRequest.entity} with data row: ${e.importEntityRequest.dataRow}. Found entity: ${e.alreadyExistingEntity}. Due to TransactionStrategy.TRANSACTION_PER_ENTITY: Entities up until this point were written."
            logWarning(importExecution, message, ImportExecutionDetailCategory.UNIQUE_VIOLATION, e)
            importExecution.success = false
        }
    }

    private void importAllEntitiesInOneTransaction(
            Collection<ImportEntityRequest> entities,
            EntityImportView importView,
            Collection<ImportEntityRequest> importedEntities,
            ImportConfiguration importConfiguration,
            ImportExecution importExecution
    ) {

        try {
            entities.each { ImportEntityRequest importEntityRequest ->
                importSingleEntity(
                        importEntityRequest,
                        importView,
                        importedEntities,
                        importConfiguration,
                        importExecution
                )
            }
            try {
                entityImportExportAPI.importEntities(importedEntities*.entity, importView, true)
                importExecution.entitiesProcessed = entities.size()
                importExecution.entitiesImportSuccess = importedEntities.size()
            }
            catch (EntityValidationException e) {
                def validationMessage = validationErrorMessage(e)
                def additionalMessage = '\n\nImportTransactionStrategy.SINGLE_TRANSACTION: Transaction abort - no entity is stored in the database.'

                logError(importExecution, validationMessage + additionalMessage, ImportExecutionDetailCategory.VALIDATION, e)
                resetImportExecution(importExecution)
            }
            catch (PersistenceException e) {
                def message = 'Error while executing import with ImportTransactionStrategy.SINGLE_TRANSACTION. Transaction abort - no entity is stored in the database'
                logError(importExecution, message, ImportExecutionDetailCategory.PERSISTENCE, e)
                resetImportExecution(importExecution)
            }
        }

        catch (ImportUniqueAbortException e) {
            def message = "Unique violation occurred with Unique Policy ABORT. Found entity: ${e.alreadyExistingEntity}. Due to TransactionStrategy.SINGLE_TRANSACTION: no entities written."
            logError(importExecution, message, ImportExecutionDetailCategory.UNIQUE_VIOLATION, e)
            resetImportExecution(importExecution)
        }
    }

    void logError(
            ImportExecution importExecution,
            String message,
            ImportExecutionDetailCategory category,
            ImportEntityRequest importEntityRequest,
            Exception exception = null
    ) {
        exception ? log.error(message, exception) : log.error(message)
        logMessage(importExecution, message, LogRecordLevel.ERROR, category, importEntityRequest, exception)
    }

    void logWarning(
            ImportExecution importExecution,
            String message,
            ImportExecutionDetailCategory category,
            ImportEntityRequest importEntityRequest,
            Exception exception = null
    ) {
        exception ? log.warn(message, exception) : log.warn(message)
        logMessage(importExecution, message, LogRecordLevel.WARN, category, importEntityRequest, exception)
    }

    void logDebug(
            ImportExecution importExecution,
            String message,
            ImportExecutionDetailCategory category,
            ImportEntityRequest importEntityRequest,
            Exception exception = null
    ) {
        exception ? log.debug(message, exception) : log.debug(message)
        logMessage(importExecution, message, LogRecordLevel.DEBUG, category, importEntityRequest, exception)
    }

    void logInfo(
            ImportExecution importExecution,
            String message,
            ImportExecutionDetailCategory category,
            ImportEntityRequest importEntityRequest,
            Exception exception = null
    ) {
        exception ? log.info(message, exception) : log.info(message)
        logMessage(importExecution, message, LogRecordLevel.INFO, category, importEntityRequest, exception)
    }

    void logError(
            ImportExecution importExecution,
            String message,
            ImportExecutionDetailCategory category,
            Exception exception = null
    ) {
        exception ? log.error(message, exception) : log.error(message)
        logMessage(importExecution, message, LogRecordLevel.ERROR, category, exception)
    }

    void logWarning(
            ImportExecution importExecution,
            String message,
            ImportExecutionDetailCategory category,
            Exception exception = null
    ) {
        exception ? log.warn(message, exception) : log.warn(message)
        logMessage(importExecution, message, LogRecordLevel.WARN, category, exception)
    }

    void logDebug(
            ImportExecution importExecution,
            String message,
            ImportExecutionDetailCategory category,
            Exception exception = null
    ) {
        exception ? log.debug(message, exception) : log.debug(message)
        logMessage(importExecution, message, LogRecordLevel.DEBUG, category, exception)
    }

    void logInfo(
            ImportExecution importExecution,
            String message,
            ImportExecutionDetailCategory category,
            Exception exception = null
    ) {
        exception ? log.info(message, exception) : log.info(message)
        logMessage(importExecution, message, LogRecordLevel.INFO, category, exception)
    }

    private void logMessage(ImportExecution importExecution, String message, LogRecordLevel level, ImportExecutionDetailCategory category, ImportEntityRequest importEntityRequest, Exception exception) {
        def importExecutionDetail = dataManager.create(ImportExecutionDetail)
        importExecutionDetail.importExecution = importExecution
        if (importEntityRequest) {
            importExecutionDetail.dataRowIndex = importEntityRequest.dataRowIndex
            importExecutionDetail.dataRow = importEntityRequest.dataRow
            importExecutionDetail.entityInstance = entityInstance(importEntityRequest)
        }
        importExecutionDetail.category = category

        if (message.length() > 4000) {
            importExecutionDetail.message = message[0..3997] + '...'
            String stacktraceMessage = message + '\n\n' + stacktraceForException(exception)
            importExecutionDetail.stacktrace = stacktraceMessage
        } else {
            importExecutionDetail.message = message
            importExecutionDetail.stacktrace = stacktraceForException(exception)
        }

        importExecutionDetail.time = timeSource.currentTimestamp()
        importExecutionDetail.level = level

        importExecution.details << importExecutionDetail
    }

    private String entityInstance(ImportEntityRequest importEntityRequest) {
        entityImportExportAPI.exportEntitiesToJSON([importEntityRequest.entity])
    }

    private void logMessage(ImportExecution importExecution, String message, LogRecordLevel level, ImportExecutionDetailCategory category, Exception exception) {
        logMessage(importExecution, message, level, category, null, exception)
    }

    private String stacktraceForException(Exception exception) {

        if (!exception) {
            return null
        }

        StringWriter stacktrace = new StringWriter()
        exception.printStackTrace(new PrintWriter(stacktrace))
        stacktrace.toString()
    }

    protected void resetImportExecution(ImportExecution importExecution) {
        importExecution.entitiesProcessed = 0
        importExecution.success = false
    }

    private void importSingleEntity(
            ImportEntityRequest importEntityRequest,
            EntityImportView importView,
            Collection<ImportEntityRequest> importedEntities,
            ImportConfiguration importConfiguration,
            ImportExecution importExecution
    ) {

        if (importConfiguration.uniqueConfigurations) {

            importConfiguration.uniqueConfigurations.each { UniqueConfiguration uniqueConfiguration ->

                View targetView = entityImportViewToViewConverter.convert(importView)
                def alreadyExistingEntity = uniqueEntityFinderService.findEntity(
                        importEntityRequest.entity,
                        uniqueConfiguration,
                        targetView
                )

                if (!alreadyExistingEntity) {
                    doImportSingleEntity(
                            importEntityRequest,
                            importView,
                            importedEntities,
                            importConfiguration,
                            importExecution
                    )
                } else if (alreadyExistingEntity && uniqueConfiguration.policy == UniquePolicy.UPDATE) {

                    importEntityRequest.entity = bindAttributesToEntity(
                            importConfiguration,
                            importEntityRequest.dataRow,
                            alreadyExistingEntity,
                            importEntityRequest.defaultValues
                    )

                    doImportSingleEntity(
                            importEntityRequest,
                            importView,
                            importedEntities,
                            importConfiguration,
                            importExecution
                    )
                } else if (alreadyExistingEntity && uniqueConfiguration.policy == UniquePolicy.ABORT) {
                    throw new ImportUniqueAbortException(
                            importEntityRequest: importEntityRequest,
                            alreadyExistingEntity: alreadyExistingEntity
                    )

                } else {
                    importExecution.entitiesUniqueConstraintSkipped++
                    logInfo(
                            importExecution,
                            'Entity not imported since it is already existing and Unique policy is set to SKIP',
                            ImportExecutionDetailCategory.UNIQUE_VIOLATION,
                            importEntityRequest
                    )
                }
            }
        } else {
            doImportSingleEntity(
                    importEntityRequest,
                    importView,
                    importedEntities,
                    importConfiguration,
                    importExecution
            )
        }
    }


    private void doImportSingleEntity(
            ImportEntityRequest importEntityRequest,
            EntityImportView importView,
            Collection<ImportEntityRequest> importedEntities,
            ImportConfiguration importConfiguration,
            ImportExecution importExecution
    ) {

        boolean entityShouldBeImported = executePreCommitScriptIfNecessary(importEntityRequest, importConfiguration, importView, importExecution)

        if (entityShouldBeImported) {
            if (importConfiguration.transactionStrategy == ImportTransactionStrategy.TRANSACTION_PER_ENTITY) {
                tryToExecuteImport(importEntityRequest, importView, importExecution)
            }
            importedEntities << importEntityRequest
        } else {
            importExecution.entitiesPreCommitSkipped++
            logWarning(importExecution, 'Entity not imported due to pre-commit script veto', ImportExecutionDetailCategory.VALIDATION, importEntityRequest)
        }

        importExecution.entitiesProcessed++
    }

    private void tryToExecuteImport(
            ImportEntityRequest importEntityRequest,
            EntityImportView importView,
            ImportExecution importExecution
    ) {
        try {
            entityImportExportAPI.importEntities([importEntityRequest.entity], importView, true)
            importExecution.entitiesImportSuccess++
        }
        catch (EntityValidationException e) {
            importEntityRequest.constraintViolations = e.constraintViolations
            importExecution.entitiesImportValidationError++
            logWarning(importExecution, validationErrorMessage(e), ImportExecutionDetailCategory.VALIDATION, importEntityRequest, e)
            importExecution.success = false
        }
        catch (PersistenceException e) {
            def message = 'Error while importing entity: ' + e.message
            logWarning(importExecution, message, ImportExecutionDetailCategory.PERSISTENCE, importEntityRequest, e)
            importExecution.success = false
        }
    }

    private String validationErrorMessage(EntityValidationException e) {
        def header = 'Validation failed: \n\n'

        def constraints = e.constraintViolations.collect {
            "  * ${it.propertyPath}: ${it.message}, provided value: '${it.invalidValue.toString()}'"
        }.join('\n')

        header + constraints
    }


    private Binding createPreCommitBinding(
            ImportEntityRequest importEntityRequest,
            ImportConfiguration importConfiguration,
            EntityImportView importView
    ) {
        new Binding(
                entity: importEntityRequest.entity,
                dataRow: importEntityRequest.dataRow,
                dataManager: dataManager,
                importConfiguration: importConfiguration,
                importView: importView
        )
    }


    private boolean executePreCommitScriptIfNecessary(
            ImportEntityRequest importEntityRequest,
            ImportConfiguration importConfiguration,
            EntityImportView importView,
            ImportExecution importExecution
    ) {
        Binding preCommitBinding = createPreCommitBinding(importEntityRequest, importConfiguration, importView)
        def preCommitScript = importConfiguration.preCommitScript
        try {
            if (preCommitScript) {
                return scripting.evaluateGroovy(preCommitScript, preCommitBinding)
            }
            return true
        }
        catch (Exception e) {
            logError(importExecution, 'Pre commit script execution failed with: ' + e.message, ImportExecutionDetailCategory.SCRIPTING, importEntityRequest, e)
            resetImportExecution(importExecution)
            return false
        }
    }


    private ImportExecution createImportExecution(ImportConfiguration importConfiguration) {
        ImportExecution importExecution = metadata.create(ImportExecution)
        importExecution.startedAt = timeSource.currentTimestamp()
        importExecution.entitiesProcessed = 0
        importExecution.entitiesImportSuccess = 0
        importExecution.entitiesImportValidationError = 0
        importExecution.entitiesPreCommitSkipped = 0
        importExecution.entitiesUniqueConstraintSkipped = 0
        importExecution.success = true

        /*
         a reference to the import configuration is only stored when the import configuration
         itself is not new. In case of the ad-hoc ImportWizard the import configuration
         is just transient and is not stored. In this case, the reference cannot be stored
          */
        if (!entityStates.isNew(importConfiguration)) {
            importExecution.configuration = importConfiguration
        }

        importExecution.details = []
        importExecution
    }

    private EntityImportView createEntityImportView(Class importEntityClass, ImportConfiguration importConfiguration) {
        EntityImportView importView = new EntityImportView(importEntityClass)
                .addLocalProperties()
        addAssociationPropertiesToImportView(importConfiguration, importView)

        importView
    }

    private void addAssociationPropertiesToImportView(ImportConfiguration importConfiguration, EntityImportView importView) {
        validImportAttributeMappers(importConfiguration).each { ImportAttributeMapper importAttributeMapper ->
            def importEntityClassName = importConfiguration.entityClass

            def entityAttribute = importAttributeMapper.entityAttribute - (importEntityClassName + '.')
            MetaPropertyPath path = metadata.getClass(importEntityClassName).getPropertyPath(entityAttribute)

            if (isAutomaticAssociationAttribute(importAttributeMapper) || isCustomAttributeMapper(importAttributeMapper)) {
                def associationMetaProperty = path.metaProperties[0]
                def associationMetaPropertyName = associationMetaProperty.name
                if (associationMetaProperty.type == MetaProperty.Type.ASSOCIATION && associationMetaProperty.range.cardinality == Range.Cardinality.MANY_TO_ONE) {
                    importView.addManyToOneProperty(associationMetaPropertyName, ReferenceImportBehaviour.IGNORE_MISSING)
                }
            }
        }
    }

    private List<ImportAttributeMapper> validImportAttributeMappers(ImportConfiguration importConfiguration) {
        importConfiguration.importAttributeMappers.findAll { it.bindable }
    }

    boolean isCustomAttributeMapper(ImportAttributeMapper importAttributeMapper) {
        importAttributeMapper.custom
    }


    boolean isAutomaticAssociationAttribute(ImportAttributeMapper importAttributeMapper) {
        importAttributeMapper.automatic && importAttributeMapper.attributeType == AttributeType.ASSOCIATION_ATTRIBUTE
    }

    Collection<ImportEntityRequest> createEntities(
            ImportConfiguration importConfiguration,
            ImportData importData,
            Map<String, Object> defaultValues = [:]
    ) {
        importData.rows.withIndex(1).collect { dataRow, dataRowIndex ->
            createImportEntityRequestFromRow(importConfiguration, dataRow, dataRowIndex, defaultValues)
        }
    }

    ImportEntityRequest createImportEntityRequestFromRow(
            ImportConfiguration importConfiguration,
            DataRow dataRow,
            int dataRowIndex,
            Map<String, Object> defaultValues
    ) {
        Entity entityInstance = createEntityInstance(importConfiguration)

        new ImportEntityRequest(
                defaultValues: defaultValues,
                entity: bindAttributesToEntity(importConfiguration, dataRow, entityInstance, defaultValues),
                dataRow: dataRow,
                dataRowIndex: dataRowIndex
        )
    }

    Entity bindAttributesToEntity(ImportConfiguration importConfiguration, DataRow dataRow, Entity entity, Map<String, Object> defaultValues) {
        dataImportEntityBinder.bindAttributesToEntity(importConfiguration, dataRow, entity, defaultValues)
    }

    private Entity createEntityInstance(ImportConfiguration importConfiguration) {
        Entity entity = metadata.create(importConfiguration.entityClass)
        ((BaseGenericIdEntity) entity).dynamicAttributes = [:]
        entity
    }
}

class ImportEntityRequest {
    Map<String, Object> defaultValues
    Entity entity
    DataRow dataRow
    Set<ConstraintViolation> constraintViolations = []
    int dataRowIndex

    boolean isSuccess() {
        constraintViolations.isEmpty()
    }
}