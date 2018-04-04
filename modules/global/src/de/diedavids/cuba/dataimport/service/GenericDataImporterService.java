package de.diedavids.cuba.dataimport.service;

import de.diedavids.cuba.dataimport.dto.ImportData;
import de.diedavids.cuba.dataimport.entity.ImportConfiguration;
import de.diedavids.cuba.dataimport.entity.ImportLog;


public interface GenericDataImporterService {
    String NAME = "ddcdi_GenericDataImporterService";


    ImportLog doDataImport(ImportConfiguration importConfiguration, ImportData importData);
}