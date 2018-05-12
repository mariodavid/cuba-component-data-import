package de.diedavids.cuba.dataimport.service

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.CommitContext
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.LoadContext
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportLog
import org.springframework.stereotype.Service

import javax.inject.Inject

@Service(ImportWizardService.NAME)
class ImportWizardServiceBean implements ImportWizardService {

    @Inject
    DataManager dataManager

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
        if(metaClass==null)
            return Collections.emptyList();

        def entityName = metaClass.name

        LoadContext loadContext =
                LoadContext.create(ImportConfiguration.class)
                .setQuery(
                LoadContext.createQuery('select o from ddcdi$ImportConfiguration o where o.entityClass = :entityName')
                .setParameter("entityName", entityName))
                .setView("importConfiguration-view")

        return dataManager.loadList(loadContext);
    }

    @Override
    ImportConfiguration getImportConfigurations(MetaClass metaClass, String configName) {
        if(metaClass==null)
            return Collections.emptyList();

        def entityName = metaClass.name

        LoadContext loadContext =
                LoadContext.create(ImportConfiguration.class)
                        .setQuery(
                        LoadContext.createQuery('select o from ddcdi$ImportConfiguration o where o.entityClass = :entityName AND o.name = :cName')
                                .setParameter("entityName", entityName)
                                .setParameter("cName", configName))
                        .setView("importConfiguration-view")

        return dataManager.loadList(loadContext).first();
    }
}