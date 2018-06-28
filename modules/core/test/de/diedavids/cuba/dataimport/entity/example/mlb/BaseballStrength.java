package de.diedavids.cuba.dataimport.entity.example.mlb;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@NamePattern("%s|name")
@Table(name = "DDCDI_BASEBALL_STRENGTH")
@Entity(name = "ddcdi$BaseballStrength")
public class BaseballStrength extends StandardEntity {
    private static final long serialVersionUID = -5362066272268979415L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @NotNull
    @Column(name = "CODE", nullable = false)
    protected String code;

    @NotNull
    @Column(name = "SCORE", nullable = false)
    protected Integer score;

    @JoinTable(name = "DDCDI_MLB_PLAYER_BASEBALL_STRENGTH_LINK",
        joinColumns = @JoinColumn(name = "BASEBALL_STRENGTH_ID"),
        inverseJoinColumns = @JoinColumn(name = "MLB_PLAYER_ID"))
    @ManyToMany
    protected List<MlbPlayer> players;

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getScore() {
        return score;
    }


    public void setPlayers(List<MlbPlayer> players) {
        this.players = players;
    }

    public List<MlbPlayer> getPlayers() {
        return players;
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