package de.diedavids.cuba.dataimport.service;


import com.haulmont.chile.core.model.MetaClass;
import de.diedavids.cuba.dataimport.entity.UniqueConfiguration;
import de.diedavids.cuba.dataimport.entity.UniqueConfigurationAttribute;
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper;
import de.diedavids.cuba.dataimport.entity.ImportConfiguration;
import de.diedavids.cuba.dataimport.entity.ImportLog;

import java.util.Collection;

public interface ImportWizardService {
    String NAME = "ddcdi_ImportWizardService";

    void saveImportConfiguration(
            ImportConfiguration importConfiguration,
            Collection<ImportAttributeMapper> importAttributeMapper,
            Collection<UniqueConfiguration> uniqueConfigurations,
            ImportLog importLog
    );


    Collection<ImportConfiguration> getImportConfigurations(MetaClass metaClass);
    
    ImportConfiguration getImportConfigurationByName(MetaClass metaClass, String configName);
}