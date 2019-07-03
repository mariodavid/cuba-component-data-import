package de.diedavids.cuba.dataimport.service;

import com.haulmont.cuba.core.app.importexport.EntityImportView;
import de.diedavids.cuba.dataimport.dto.ImportData;
import de.diedavids.cuba.dataimport.entity.ImportConfiguration;
import de.diedavids.cuba.dataimport.entity.ImportExecution;

import java.util.Map;
import java.util.function.Consumer;


public interface GenericDataImporterService {
    String NAME = "ddcdi_GenericDataImporterService";


    ImportExecution doDataImport(ImportConfiguration importConfiguration, ImportData importData);
    ImportExecution doDataImport(ImportConfiguration importConfiguration, ImportData importData, Map<String, Object> defaultValues);
    ImportExecution doDataImport(ImportConfiguration importConfiguration, ImportData importData, Map<String, Object> defaultValues, Consumer<EntityImportView> importViewCustomization);

}