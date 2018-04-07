package de.diedavids.cuba.dataimport.binding

import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Scripting
import groovy.util.logging.Slf4j

@Slf4j
class CustomGroovyAttributeBinder implements AttributeBinder {

    Scripting scripting

    DataManager dataManager

    @Override
    void bindAttribute(Entity entity, AttributeBindRequest bindRequest) {
        Binding binding = new Binding(
                importConfiguration: bindRequest.importConfiguration,
                importAttributeMapper: bindRequest.importAttributeMapper,
                dataRow: bindRequest.dataRow,
                entityAttribute:  bindRequest.entityAttributePath,
                dataManager: dataManager,
                rawValue: bindRequest.rawValue
        )


        try {
            def value = scripting.evaluateGroovy(bindRequest.importAttributeMapper.customAttributeBindScript, binding)
            entity.setValueEx(bindRequest.entityAttributePath, value)
        }
        catch (Exception e) {
            log.error("Error while executing custom binding script: ${e.getClass()}", e)
        }
    }
}
