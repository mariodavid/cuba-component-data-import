package de.diedavids.cuba.dataimport.entity.attributemapper;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum AttributeType implements EnumClass<String> {

    DIRECT_ATTRIBUTE("DIRECT_ATTRIBUTE"),
    ASSOCIATION_ATTRIBUTE("ASSOCIATION_ATTRIBUTE"),
    DYNAMIC_ATTRIBUTE("DYNAMIC_ATTRIBUTE");

    private String id;

    AttributeType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static AttributeType fromId(String id) {
        for (AttributeType at : AttributeType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}