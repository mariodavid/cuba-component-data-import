package de.diedavids.cuba.dataimport.binding

import com.haulmont.cuba.core.entity.Entity

interface AttributeBinder {

    void bindAttribute(Entity entity, AttributeBindRequest bindRequest)
}
