package de.diedavids.cuba.dataimport.service

import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.chile.core.model.MetaPropertyPath
import com.haulmont.chile.core.model.Range
import com.haulmont.cuba.core.app.importexport.EntityImportExportAPI
import com.haulmont.cuba.core.app.importexport.EntityImportView
import com.haulmont.cuba.core.app.importexport.ReferenceImportBehaviour
import com.haulmont.cuba.core.entity.BaseGenericIdEntity
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.Scripting
import com.haulmont.cuba.core.global.TimeSource
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


    @Override
    ImportLog doDataImport(
            ImportConfiguration importConfiguration,
            ImportData importData,
            Map<String, Object> defaultValues = [:]
    ) {
        doDataImport(importConfiguration, importData, defaultValues, null)
    }

    @Override
    ImportLog doDataImport(
            ImportConfiguration importConfiguration,
            ImportData importData,
            Map<String, Object> defaultValues,
            Consumer<EntityImportView> importViewCustomization
    ) {
        def entities = createEntities(importConfiguration, importData, defaultValues)

        ImportLog importLog = createImportLog(importConfiguration)

        importEntities(entities, importConfiguration, importLog, importViewCustomization)
    }

    private ImportLog importEntities(
            Collection<ImportEntityRequest> entities,
            ImportConfiguration importConfiguration,
            ImportLog importLog,
            Consumer<EntityImportView> importViewCustomization
    ) {

        def importEntityMetaClass = metadata.getClass(importConfiguration.entityClass)
        def importEntityClass = importEntityMetaClass.javaClass
        EntityImportView importView = createEntityImportView(importEntityClass, importConfiguration)


        importViewCustomization?.accept(importView)

        Collection<ImportEntityRequest> importedEntities = []

        if (importConfiguration.transactionStrategy == ImportTransactionStrategy.TRANSACTION_PER_ENTITY) {
            importAllEntitiesInMultipleTransactions(entities, importView, importedEntities, importConfiguration, importLog)
        } else {
            importAllEntitiesInOneTransaction(entities, importView, importedEntities, importConfiguration, importLog)
        }

        importLog.finishedAt = timeSource.currentTimestamp()

        importLog

    }

    protected void importAllEntitiesInMultipleTransactions(
            Collection<ImportEntityRequest> entities,
            EntityImportView importView,
            Collection<ImportEntityRequest> importedEntities,
            ImportConfiguration importConfiguration,
            ImportLog importLog
    ) {
        try {
            entities.each { ImportEntityRequest importEntityRequest ->
                importSingleEntity(importEntityRequest, importView, importedEntities, importConfiguration, importLog)
            }
        }
        catch (ImportUniqueAbortException e) {
            def message = "Unique violation occurred with Unique Policy ABORT for entity: ${e.importEntityRequest.entity} with data row: ${e.importEntityRequest.dataRow}. Found entity: ${e.alreadyExistingEntity}. Due to TransactionStrategy.TRANSACTION_PER_ENTITY: Entities up until this point were written."
            logWarning(importLog, message, e)
            importLog.success = false
        }
    }

    private void importAllEntitiesInOneTransaction(
            Collection<ImportEntityRequest> entities,
            EntityImportView importView,
            Collection<ImportEntityRequest> importedEntities,
            ImportConfiguration importConfiguration,
            ImportLog importLog
    ) {

        try {
            entities.each { ImportEntityRequest importEntityRequest ->
                importSingleEntity(importEntityRequest, importView, importedEntities, importConfiguration, importLog)
            }
            try {
                entityImportExportAPI.importEntities(importedEntities*.entity, importView, true)
                importLog.entitiesProcessed = entities.size()
                importLog.entitiesImportSuccess = importedEntities.size()
            }
            catch (EntityValidationException e) {
                def message = 'Validation error while executing import with ImportTransactionStrategy.SINGLE_TRANSACTION. Transaction abort - no Entity is written.'
                logError(importLog, message, e)
                resetImportLog(importLog)
            }
            catch(PersistenceException e) {
                def message = 'Error while executing import with ImportTransactionStrategy.SINGLE_TRANSACTION. Transaction abort - no Entity is written.'
                logError(importLog, message, e)
                resetImportLog(importLog)
            }
        }

        catch (ImportUniqueAbortException e) {
            def message = "Unique violation occurred with Unique Policy ABORT for entity: ${e.importEntityRequest.entity} with data row: ${e.importEntityRequest.dataRow}. Found entity: ${e.alreadyExistingEntity}. Due to TransactionStrategy.SINGLE_TRANSACTION: no entities written."
            logError(importLog, message, e)
            resetImportLog(importLog)
        }
    }

    void logError(
            ImportLog importLog,
            String message,
            ImportEntityRequest importEntityRequest,
            Exception exception = null
    ) {
        exception ? log.error(message, exception) : log.error(message)
        logMessage(importLog, message, LogRecordLevel.ERROR, importEntityRequest, exception)
    }

    void logWarning(
            ImportLog importLog,
            String message,
            ImportEntityRequest importEntityRequest,
            Exception exception = null
    ) {
        exception ? log.warn(message, exception) : log.warn(message)
        logMessage(importLog, message, LogRecordLevel.WARN, importEntityRequest, exception)
    }

    void logDebug(
            ImportLog importLog,
            String message,
            ImportEntityRequest importEntityRequest,
            Exception exception = null
    ) {
        exception ? log.debug(message, exception) : log.debug(message)
        logMessage(importLog, message, LogRecordLevel.DEBUG, importEntityRequest, exception)
    }

    void logInfo(
            ImportLog importLog,
            String message,
            ImportEntityRequest importEntityRequest,
            Exception exception = null
    ) {
        exception ? log.info(message, exception) : log.info(message)
        logMessage(importLog, message, LogRecordLevel.INFO, importEntityRequest, exception)
    }

    void logError(
            ImportLog importLog,
            String message,
            Exception exception = null
    ) {
        exception ? log.error(message, exception) : log.error(message)
        logMessage(importLog, message, LogRecordLevel.ERROR, exception)
    }

    void logWarning(ImportLog importLog, String message, Exception exception = null) {
        exception ? log.warn(message, exception) : log.warn(message)
        logMessage(importLog, message, LogRecordLevel.WARN, exception)
    }

    void logDebug(ImportLog importLog, String message, Exception exception = null) {
        exception ? log.debug(message, exception) : log.debug(message)
        logMessage(importLog, message, LogRecordLevel.DEBUG, exception)
    }

    void logInfo(ImportLog importLog, String message, Exception exception = null) {
        exception ? log.info(message, exception) : log.info(message)
        logMessage(importLog, message, LogRecordLevel.INFO, exception)
    }

    private void logMessage(ImportLog importLog, String message, LogRecordLevel level, ImportEntityRequest importEntityRequest, Exception exception) {
        def importLogRecord = dataManager.create(ImportLogRecord)
        importLogRecord.importLog = importLog


        if (message.length() > 4000) {
            importLogRecord.message = message.substring(0,3997) + "..."
            String stacktraceMessage = message + "\n\n" + stacktraceForException(exception)
            importLogRecord.stacktrace = stacktraceMessage
        }
        else {
            importLogRecord.message = message
            importLogRecord.stacktrace = stacktraceForException(exception)
        }

        importLogRecord.time = timeSource.currentTimestamp()
        importLogRecord.level = level

        importLog.records << importLogRecord
    }

    private void logMessage(ImportLog importLog, String message, LogRecordLevel level, Exception exception) {
        def importLogRecord = dataManager.create(ImportLogRecord)
        importLogRecord.importLog = importLog


        if (message.length() > 4000) {
            importLogRecord.message = message.substring(0,3997) + "..."
            String stacktraceMessage = message + "\n\n" + stacktraceForException(exception)
            importLogRecord.stacktrace = stacktraceMessage
        }
        else {
            importLogRecord.message = message
            importLogRecord.stacktrace = stacktraceForException(exception)
        }

        importLogRecord.time = timeSource.currentTimestamp()
        importLogRecord.level = level

        importLog.records << importLogRecord
    }

    private String stacktraceForException(Exception exception) {

        if (!exception) {
            return null
        }

        StringWriter stacktrace = new StringWriter()
        exception.printStackTrace(new PrintWriter(stacktrace))
        return stacktrace.toString()
    }

    protected void resetImportLog(ImportLog importLog) {
        importLog.entitiesProcessed = 0
        importLog.success = false
    }

    private void importSingleEntity(
            ImportEntityRequest importEntityRequest,
            EntityImportView importView,
            Collection<ImportEntityRequest> importedEntities,
            ImportConfiguration importConfiguration,
            ImportLog importLog
    ) {

        if (importConfiguration.uniqueConfigurations) {

            importConfiguration.uniqueConfigurations.each { UniqueConfiguration uniqueConfiguration ->

                def alreadyExistingEntity = uniqueEntityFinderService.findEntity(importEntityRequest.entity, uniqueConfiguration)

                if (!alreadyExistingEntity) {
                    doImportSingleEntity(importEntityRequest, importView, importedEntities, importConfiguration, importLog)
                } else if (alreadyExistingEntity && uniqueConfiguration.policy == UniquePolicy.UPDATE) {
                    importEntityRequest.entity = bindAttributesToEntity(importConfiguration, importEntityRequest.dataRow, alreadyExistingEntity, importEntityRequest.defaultValues)

                    doImportSingleEntity(importEntityRequest, importView, importedEntities, importConfiguration, importLog)
                } else if (alreadyExistingEntity && uniqueConfiguration.policy == UniquePolicy.ABORT) {
                    throw new ImportUniqueAbortException(importEntityRequest: importEntityRequest, alreadyExistingEntity: alreadyExistingEntity)

                } else {
                    importLog.entitiesUniqueConstraintSkipped++
                }
            }
        } else {
            doImportSingleEntity(importEntityRequest, importView, importedEntities, importConfiguration, importLog)
        }
    }

    private void doImportSingleEntity(
            ImportEntityRequest importEntityRequest,
            EntityImportView importView,
            Collection<ImportEntityRequest> importedEntities,
            ImportConfiguration importConfiguration,
            ImportLog importLog
    ) {

        boolean entityShouldBeImported = executePreCommitScriptIfNecessary(importEntityRequest, importConfiguration, importView, importLog)

        if (entityShouldBeImported) {
            if (importConfiguration.transactionStrategy == ImportTransactionStrategy.TRANSACTION_PER_ENTITY) {
                tryToExecuteImport(importEntityRequest, importView, importLog)
            }
            importedEntities << importEntityRequest
        } else {
            importLog.entitiesPreCommitSkipped++
            logDebug(importLog, "Entity not imported due to Pre-Commit script returned false", importEntityRequest, null)
        }

        importLog.entitiesProcessed++

    }

    private void tryToExecuteImport(
            ImportEntityRequest importEntityRequest,
            EntityImportView importView,
            ImportLog importLog
    ) {
        try {
            entityImportExportAPI.importEntities([importEntityRequest.entity], importView, true)
            importLog.entitiesImportSuccess++
        }
        catch (EntityValidationException e) {
            importEntityRequest.constraintViolations = e.constraintViolations
            importLog.entitiesImportValidationError++
            logWarning(importLog, "Validation of entity failed: " + e.constraintViolations, importEntityRequest, e)
            importLog.success = false
        }
        catch(PersistenceException e) {
            def message = 'Error while importing entity: ' + e.message
            logWarning(importLog, message, importEntityRequest, e)
            importLog.success = false
        }
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
            ImportLog importLog
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
            logError(importLog, "Pre commit script execution failed with: " + e.message, e)
            return false
        }
    }


    private ImportLog createImportLog(ImportConfiguration importConfiguration) {
        ImportLog importLog = metadata.create(ImportLog)
        importLog.startedAt = timeSource.currentTimestamp()
        importLog.entitiesProcessed = 0
        importLog.entitiesImportSuccess = 0
        importLog.entitiesImportValidationError = 0
        importLog.entitiesPreCommitSkipped = 0
        importLog.entitiesUniqueConstraintSkipped = 0
        importLog.success = true
        importLog.configuration = importConfiguration
        importLog.records = []
        importLog
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

    ImportEntityRequest createImportEntityRequestFromRow(ImportConfiguration importConfiguration, DataRow dataRow, int dataRowIndex, Map<String, Object> defaultValues) {
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