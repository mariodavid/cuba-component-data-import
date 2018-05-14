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

    @Named("fieldGroup.dynamicAttribute")
    private CheckBox dynamicAttribute;

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
                showNotification(caption)
                lookupField.setValue(caption)
                //TODO: check if new value is already in in the list, it shouldn't be dynamic in this case
            }
        })

        dynamicAttribute.addValueChangeListener(new Component.ValueChangeListener() {
            @Override
            void valueChanged(Component.ValueChangeEvent e) {
                if (Boolean.FALSE.equals(e.getValue())) {
                    lookupField.setNewOptionAllowed(false)
                    updateList(lookupField)
                } else {
                    lookupField.setNewOptionAllowed(true)
                    updateList(lookupField)
                }
            }
        })


    }

    void updateList(LookupField entityAttributeId) {
        if (item) {
            //get selected entity
            try {
                def entityClass = item.configuration.entityClass
                def focusedClazz = metadata.getClass(entityClass)
                def list = metaPropertyMatcher.listProperties([], '', focusedClazz)

                def found = list.find {
                    it.toLowerCase().startsWith(item.fileColumnAlias.toLowerCase())
                }

                if(item.dynamicAttribute){
                    def attribute = item.entityAttribute
                    found = attribute
                    list = [found]
                }

                entityAttributeId.setOptionsList(list)
                entityAttributeId.setValue(found)

            } catch (Exception e) {
                //showNotification('Something went wrong', NotificationType.ERROR)
                entityAttributeId.setNewOptionAllowed(true)
            }

        }
    }
}