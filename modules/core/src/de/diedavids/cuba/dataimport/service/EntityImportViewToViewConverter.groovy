package de.diedavids.cuba.dataimport.service

import com.haulmont.cuba.core.app.importexport.EntityImportView
import com.haulmont.cuba.core.app.importexport.EntityImportViewProperty
import com.haulmont.cuba.core.global.View
import org.springframework.stereotype.Component

@Component('ddcd    i_EntityImportViewToViewConverter')
class EntityImportViewToViewConverter {

    View convert(EntityImportView entityImportView) {
        def view = new View(entityImportView.entityClass)

        entityImportView.properties.each { EntityImportViewProperty property ->
            view.addProperty(property.name)
        }

        view
    }
}
