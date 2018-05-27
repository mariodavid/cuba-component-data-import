package de.diedavids.cuba.dataimport.entity.attributemapper;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum AttributeMapperMode implements EnumClass<String> {

    AUTOMATIC("AUTOMATIC"),
    CUSTOM("CUSTOM");

    private String id;

    AttributeMapperMode(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static AttributeMapperMode fromId(String id) {
        for (AttributeMapperMode at : AttributeMapperMode.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}