package de.diedavids.cuba.dataimport.entity.example.mlb;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum State implements EnumClass<String> {

    AK("AK"),
    AL("AL"),
    AR("AR"),
    AZ("AZ"),
    CA("CA"),
    CO("CO"),
    CT("CT"),
    DC("DC"),
    DE("DE"),
    FL("FL"),
    GA("GA"),
    HI("HI"),
    IA("IA"),
    ID("ID"),
    IL("IL"),
    IN("IN"),
    KS("KS"),
    KY("KY"),
    LA("LA"),
    MA("MA"),
    MD("MD"),
    ME("ME"),
    MI("MI"),
    MN("MN"),
    MO("MO"),
    MS("MS"),
    MT("MT"),
    NC("NC"),
    ND("ND"),
    NE("NE"),
    NH("NH"),
    NJ("NJ"),
    NM("NM"),
    NV("NV"),
    NY("NY"),
    OH("OH"),
    OK("OK"),
    OR("OR"),
    PA("PA"),
    RI("RI"),
    SC("SC"),
    SD("SD"),
    TN("TN"),
    TX("TX"),
    UT("UT"),
    VA("VA"),
    VT("VT"),
    WA("WA"),
    WI("WI"),
    WV("WV"),
    WY("WY");


    private String id;

    State(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static State fromId(String id) {
        for (State at : State.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}