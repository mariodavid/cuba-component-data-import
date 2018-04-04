package de.diedavids.cuba.dataimport.web.importconfiguration

import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.components.AbstractEditor
import com.haulmont.cuba.gui.components.FieldGroup
import com.haulmont.cuba.gui.components.LookupField
import com.haulmont.cuba.gui.data.Datasource
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.Importer
import de.diedavids.cuba.dataimport.web.util.EntityClassSelector
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

    @Inject
    EntityClassSelector entityClassSelector

    private static final String ENTITY_CLASS_FIELD_NAME = 'entityClass'


    @Override
    protected void postInit() {
        initImporterField()
        initEntityClassField()
        initImportersDatasourceItemChangeListener()

    }

    private Datasource.ItemChangeListener initImportersDatasourceItemChangeListener() {
        importersDs.addItemChangeListener { e ->
            if (e.item) {
                item.importerBeanName = e.item.beanName
            } else {
                item.importerBeanName = null
            }

        } as Datasource.ItemChangeListener
    }

    private void initEntityClassField() {
        FieldGroup.FieldConfig entityClassFieldConfig = fieldGroup.getField(ENTITY_CLASS_FIELD_NAME)

        LookupField lookupField = componentsFactory.createComponent(LookupField)

        lookupField.setDatasource(importConfigurationDs, ENTITY_CLASS_FIELD_NAME)
        lookupField.setOptionsMap(entityClassSelector.entitiesLookupFieldOptions)


        entityClassFieldConfig.setComponent(lookupField)
    }

    private initImporterField() {
        fieldGroup.addCustomField(IMPORTER_FIELD_NAME) { datasource, propertyId ->
            LookupField lookupField = (LookupField) componentsFactory.createComponent(LookupField.NAME)
            lookupField.optionsDatasource = importersDs
            lookupField
        }
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