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
    private LookupField entityAttributeId


    @Inject
    MetaPropertyMatcher metaPropertyMatcher


    @Override
    protected void postInit() {
        if (item) {
            //get selected entity
            try {
                def entityClass = item.configuration.entityClass
                def focusedClazz = metadata.getClass(entityClass)
                def list = metaPropertyMatcher.listProperties([], '', focusedClazz)

                def found = list.find {
                    it.toLowerCase().startsWith(item.fileColumnAlias.toLowerCase())
                }
                entityAttributeId.setOptionsList(list)
                entityAttributeId.setValue(found)

            } catch (Exception e) {
                showNotification('Something went wrong', NotificationType.ERROR)
            }

        }
    }
}