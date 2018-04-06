package de.diedavids.cuba.dataimport.service

import com.haulmont.chile.core.model.MetaPropertyPath
import de.diedavids.cuba.dataimport.dto.DataRow

class MultipleAssociationValuesFoundException extends RuntimeException {

    Object value
    DataRow dataRow
    MetaPropertyPath propertyPath
    Collection<?> allResults

}
