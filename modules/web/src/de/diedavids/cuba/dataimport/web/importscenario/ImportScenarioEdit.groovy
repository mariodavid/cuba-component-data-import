package de.diedavids.cuba.dataimport.web.importscenario

import com.haulmont.cuba.gui.components.AbstractEditor
import com.haulmont.cuba.gui.components.FieldGroup
import com.haulmont.cuba.gui.components.LookupField
import com.haulmont.cuba.gui.data.Datasource
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import de.diedavids.cuba.dataimport.entity.ImportScenario
import de.diedavids.cuba.dataimport.entity.Importer
import de.diedavids.cuba.dataimport.web.datasources.ImportersDatasource

import javax.inject.Inject

class ImportScenarioEdit extends AbstractEditor<ImportScenario> {


    @Inject
    private ImportersDatasource importersDs

    @Inject
    private FieldGroup fieldGroup

    @Inject
    private ComponentsFactory componentsFactory

    private static final String IMPORTER_FIELD_NAME = 'importer'

    @Override
    protected void postInit() {

        fieldGroup.addCustomField(IMPORTER_FIELD_NAME) { datasource, propertyId ->
            LookupField lookupField = (LookupField) componentsFactory.createComponent(LookupField.NAME)
            lookupField.optionsDatasource = importersDs
            lookupField
        }

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