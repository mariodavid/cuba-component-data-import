package de.diedavids.cuba.dataimport.web.importattributemapper

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.components.AbstractFrame
import com.haulmont.cuba.gui.components.FieldGroup
import com.haulmont.cuba.gui.components.LookupField
import com.haulmont.cuba.gui.components.SourceCodeEditor
import com.haulmont.cuba.gui.data.Datasource
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.web.util.MetadataSelector

import javax.inject.Inject

class ImportAttributeMapperCustomFrame extends AbstractFrame {


    static final String ENTITY_ATTRIBUTE_NAME = 'entityAttribute'
    static final String ENTITY_ATTRIBUTE_LABEL = 'entityAttributeLabel'
    static final String CUSTOM_ENTITY_ATTRIBUTE_NAME = 'customEntityAttribute'


    @Inject
    Datasource<ImportAttributeMapper> importAttributeMapperDs

    @Inject
    SourceCodeEditor customAttributeBindScriptEditor

    @Inject
    ComponentsFactory componentsFactory

    @Inject
    FieldGroup fieldGroup

    @Inject
    MetadataSelector metadataSelector

    @Inject
    Metadata metadata

    @Override
    void init(Map<String, Object> params) {
        super.init(params)
        initCustomEntityAttributeLookup()
    }


    def initCustomEntityAttributeLookup() {
        FieldGroup.FieldConfig fieldConfig = fieldGroup.getField(CUSTOM_ENTITY_ATTRIBUTE_NAME)
        def customEntityAttribute = componentsFactory.createComponent(LookupField)
        customEntityAttribute.setOptionsMap(metadataSelector.getAllAttributesLookupFieldOptions(metaClassOfItem))
        customEntityAttribute.setDatasource(importAttributeMapperDs, ENTITY_ATTRIBUTE_NAME)
        customEntityAttribute.caption = messages.getMessage(this.class, ENTITY_ATTRIBUTE_LABEL)
        fieldConfig.setComponent(customEntityAttribute)
    }

    MetaClass getMetaClassOfItem() {
        metadata.session.getClass(importAttributeMapperDs.item.configuration.entityClass)
    }
}