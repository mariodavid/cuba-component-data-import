package de.diedavids.cuba.dataimport.web.importattributemapper

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.components.FieldGroup
import com.haulmont.cuba.gui.components.LookupField
import com.haulmont.cuba.gui.components.OptionsGroup
import com.haulmont.cuba.gui.data.Datasource
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.web.util.MetadataSelector

class EntityAttributeSelectionHandler {


    private static final String ATTRIBUTE_TYPE_NAME = 'attributeType'

    private static final String ENTITY_ATTRIBUTE_NAME = 'entityAttribute'
    private static final String ENTITY_ATTRIBUTE_LABEL = 'entityAttributeLabel'

    private static final String DYNAMIC_ENTITY_ATTRIBUTE_NAME = 'dynamicEntityAttribute'

    private static final String ASSOCIATION_ENTITY_ATTRIBUTE_NAME = 'associationEntityAttribute'
    private static final String ASSOCIATION_LOOKUP_NAME = 'associationLookupAttribute'


    Datasource<ImportAttributeMapper> importAttributeMapperDs
    Metadata metadata
    ComponentsFactory componentsFactory
    FieldGroup fieldGroup
    MetadataSelector metadataSelector
    Messages messages

    private LookupField entityAttribute
    private LookupField dynamicEntityAttribute
    private LookupField associationEntityAttribute
    private LookupField associationLookupAttribute



    void registerLookupChangeListener() {

        importAttributeMapperDs.addItemPropertyChangeListener { Datasource.ItemPropertyChangeEvent<ImportAttributeMapper> e ->
            if (e.property == ATTRIBUTE_TYPE_NAME) {
                clearEntityAttributeValues()
                showLookupFieldsForAttributeType(e.value as AttributeType)
            }
        }

        importAttributeMapperDs.addItemPropertyChangeListener { Datasource.ItemPropertyChangeEvent<ImportAttributeMapper> e ->
            if (didAssociationEntityAttributeChange(e)) {
                activateAssociationLookupAttributes(e.value as String)
            }
        }
    }

    boolean didAssociationEntityAttributeChange(Datasource.ItemPropertyChangeEvent<ImportAttributeMapper> event) {
        event.property == ENTITY_ATTRIBUTE_NAME && importAttributeMapperDs.item.attributeType == AttributeType.ASSOCIATION_ATTRIBUTE
    }

    protected void initEntityAttributeLookupFields() {
        initDynamicEntityAttributeLookup()
        initEntityAttributeLookup()
        initAssociationEntityAttributeLookup()
        initAssociationLookupAttributeLookup()
        initAttributeTypeOptionsGroup()
        showLookupFieldsForAttributeType(importAttributeMapperDs.item.attributeType)
    }

    void clearEntityAttributeValues() {
        importAttributeMapperDs.item.entityAttribute = null
        importAttributeMapperDs.item.associationLookupAttribute = null
    }

    def initAssociationEntityAttributeLookup() {
        FieldGroup.FieldConfig fieldConfig = fieldGroup.getField(ASSOCIATION_ENTITY_ATTRIBUTE_NAME)
        associationEntityAttribute = componentsFactory.createComponent(LookupField)
        associationEntityAttribute.setOptionsMap(metadataSelector.getAssociationAttributes(metaClassOfItem))
        associationEntityAttribute.setDatasource(importAttributeMapperDs, ENTITY_ATTRIBUTE_NAME)
        associationEntityAttribute.caption = messages.getMessage(this.class, ENTITY_ATTRIBUTE_LABEL)
        fieldConfig.setComponent(associationEntityAttribute)
    }

    def initAssociationLookupAttributeLookup() {
        FieldGroup.FieldConfig fieldConfig = fieldGroup.getField(ASSOCIATION_LOOKUP_NAME)
        associationLookupAttribute = componentsFactory.createComponent(LookupField)
        associationLookupAttribute.setDatasource(importAttributeMapperDs, ASSOCIATION_LOOKUP_NAME)

        associationLookupAttribute.setNewOptionAllowed(true)
        associationLookupAttribute.newOptionHandler = { String caption ->
            importAttributeMapperDs.item.associationLookupAttribute = caption
        }

        if (importAttributeMapperDs.item.attributeType == AttributeType.ASSOCIATION_ATTRIBUTE && importAttributeMapperDs.item.entityAttribute) {
            activateAssociationLookupAttributes(importAttributeMapperDs.item.entityAttribute)
        } else {
            associationLookupAttribute.enabled = false
        }
        fieldConfig.setComponent(associationLookupAttribute)
    }

    void activateAssociationLookupAttributes(String metaProperty) {
        if (metaProperty) {
            MetaClass importConfigurationMetaClass = metadata.session.getClass(importAttributeMapperDs.item.configuration.entityClass)
            def assocProperties = importConfigurationMetaClass.getProperty(metaProperty)?.range?.asClass()?.ownProperties
            associationLookupAttribute.optionsMap = metadataSelector.getLookupMetaProperties(assocProperties)
            associationLookupAttribute.enabled = true
        }
    }

    def initDynamicEntityAttributeLookup() {
        FieldGroup.FieldConfig fieldConfig = fieldGroup.getField(DYNAMIC_ENTITY_ATTRIBUTE_NAME)
        dynamicEntityAttribute = componentsFactory.createComponent(LookupField)
        dynamicEntityAttribute.setOptionsMap(metadataSelector.getDynamicAttributesLookupFieldOptions(metaClassOfItem))
        dynamicEntityAttribute.setDatasource(importAttributeMapperDs, ENTITY_ATTRIBUTE_NAME)
        dynamicEntityAttribute.caption = messages.getMessage(this.class, ENTITY_ATTRIBUTE_LABEL)
        fieldConfig.setComponent(dynamicEntityAttribute)
    }

    private MetaClass getMetaClassOfItem() {
        metadata.session.getClass(importAttributeMapperDs.item.configuration.entityClass)
    }

    def initEntityAttributeLookup() {
        FieldGroup.FieldConfig fieldConfig = fieldGroup.getField(ENTITY_ATTRIBUTE_NAME)
        entityAttribute = componentsFactory.createComponent(LookupField)
        entityAttribute.setOptionsMap(metadataSelector.getDirectAttributesLookupFieldOptions(metaClassOfItem))
        entityAttribute.setDatasource(importAttributeMapperDs, ENTITY_ATTRIBUTE_NAME)
        fieldConfig.setComponent(entityAttribute)
    }

    void initAttributeTypeOptionsGroup() {
        FieldGroup.FieldConfig fieldConfig = fieldGroup.getField(ATTRIBUTE_TYPE_NAME)
        OptionsGroup component = componentsFactory.createComponent(OptionsGroup)
        component.setOptionsEnum(AttributeType)
        component.setDatasource(importAttributeMapperDs, ATTRIBUTE_TYPE_NAME)
        fieldConfig.setComponent(component)
    }

    void showLookupFieldsForAttributeType(AttributeType attributeType) {
        switch (attributeType) {
            case AttributeType.DIRECT_ATTRIBUTE: displayLookupFieldsForDirectAttribute(); break
            case AttributeType.DYNAMIC_ATTRIBUTE: displayLookupFieldsForDynamicAttribute(); break
            case AttributeType.ASSOCIATION_ATTRIBUTE: displayLookupFieldsForAssociationAttribute(); break
            default: hideEntityAttributeLookupFields(); break
        }
    }

    private void hideEntityAttributeLookupFields() {
        dynamicEntityAttribute.visible = false
        entityAttribute.visible = false
        associationEntityAttribute.visible = false
        associationLookupAttribute.visible = false
    }

    private void displayLookupFieldsForAssociationAttribute() {
        dynamicEntityAttribute.visible = false
        entityAttribute.visible = false
        associationEntityAttribute.visible = true
        associationLookupAttribute.visible = true
        associationLookupAttribute.required = true
    }

    private void displayLookupFieldsForDirectAttribute() {
        dynamicEntityAttribute.visible = false
        entityAttribute.visible = true
        associationEntityAttribute.visible = false
        associationLookupAttribute.visible = false
    }

    private void displayLookupFieldsForDynamicAttribute() {
        dynamicEntityAttribute.visible = true
        dynamicEntityAttribute.required = true
        entityAttribute.visible = false
        associationEntityAttribute.visible = false
        associationLookupAttribute.visible = false
    }

}