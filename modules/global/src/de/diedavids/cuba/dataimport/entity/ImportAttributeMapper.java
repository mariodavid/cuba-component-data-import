package de.diedavids.cuba.dataimport.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Lob;

@Table(name = "DDCDI_IMPORT_ATTRIBUTE_MAPPER")
@Entity(name = "ddcdi$ImportAttributeMapper")
public class ImportAttributeMapper extends StandardEntity {
    private static final long serialVersionUID = 6042259524321617547L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CONFIGURATION_ID")
    protected ImportConfiguration configuration;


    @NotNull
    @Column(name = "ENTITY_ATTRIBUTE", nullable = false)
    protected String entityAttribute;

    @NotNull
    @Column(name = "FILE_COLUMN_NUMBER", nullable = false)
    protected Integer fileColumnNumber;

    @Column(name = "FILE_COLUMN_ALIAS")
    protected String fileColumnAlias;



    @Lob
    @Column(name = "CUSTOM_ATTRIBUTE_BIND_SCRIPT")
    protected String customAttributeBindScript;

    public void setCustomAttributeBindScript(String customAttributeBindScript) {
        this.customAttributeBindScript = customAttributeBindScript;
    }

    public String getCustomAttributeBindScript() {
        return customAttributeBindScript;
    }


    public void setConfiguration(ImportConfiguration configuration) {
        this.configuration = configuration;
    }

    public ImportConfiguration getConfiguration() {
        return configuration;
    }


    public void setEntityAttribute(String entityAttribute) {
        this.entityAttribute = entityAttribute;
    }

    public String getEntityAttribute() {
        return entityAttribute;
    }

    public void setFileColumnNumber(Integer fileColumnNumber) {
        this.fileColumnNumber = fileColumnNumber;
    }

    public Integer getFileColumnNumber() {
        return fileColumnNumber;
    }

    public void setFileColumnAlias(String fileColumnAlias) {
        this.fileColumnAlias = fileColumnAlias;
    }

    public String getFileColumnAlias() {
        return fileColumnAlias;
    }


}