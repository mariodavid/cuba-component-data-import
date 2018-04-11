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
        Class<Enum> enumType = bindRequest.metaProperty.javaType as Class<Enum>
        def value = bindRequest.rawValue.toUpperCase()
        if (enumType.isEnum() && value) {
            try {
                enumType.valueOf(enumType, value)
            }
            catch (IllegalArgumentException e) {
                log.info("Enum value could not be found: $value for Enum: ${enumType.simpleName}. Will be ignored")
                log.debug('Details: ', e)
            }
        }
    }

}
