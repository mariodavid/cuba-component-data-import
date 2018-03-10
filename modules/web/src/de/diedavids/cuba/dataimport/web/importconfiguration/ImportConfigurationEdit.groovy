package de.diedavids.cuba.dataimport.web.importconfiguration

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.Security
import com.haulmont.cuba.gui.components.AbstractEditor
import com.haulmont.cuba.gui.components.FieldGroup
import com.haulmont.cuba.gui.components.LookupField
import com.haulmont.cuba.gui.data.Datasource
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import com.haulmont.cuba.security.entity.EntityOp
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.Importer
import de.diedavids.cuba.dataimport.web.datasources.ImportersDatasource

import javax.inject.Inject

class ImportConfigurationEdit extends AbstractEditor<ImportConfiguration> {


    @Inject
    private ImportersDatasource importersDs

    @Inject
    private Datasource<ImportConfiguration> importConfigurationDs

    @Inject
    private FieldGroup fieldGroup

    @Inject
    private ComponentsFactory componentsFactory

    private static final String IMPORTER_FIELD_NAME = 'importer'

   @Inject
   Metadata metadata



    protected boolean readPermitted(MetaClass metaClass) {
        return entityOpPermitted(metaClass, EntityOp.READ)
    }

    protected boolean entityOpPermitted(MetaClass metaClass, EntityOp entityOp) {
        Security security = AppBeans.get(Security.NAME)
        return security.isEntityOpPermitted(metaClass, entityOp)
    }



    protected Map<String, Object> getEntitiesLookupFieldOptions() {
        Map<String, Object> options = new TreeMap<>()

        for (MetaClass metaClass : metadata.getTools().getAllPersistentMetaClasses()) {
            if (readPermitted(metaClass)) {
                Class javaClass = metaClass.getJavaClass()
                if (Entity.class.isAssignableFrom(javaClass)) {
                    options.put(messages.getTools().getEntityCaption(metaClass) + " (" + metaClass.getName() + ")", metaClass.getName())
                }
            }
        }

        return options
    }

    @Override
    protected void postInit() {

        fieldGroup.addCustomField(IMPORTER_FIELD_NAME) { datasource, propertyId ->
            LookupField lookupField = (LookupField) componentsFactory.createComponent(LookupField.NAME)
            lookupField.optionsDatasource = importersDs
            lookupField
        }

        FieldGroup.FieldConfig entityClassFieldConfig = fieldGroup.getField("entityClass")

        LookupField lookupField = componentsFactory.createComponent(LookupField)

        lookupField.setDatasource(importConfigurationDs, "entityClass")
        lookupField.setOptionsMap(getEntitiesLookupFieldOptions())


        entityClassFieldConfig.setComponent(lookupField)

        importersDs.addItemChangeListener { e -> 
            if (e.item) {
                item.importerBeanName = e.item.beanName
            }
            else {
                item.importerBeanName = null
            }

        } as Datasource.ItemChangeListener

    }

    @Override
    void ready() {

        if (item.importerBeanName) {
            importersDs.items.each { Importer importer ->
                if (importer.beanName.equalsIgnoreCase(item.importerBeanName)) {
                    ((LookupField) fieldGroup.getFieldComponent(IMPORTER_FIELD_NAME)).value = importer
                }
            }
        }
    }
}