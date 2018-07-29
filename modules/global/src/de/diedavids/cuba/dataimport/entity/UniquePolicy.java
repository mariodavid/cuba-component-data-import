package de.diedavids.cuba.dataimport.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum UniquePolicy implements EnumClass<String> {

    SKIP("SKIP"),
    UPDATE("UPDATE"),
    ABORT("ABORT");

    private String id;

    UniquePolicy(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static UniquePolicy fromId(String id) {
        for (UniquePolicy at : UniquePolicy.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}