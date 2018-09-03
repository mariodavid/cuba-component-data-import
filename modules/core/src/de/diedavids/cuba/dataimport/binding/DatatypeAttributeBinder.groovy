package de.diedavids.cuba.dataimport.binding

import com.haulmont.cuba.core.entity.Entity
import groovy.util.logging.Slf4j

@Slf4j
class DatatypeAttributeBinder implements AttributeBinder {

    DatatypeFactory datatypeFactory

    @Override
    void bindAttribute(Entity entity, AttributeBindRequest bindRequest) {
        entity.setValueEx(bindRequest.entityAttributePath, datatypeFactory.getValue(bindRequest))
    }

}
