package de.diedavids.cuba.dataimport.service

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.CommitContext
import com.haulmont.cuba.core.global.DataManager
import de.diedavids.cuba.dataimport.data.EntityAttributeValueFactory
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportExecution
import de.diedavids.cuba.dataimport.entity.UniqueConfiguration
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import org.springframework.stereotype.Service

import javax.inject.Inject

@Service(ImportWizardService.NAME)
class ImportWizardServiceBean implements ImportWizardService {


    @Inject
    DataManager dataManager

    @Inject
    SimpleDataLoader simpleDataLoader

    @Inject
    EntityAttributeValueFactory entityAttributeValueFactory

    @Override
    void saveImportConfiguration(
            ImportConfiguration importConfiguration,
            Collection<ImportAttributeMapper> importAttributeMapper,
            Collection<UniqueConfiguration> uniqueConfigurations,
            ImportExecution importExecution
    ) {

        CommitContext commitContext = new CommitContext()
        commitContext.addInstanceToCommit(importConfiguration)
        importConfiguration.importAttributeMappers = []

        addImportAttributeMapper(importAttributeMapper, importConfiguration, commitContext)

        addUniqueConfiguration(uniqueConfigurations, importConfiguration, commitContext)

        importConfiguration.logs = [importExecution]
        importExecution.configuration = importConfiguration

        commitContext.addInstanceToCommit(importExecution)

        dataManager.commit(commitContext)
    }

    private void addUniqueConfiguration(Collection<UniqueConfiguration> uniqueConfigurations, ImportConfiguration importConfiguration, commitContext) {
        uniqueConfigurations.each {
            it.importConfiguration = importConfiguration
            importConfiguration.uniqueConfigurations << it
            commitContext.addInstanceToCommit(it)
            it.entityAttributes.each { entityAttribute ->
                commitContext.addInstanceToCommit(entityAttribute)
            }
        }
    }

    private void addImportAttributeMapper(Collection<ImportAttributeMapper> importAttributeMapper, ImportConfiguration importConfiguration, commitContext) {
        importAttributeMapper.each {
            it.configuration = importConfiguration
            importConfiguration.importAttributeMappers << it
            commitContext.addInstanceToCommit(it)
        }
    }

    @Override
    Collection<ImportConfiguration> getImportConfigurations(MetaClass metaClass) {

        Collection<ImportConfiguration> importConfigurations = []

        if (metaClass) {
            importConfigurations = loadImportConfigurationWithAttributes(entityClass: metaClass.name)
        }

        importConfigurations
    }

    @Override
    ImportConfiguration getImportConfigurationByName(MetaClass metaClass, String configName) {

        ImportConfiguration importConfiguration = null

        if (metaClass) {
            def allImportConfigurations = loadImportConfigurationWithAttributes(entityClass: metaClass.name, name: configName)
            importConfiguration = allImportConfigurations?.first()
        }

        importConfiguration
    }


    private Collection<ImportConfiguration> loadImportConfigurationWithAttributes(Map<String, Object> attributeMap) {
        simpleDataLoader.loadAllByAttributes(ImportConfiguration, entityAttributeValueFactory.ofMap(attributeMap), 'importConfiguration-view')
    }


}