package de.diedavids.cuba.dataimport.data

import com.haulmont.cuba.core.entity.Entity
import org.springframework.stereotype.Component

@Component('ddcdi_EntityAttributeValueFactory')
class EntityAttributeValueFactory {

    EntityAttributeValue createEntityAttributeValue(Entity entity, String entityAttribute) {
        new EntityAttributeValueImpl(
                entityAttribute: entityAttribute,
                value: entity.getValueEx(entityAttribute)
        )
    }


    EntityAttributeValue createEntityAttributeValue(String entityAttribute, Object attributeValue) {
        new EntityAttributeValueImpl(
                entityAttribute: entityAttribute,
                value: attributeValue
        )
    }

    Collection<EntityAttributeValue> ofMap(Map<String, Object> attributeValueMap) {
        attributeValueMap.collect { String entityAttribute, Object attributeValue ->
            createEntityAttributeValue(entityAttribute, attributeValue)
        }
    }
}
