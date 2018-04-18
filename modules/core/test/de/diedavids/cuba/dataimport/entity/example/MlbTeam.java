package de.diedavids.cuba.dataimport.entity.example;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.UniqueConstraint;
import org.hibernate.validator.constraints.Length;

@NamePattern("%s|name")
@Table(name = "DDCDI_MLB_TEAM", uniqueConstraints = {
    @UniqueConstraint(name = "IDX_DDCDI_MLB_TEAM_CODE_UNQ", columnNames = {"CODE", "DELETE_TS"})
})
@Entity(name = "ddcdi$MlbTeam")
public class MlbTeam extends StandardEntity {
    private static final long serialVersionUID = -9031136045471665865L;

    @Length(min = 1)
    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @NotNull
    @Column(name = "CODE", nullable = false)
    protected String code;

    @Column(name = "STATE")
    protected String state;

    public void setState(State state) {
        this.state = state == null ? null : state.getId();
    }

    public State getState() {
        return state == null ? null : State.fromId(state);
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


}