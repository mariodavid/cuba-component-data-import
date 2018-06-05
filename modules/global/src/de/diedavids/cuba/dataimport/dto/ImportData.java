package de.diedavids.cuba.dataimport.dto;

import de.diedavids.cuba.dataimport.entity.ImportConfiguration;
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper;

import java.io.Serializable;
import java.util.List;

public interface ImportData extends Serializable {

    List<DataRow> getRows();
    List<String> getColumns();

    boolean isCompatibleWith(List<ImportAttributeMapper> attributeMappers);
}