package de.diedavids.cuba.dataimport.service;


import de.diedavids.cuba.dataimport.entity.ImportLog;
import de.diedavids.cuba.dataimport.entity.Importer;

import java.util.Map;
import java.util.Set;

public interface DataImportService {
    String NAME = "ddcdi_DataImportService";

    Set<Importer> getImporters();

    /**
     *
     * @param log log parameter should have fully loaded file descriptor and related configuration entity
     * @param params additional parameters that could be used in an importer implementation
     * @param doPersistLog when true - log will be persisted with log records under its configuration
     * @return import log
     */
    ImportLog doImport(ImportLog log, Map<String, Object> params, boolean doPersistLog);
}