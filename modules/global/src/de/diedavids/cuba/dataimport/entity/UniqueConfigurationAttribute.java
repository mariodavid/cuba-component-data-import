package de.diedavids.cuba.dataimport.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.chile.core.annotations.NamePattern;

@NamePattern("%s|entityAttribute")
@Table(name = "DDCDI_UNIQUE_CONFIGURATION_ATTRIBUTE")
@Entity(name = "ddcdi$UniqueConfigurationAttribute")
public class UniqueConfigurationAttribute extends StandardEntity {
    private static final long serialVersionUID = -5344248190409697007L;

    @NotNull
    @Column(name = "ENTITY_ATTRIBUTE", nullable = false)
    protected String entityAttribute;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UNIQUE_CONFIGURATION_ID")
    protected UniqueConfiguration uniqueConfiguration;

    public void setUniqueConfiguration(UniqueConfiguration uniqueConfiguration) {
        this.uniqueConfiguration = uniqueConfiguration;
    }

    public UniqueConfiguration getUniqueConfiguration() {
        return uniqueConfiguration;
    }


    public void setEntityAttribute(String entityAttribute) {
        this.entityAttribute = entityAttribute;
    }

    public String getEntityAttribute() {
        return entityAttribute;
    }


}