package de.diedavids.cuba.dataimport.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ImportExecutionRecordCategory implements EnumClass<String> {

    UNIQUE_VIOLATION("UNIQUE_VIOLATION"),
    VALIDATION("VALIDATION"),
    PERSISTENCE("PERSISTENCE"),
    GENERAL("GENERAL"),
    SCRIPTING("SCRIPTING");

    private String id;

    ImportExecutionRecordCategory(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ImportExecutionRecordCategory fromId(String id) {
        for (ImportExecutionRecordCategory at : ImportExecutionRecordCategory.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}