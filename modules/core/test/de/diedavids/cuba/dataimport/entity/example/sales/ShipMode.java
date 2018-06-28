package de.diedavids.cuba.dataimport.entity.example.sales;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum ShipMode implements EnumClass<Integer> {

    second_class(10),
    standard_class(20),
    first_class(30),
    same_day(40);

    private Integer id;

    ShipMode(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static ShipMode fromId(Integer id) {
        for (ShipMode at : ShipMode.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}