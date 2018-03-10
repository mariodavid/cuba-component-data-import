package de.diedavids.cuba.dataimport.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Table(name = "DDCDI_IMPORT_ATTRIBUTE_MAPPER")
@Entity(name = "ddcdi$ImportAttributeMapper")
public class ImportAttributeMapper extends StandardEntity {
    private static final long serialVersionUID = 6042259524321617547L;

    @NotNull
    @Column(name = "ENTITY_ATTRIBUTE", nullable = false)
    protected String entityAttribute;

    @NotNull
    @Column(name = "FILE_COLUMN_NUMBER", nullable = false)
    protected Integer fileColumnNumber;

    @Column(name = "FILE_COLUMN_ALIAS")
    protected String fileColumnAlias;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IMPORT_SCENARIO_ID")
    protected ImportScenario importScenario;

    public void setImportScenario(ImportScenario importScenario) {
        this.importScenario = importScenario;
    }

    public ImportScenario getImportScenario() {
        return importScenario;
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