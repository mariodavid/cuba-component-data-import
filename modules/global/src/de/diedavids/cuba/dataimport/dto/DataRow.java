package de.diedavids.cuba.dataimport.dto;

import com.haulmont.cuba.core.entity.KeyValueEntity;

import java.io.Serializable;

public interface DataRow extends Serializable {

    java.util.List<java.lang.String> getColumnNames();
    java.util.Map toMap();
    KeyValueEntity toKevValueEntity();

    //static de.diedavids.cuba.dataimport.dto.DataRowImpl ofMap(java.util.Map<java.lang.String, java.lang.Object> data);
}