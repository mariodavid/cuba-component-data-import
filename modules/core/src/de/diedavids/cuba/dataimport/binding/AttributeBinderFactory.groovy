package de.diedavids.cuba.dataimport.binding

import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Scripting
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import javax.inject.Inject

@Slf4j
@Component('ddcdi_AttributeBinderFactory')
class AttributeBinderFactory {

    @Inject
    DatatypeFactory datatypeFactory

    @Inject
    SimpleDataLoader simpleDataLoader

    @Inject
    Scripting scripting

    @Inject
    DataManager dataManager


    AttributeBinder createAttributeBinderFromBindingRequest(AttributeBindRequest bindRequest) {

        AttributeBinder binder = null

        if (bindRequest.customScriptBindingRequest) {
            binder = new CustomScriptAttributeBinder(scripting: scripting, dataManager: dataManager)
        }
        else if (bindRequest.isDynamicAttributeBindingRequest()) {
            binder = createDatatypeAttributeBinder()
        }
        else if (bindRequest.isAssociationBindingRequest()) {
            binder = new AssociationAttributeBinder(simpleDataLoader: simpleDataLoader, datatypeFactory: datatypeFactory)
        }
        else if (bindRequest.isDatatypeBindingRequest()) {
            binder = createDatatypeAttributeBinder()
        }
        else if (bindRequest.isEnumBindingRequest()) {
            binder = new EnumAttributeBinder()
        }
        else {
            log.warn("No valid Attribute binder for AttributeBindRequest: $bindRequest found. Will be ignored")
        }

        binder
    }

    protected DatatypeAttributeBinder createDatatypeAttributeBinder() {
        new DatatypeAttributeBinder(datatypeFactory: datatypeFactory)
    }

}
