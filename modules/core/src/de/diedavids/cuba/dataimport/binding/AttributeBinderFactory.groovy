package de.diedavids.cuba.dataimport.binding

import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Scripting
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import org.springframework.stereotype.Component

import javax.inject.Inject

@Component('ddcdi_AttributeBinderFactory')
class AttributeBinderFactory {


    @Inject
    SimpleDataLoader simpleDataLoader

    @Inject
    Scripting scripting

    @Inject
    DataManager dataManager


    AttributeBinder createAttributeBinderFromBindingRequest(AttributeBindRequest bindRequest) {

        if (bindRequest.customScriptBindingRequest) {
            return new CustomScriptAttributeBinder(
                    scripting: scripting,
                    dataManager: dataManager
            )
        }
        else if (bindRequest.isDynamicAttributeBindingRequest()) {
            return new DatatypeAttributeBinder()
        }
        else if (bindRequest.isAssociationBindingRequest()) {
            return new AssociationAttributeBinder(
                    simpleDataLoader: simpleDataLoader
            )
        }
        else if (bindRequest.isDatatypeBindingRequest()) {
            return new DatatypeAttributeBinder()
        }
        else if (bindRequest.isEnumBindingRequest()) {
            return new EnumAttributeBinder()
        }

        throw new IllegalStateException("No valid Attribute binder for AttributeBindRequest: $bindRequest found")
    }

}
