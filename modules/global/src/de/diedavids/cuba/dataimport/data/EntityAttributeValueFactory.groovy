package de.diedavids.cuba.dataimport.data

import com.haulmont.cuba.core.entity.Entity
import org.springframework.stereotype.Component

@Component
class EntityAttributeValueFactory {

    EntityAttributeValue createEntityAttributeValue(Entity entity, String entityAttribute) {
        new EntityAttributeValueImpl(
                entityAttribute: entityAttribute,
                value: entity.getValueEx(entityAttribute)
        )
    }
}
