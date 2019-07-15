package de.diedavids.cuba.dataimport.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ImportExecutionDetailCategory implements EnumClass<String> {

    UNIQUE_VIOLATION("UNIQUE_VIOLATION"),
    VALIDATION("VALIDATION"),
    PERSISTENCE("PERSISTENCE"),
    GENERAL("GENERAL"),
    SCRIPTING("SCRIPTING"),
    DATA_BINDING("DATA_BINDING");

    private String id;

    ImportExecutionDetailCategory(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ImportExecutionDetailCategory fromId(String id) {
        for (ImportExecutionDetailCategory at : ImportExecutionDetailCategory.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}