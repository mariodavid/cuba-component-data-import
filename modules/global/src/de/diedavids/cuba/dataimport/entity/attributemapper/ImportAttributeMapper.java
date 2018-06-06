package de.diedavids.cuba.dataimport.entity.attributemapper;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import com.haulmont.chile.core.datatypes.FormatStrings;
import com.haulmont.cuba.core.entity.StandardEntity;
import de.diedavids.cuba.dataimport.entity.ImportConfiguration;
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy;

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
    @Column(name = "MAPPER_MODE", nullable = false)
    protected String mapperMode;

    @Column(name = "ATTRIBUTE_TYPE")
    protected String attributeType;

    @Column(name = "ENTITY_ATTRIBUTE")
    protected String entityAttribute;

    @Column(name = "ASSOCIATION_LOOKUP_ATTRIBUTE")
    protected String associationLookupAttribute;

    @NotNull
    @Column(name = "FILE_COLUMN_NUMBER", nullable = false)
    protected Integer fileColumnNumber;

    @Column(name = "FILE_COLUMN_ALIAS")
    protected String fileColumnAlias;



    @Lob
    @Column(name = "CUSTOM_ATTRIBUTE_BIND_SCRIPT")
    protected String customAttributeBindScript;

    public void setMapperMode(AttributeMapperMode mapperMode) {
        this.mapperMode = mapperMode == null ? null : mapperMode.getId();
    }

    public AttributeMapperMode getMapperMode() {
        return mapperMode == null ? null : AttributeMapperMode.fromId(mapperMode);
    }


    public void setAssociationLookupAttribute(String associationLookupAttribute) {
        this.associationLookupAttribute = associationLookupAttribute;
    }

    public String getAssociationLookupAttribute() {
        return associationLookupAttribute;
    }


    public void setAttributeType(AttributeType attributeType) {
        this.attributeType = attributeType == null ? null : attributeType.getId();
    }

    public AttributeType getAttributeType() {
        return attributeType == null ? null : AttributeType.fromId(attributeType);
    }



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


    @PostConstruct
    protected void initDefaultValues() {
        initDefaultMapperMode();
    }

    private void initDefaultMapperMode() {
        setMapperMode(AttributeMapperMode.AUTOMATIC);
    }

    public boolean isBindable() {
        if (isCustom()) {
            return entityAttribute != null;
        }
        else {
            return entityAttribute != null && attributeType != null;
        }
    }

    public boolean isCustom() {
        return getMapperMode() == AttributeMapperMode.CUSTOM;
    }

    public boolean isAutomatic() {
        return getMapperMode() == AttributeMapperMode.AUTOMATIC;
    }
}