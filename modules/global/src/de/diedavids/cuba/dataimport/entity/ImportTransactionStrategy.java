package de.diedavids.cuba.dataimport.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ImportTransactionStrategy implements EnumClass<String> {

    SINGLE_TRANSACTION("SINGLE_TRANSACTION"),
    TRANSACTION_PER_ENTITY("TRANSACTION_PER_ENTITY");

    private String id;

    ImportTransactionStrategy(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ImportTransactionStrategy fromId(String id) {
        for (ImportTransactionStrategy at : ImportTransactionStrategy.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}