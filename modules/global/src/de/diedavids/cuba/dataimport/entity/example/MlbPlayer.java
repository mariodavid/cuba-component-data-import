package de.diedavids.cuba.dataimport.entity.example;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@NamePattern("%s|name")
@Table(name = "DDCDI_MLB_PLAYER")
@Entity(name = "ddcdi$MlbPlayer")
public class MlbPlayer extends StandardEntity {
    private static final long serialVersionUID = -28922919610355807L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "TEAM")
    protected String team;

    @Column(name = "HEIGHT")
    protected Integer height;

    @Column(name = "WEIGHT")
    protected Integer weight;

    @Column(name = "AGE")
    protected Double age;

    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTHDAY")
    protected Date birthday;

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeam() {
        return team;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getHeight() {
        return height;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setAge(Double age) {
        this.age = age;
    }

    public Double getAge() {
        return age;
    }


}