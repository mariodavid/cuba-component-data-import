package de.diedavids.cuba.dataimport.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ImportLogRecordCategory implements EnumClass<String> {

    UNIQUE_VIOLATION("UNIQUE_VIOLATION"),
    VALIDATION("VALIDATION"),
    PERSISTENCE("PERSISTENCE"),
    GENERAL("GENERAL"),
    SCRIPTING("SCRIPTING");

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