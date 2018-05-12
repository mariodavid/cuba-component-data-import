package de.diedavids.cuba.dataimport.web.importattributemapper

import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.components.AbstractEditor
import com.haulmont.cuba.gui.components.LookupField
import com.haulmont.cuba.gui.data.Datasource
import de.diedavids.cuba.dataimport.converter.MetaPropertyMatcher
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper

import javax.inject.Inject

class ImportAttributeMapperEdit extends AbstractEditor<ImportAttributeMapper> {

    @Inject
    Datasource<ImportAttributeMapper> importAttributeMapperDs

    @Inject
    Metadata metadata

    @Inject
    private LookupField entityAttributeId;

    private List<String> list = new ArrayList<>();

    @Inject
    MetaPropertyMatcher metaPropertyMatcher


    @Override
    protected void postInit() {
        if (getItem()) {

            //get selected entity
            try {
                def entityClass = item.configuration.entityClass
                def focusedClazz = metadata.getClass(entityClass)
                list = metaPropertyMatcher.listProperties(new ArrayList<String>(), "", focusedClazz)

                def found = list.find {
                    it.toLowerCase().startsWith(getItem().fileColumnAlias.toLowerCase())
                }
                entityAttributeId.setOptionsList(list)
                entityAttributeId.setValue(found)

            } catch (Exception e) {
                showNotification("Something went wrong", NotificationType.ERROR)
            }

        }
    }

    @Override
    void init(Map<String, Object> params) {
        entityAttributeId.setNewOptionAllowed(false)
    }

    /* Depreciated */

    void listProperties(String prev, String entityName) {
        if (entityName) {
            def focusedClazz = metadata.getClass(entityName)
            if (focusedClazz) {
                focusedClazz.getProperties().each {
                    if (!it.type.equals(com.haulmont.chile.core.model.MetaProperty.Type.ASSOCIATION)) {
                        if (prev == null || prev.isEmpty())
                            prev = ""
                        else if (!prev.endsWith("."))
                            prev += "."

                        def val = prev.concat(it.name)
                        list.add(val);
                    } else {
                        def nn = it.name
                        def nextEntityName = it.getRange().asClass().name;
                        listProperties(nn, nextEntityName)
                    }
                }
            }
        }
    }
}