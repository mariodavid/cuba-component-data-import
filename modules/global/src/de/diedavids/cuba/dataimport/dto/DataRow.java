package de.diedavids.cuba.dataimport.dto;

import com.haulmont.cuba.core.entity.KeyValueEntity;

import java.io.Serializable;

public interface DataRow extends Serializable {

    java.util.List<java.lang.String> getColumnNames();
    java.util.Map toMap();
    KeyValueEntity toKevValueEntity();

    boolean isEmpty();

}