package de.diedavids.cuba.dataimport.service

import com.haulmont.chile.core.model.MetaProperty
import de.diedavids.cuba.dataimport.binding.AttributeBindRequest


class AssociationDirectReferenceException extends RuntimeException {

    MetaProperty metaProperty

    AttributeBindRequest attributeBindRequest
}
