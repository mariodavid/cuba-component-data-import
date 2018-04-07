package de.diedavids.cuba.dataimport.binding

import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Scripting
import groovy.util.logging.Slf4j

@Slf4j
class CustomScriptAttributeBinder implements AttributeBinder {

    Scripting scripting

    DataManager dataManager

    @Override
    void bindAttribute(Entity entity, AttributeBindRequest bindRequest) {
        Binding binding = createScriptBinding(bindRequest)
        tryToExecuteCustomScriptAndSetValue(bindRequest, binding, entity)
    }

    private void tryToExecuteCustomScriptAndSetValue(AttributeBindRequest bindRequest, Binding binding, Entity entity) {
        try {
            def value = getValue(bindRequest, binding)
            entity.setValueEx(bindRequest.entityAttributePath, value)
        }
        catch (Exception e) {
            log.error("Error while executing custom binding script: ${e.getClass()}", e)
        }
    }

    private Binding createScriptBinding(AttributeBindRequest bindRequest) {
        new Binding(
                importConfiguration: bindRequest.importConfiguration,
                importAttributeMapper: bindRequest.importAttributeMapper,
                dataRow: bindRequest.dataRow,
                entityAttribute: bindRequest.entityAttributePath,
                dataManager: dataManager,
                rawValue: bindRequest.rawValue
        )
    }

    private Object getValue(AttributeBindRequest bindRequest, Binding binding) {
        scripting.evaluateGroovy(bindRequest.importAttributeMapper.customAttributeBindScript, binding)
    }
}
