package de.diedavids.cuba.dataimport.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ImportLogRecordCategory implements EnumClass<String> {

    UNIQUE_VIOLATION("UNIQUE_VIOLATION"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    PERSISTENCE_ERROR("PERSISTENCE_ERROR"),
    GENERAL("GENERAL"),
    SCRIPTING_ERROR("SCRIPTING_ERROR");

    private String id;

    ImportLogRecordCategory(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ImportLogRecordCategory fromId(String id) {
        for (ImportLogRecordCategory at : ImportLogRecordCategory.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}