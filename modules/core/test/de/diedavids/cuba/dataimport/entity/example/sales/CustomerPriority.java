package de.diedavids.cuba.dataimport.entity.example.sales;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum CustomerPriority implements EnumClass<Integer> {

    LOW(10),
    MEDIUM(20),
    HIGH(30);

    private Integer id;

    CustomerPriority(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static CustomerPriority fromId(Integer id) {
        for (CustomerPriority at : CustomerPriority.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}