package de.diedavids.cuba.dataimport.entity.example;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@NamePattern("%s|name")
@Table(name = "DDCDI_MLB_PLAYER")
@Entity(name = "ddcdi$MlbPlayer")
public class MlbPlayer extends StandardEntity {
    private static final long serialVersionUID = -28922919610355807L;

    @Column(name = "NAME")
    protected String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    protected MlbTeam team;

    @Column(name = "HEIGHT")
    protected Integer height;

    @Column(name = "WEIGHT")
    protected Integer weight;

    @Column(name = "AGE")
    protected Double age;

    @Temporal(TemporalType.DATE)
    @Column(name = "BIRTHDAY")
    protected Date birthday;

    @Column(name = "LEFT_HANDED")
    protected Boolean leftHanded;

    @Column(name = "ANNUAL_SALARY")
    protected BigDecimal annualSalary;

    public void setAnnualSalary(BigDecimal annualSalary) {
        this.annualSalary = annualSalary;
    }

    public BigDecimal getAnnualSalary() {
        return annualSalary;
    }


    public void setLeftHanded(Boolean leftHanded) {
        this.leftHanded = leftHanded;
    }

    public Boolean getLeftHanded() {
        return leftHanded;
    }


    public MlbTeam getTeam() {
        return team;
    }

    public void setTeam(MlbTeam team) {
        this.team = team;
    }


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