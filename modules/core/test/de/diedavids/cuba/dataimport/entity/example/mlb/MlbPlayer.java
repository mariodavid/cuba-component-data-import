package de.diedavids.cuba.dataimport.entity.example.mlb;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@NamePattern("%s|name")
@Table(name = "DDCDI_MLB_PLAYER")
@Entity(name = "ddcdi$MlbPlayer")
public class MlbPlayer extends StandardEntity {
    private static final long serialVersionUID = -4518611612657008151L;

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

    @Column(name = "BIRTHDAY_LOCAL_DATE")
    protected LocalDate birthdayLocalDate;

    @Column(name = "LEFT_HANDED")
    protected Boolean leftHanded;

    @Column(name = "ANNUAL_SALARY")
    protected BigDecimal annualSalary;

    @Column(name = "ANNUAL_SALARY_LONG")
    protected Long annualSalaryLong;

    @JoinTable(name = "DDCDI_MLB_PLAYER_BASEBALL_STRENGTH_LINK",
            joinColumns = @JoinColumn(name = "MLB_PLAYER_ID"),
            inverseJoinColumns = @JoinColumn(name = "BASEBALL_STRENGTH_ID"))
    @ManyToMany
    protected List<BaseballStrength> strengths;

    public LocalDate getBirthdayLocalDate() {
        return birthdayLocalDate;
    }

    public void setBirthdayLocalDate(LocalDate birthdayLocalDate) {
        this.birthdayLocalDate = birthdayLocalDate;
    }

    public Long getAnnualSalaryLong() {
        return annualSalaryLong;
    }

    public void setAnnualSalaryLong(Long annualSalaryLong) {
        this.annualSalaryLong = annualSalaryLong;
    }

    public void setStrengths(List<BaseballStrength> strengths) {
        this.strengths = strengths;
    }

    public List<BaseballStrength> getStrengths() {
        return strengths;
    }


    public void setAnnualSalary(BigDecimal annualSalary) {
        this.annualSalary = annualSalary;
    }

    public BigDecimal getAnnualSalary() {
        return annualSalary;
    }


    public void setTeam(MlbTeam team) {
        this.team = team;
    }

    public MlbTeam getTeam() {
        return team;
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

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setLeftHanded(Boolean leftHanded) {
        this.leftHanded = leftHanded;
    }

    public Boolean getLeftHanded() {
        return leftHanded;
    }


}