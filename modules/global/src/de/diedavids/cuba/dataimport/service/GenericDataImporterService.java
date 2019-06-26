package de.diedavids.cuba.dataimport.service;

import com.haulmont.cuba.core.app.importexport.EntityImportView;
import com.haulmont.cuba.core.app.importexport.EntityImportViewBuilderAPI;
import com.haulmont.cuba.core.entity.FileDescriptor;
import de.diedavids.cuba.dataimport.dto.ImportData;
import de.diedavids.cuba.dataimport.entity.ImportConfiguration;
import de.diedavids.cuba.dataimport.entity.ImportLog;

import java.util.Map;
import java.util.function.Consumer;


public interface GenericDataImporterService {
    String NAME = "ddcdi_GenericDataImporterService";


    ImportLog doDataImport(ImportConfiguration importConfiguration, ImportData importData);
    ImportLog doDataImport(ImportConfiguration importConfiguration, ImportData importData, Map<String, Object> defaultValues);
    ImportLog doDataImport(ImportConfiguration importConfiguration, ImportData importData, Map<String, Object> defaultValues, Consumer<EntityImportView> importViewCustomization);

}