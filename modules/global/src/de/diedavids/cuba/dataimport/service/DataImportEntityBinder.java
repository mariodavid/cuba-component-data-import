package de.diedavids.cuba.dataimport.service;

import com.haulmont.cuba.core.entity.Entity;
import de.diedavids.cuba.dataimport.dto.DataRow;
import de.diedavids.cuba.dataimport.entity.ImportConfiguration;

public interface DataImportEntityBinder {
    String NAME = "ddcdi_DataImportEntityBinder";

    Entity bindAttributes(ImportConfiguration importConfiguration, DataRow dataRow, Entity entity);

}