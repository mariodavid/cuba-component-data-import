package de.diedavids.cuba.dataimport.entity.example;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "DDCDI_GAME")
@Entity(name = "ddcdi_Game")
public class Game extends StandardEntity {
    @Column(name = "NAME")
    protected String name;

    @Column(name = "NAME2")
    protected String name2;

    @Column(name = "NAME3")
    protected String name3;

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}