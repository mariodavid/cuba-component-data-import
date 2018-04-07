package de.diedavids.cuba.dataimport.binding

import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.chile.core.model.MetaPropertyPath
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Scripting
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import org.springframework.stereotype.Component

import javax.inject.Inject


@Component
class AttributeBinderFactory {

    @Inject
    SimpleDataLoader simpleDataLoader

    @Inject
    Scripting scripting

    @Inject
    DataManager dataManager

    AttributeBinder createDatatypeAttributeBinder() {
        new DatatypeAttributeBinder()
    }

    AttributeBinder createAttributeBinderFromBindingRequest(AttributeBindRequest bindRequest) {

        if (bindRequest.importAttributeMapper.customAttributeBindScript) {
            return new CustomGroovyAttributeBinder(
                    scripting: scripting,
                    dataManager: dataManager
            )
        }

        if (isAssociatedAttribute(bindRequest.importEntityPropertyPath)) {
            return new AssociationAttributeBinder(simpleDataLoader: simpleDataLoader)
        }
        switch (bindRequest.metaProperty.type) {
            case MetaProperty.Type.ENUM: return new EnumAttributeBinder()
            case MetaProperty.Type.DATATYPE: return new DatatypeAttributeBinder()
        }
    }

    private boolean isAssociatedAttribute(MetaPropertyPath path) {
        path.metaProperties.size() > 1
    }


}
