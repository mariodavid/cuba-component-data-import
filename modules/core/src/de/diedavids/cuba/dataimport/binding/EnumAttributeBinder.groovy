package de.diedavids.cuba.dataimport.binding

import com.haulmont.cuba.core.entity.Entity
import groovy.util.logging.Slf4j

@Slf4j
class EnumAttributeBinder implements AttributeBinder {

    @Override
    void bindAttribute(Entity entity, AttributeBindRequest bindRequest) {
        entity.setValueEx(bindRequest.entityAttributePath, getValue(bindRequest))
    }

    private getValue(AttributeBindRequest bindRequest) {
        bindRequest.metaProperty.javaType.fromId(bindRequest.rawValue)
    }

}
