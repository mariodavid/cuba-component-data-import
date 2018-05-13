package de.diedavids.cuba.dataimport.service

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.CommitContext
import com.haulmont.cuba.core.global.DataManager
import de.diedavids.cuba.dataimport.data.EntityAttributeValueImpl
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportLog
import org.springframework.stereotype.Service

import javax.inject.Inject

@Service(ImportWizardService.NAME)
class ImportWizardServiceBean implements ImportWizardService {

    @Inject
    DataManager dataManager

    @Inject
    SimpleDataLoader simpleDataLoader

    final String viewName = 'importConfiguration-view'

    @Override
    void saveImportConfiguration(ImportConfiguration importConfiguration, Collection<ImportAttributeMapper> importAttributeMapper, ImportLog importLog) {

        CommitContext commitContext = new CommitContext()
        commitContext.addInstanceToCommit(importConfiguration)
        importConfiguration.importAttributeMappers = []

        importAttributeMapper.each {
            it.configuration = importConfiguration
            importConfiguration.importAttributeMappers << it
            commitContext.addInstanceToCommit(it)
        }

        importConfiguration.logs = [importLog]
        importLog.configuration = importConfiguration

        commitContext.addInstanceToCommit(importLog)

        importConfiguration.importerBeanName = GenericDataImporterService.NAME

        dataManager.commit(commitContext)
    }

    @Override
    Collection<ImportConfiguration> getImportConfigurations(MetaClass metaClass) {
        if (!metaClass) {
            return []
        }

        simpleDataLoader.loadAllByAttributes(ImportConfiguration, [
                new EntityAttributeValueImpl(
                        entityAttribute: 'entityClass',
                        value: metaClass.name
                )
        ], viewName)
    }

    @Override
    @SuppressWarnings('DuplicateStringLiteral')
    ImportConfiguration getImportConfigurationByName(MetaClass metaClass, String configName) {
        if (!metaClass) {
            return []
        }

        def results = simpleDataLoader.loadAllByAttributes(ImportConfiguration, [
                new EntityAttributeValueImpl(
                        entityAttribute: 'entityClass',
                        value: metaClass.name
                ),
                new EntityAttributeValueImpl(
                        entityAttribute: 'name',
                        value: configName
                )
        ], viewName)

        if (results && results.size() > 0) {
            return results.first()
        }
        []
    }
}