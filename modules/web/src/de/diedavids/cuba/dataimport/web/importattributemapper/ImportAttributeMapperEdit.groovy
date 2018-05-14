package de.diedavids.cuba.dataimport.web.importattributemapper

import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.data.Datasource
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import de.diedavids.cuba.dataimport.converter.MetaPropertyMatcher
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper

import javax.inject.Inject
import javax.inject.Named

class ImportAttributeMapperEdit extends AbstractEditor<ImportAttributeMapper> {

    private static final String ENTITY_ATTRIBUTE_FIELD_NAME = 'entityAttribute'

    @Inject
    Datasource<ImportAttributeMapper> importAttributeMapperDs

    @Inject
    Metadata metadata

    @Inject
    MetaPropertyMatcher metaPropertyMatcher

    @Named('fieldGroup.dynamicAttribute')
    private CheckBox dynamicAttribute

    @Inject
    private ComponentsFactory componentsFactory

    @Inject
    private FieldGroup fieldGroup

    @Override
    protected void postInit() {
        FieldGroup.FieldConfig entityClassFieldConfig = fieldGroup.getField(ENTITY_ATTRIBUTE_FIELD_NAME)
        LookupField lookupField = componentsFactory.createComponent(LookupField)
        lookupField.setDatasource(importAttributeMapperDs, ENTITY_ATTRIBUTE_FIELD_NAME)
        updateList(lookupField)
        entityClassFieldConfig.setComponent(lookupField)

        lookupField.setNewOptionHandler(new LookupField.NewOptionHandler() {
            @Override
            void addNewOption(String caption) {
                showNotification(String.format('Using a dynamic attribute: %s', caption))
                lookupField.setValue(caption)
                //TODO: check if new value is already in in the list, it shouldn't be dynamic in this case
            }
        })
        initDynamicAttributeChanger(lookupField)
    }

    void initDynamicAttributeChanger(LookupField lookupField) {
        dynamicAttribute.addValueChangeListener(new Component.ValueChangeListener() {
            @Override
            void valueChanged(Component.ValueChangeEvent e) {
                updateList(lookupField)
            }
        })
    }

    void useDynamicAttributeInput(LookupField lookupField) {
        lookupField.setNewOptionAllowed(true)
        lookupField.setDescription('Add new dynamic attribute to the object')
    }

    void userPropertiesListInput(LookupField lookupField) {
        lookupField.setNewOptionAllowed(false)
        lookupField.setDescription(null)
    }

    List getList(ImportAttributeMapper item) {
        if (item) {
            def entityClass = item.configuration.entityClass
            def focusedClazz = metadata.getClass(entityClass)
            return metaPropertyMatcher.listProperties([], '', focusedClazz)
        }
        []
    }

    void updateList(LookupField entityAttributeId) {
        if (item) {
            try {
                def list = getList(item)
                def found = list.find {
                    it.toLowerCase().startsWith(item.fileColumnAlias.toLowerCase())
                }

                if (item.dynamicAttribute) {
                    useDynamicAttributeInput(entityAttributeId)
                    found = item.entityAttribute
                    list = [found]
                } else {
                    userPropertiesListInput(entityAttributeId)
                }
                entityAttributeId.setOptionsList(list)
                entityAttributeId.setValue(found)
            } catch (Exception e) {
                entityAttributeId.setNewOptionAllowed(true)
            }
        }
    }
}