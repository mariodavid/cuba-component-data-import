package de.diedavids.cuba.dataimport.binding

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.chile.core.model.MetaPropertyPath
import com.haulmont.cuba.core.global.Metadata
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration

class AttributeBindRequest {

    private static final String PATH_SEPARATOR = '.'

    ImportConfiguration importConfiguration
    DataRow dataRow
    ImportAttributeMapper importAttributeMapper

    Metadata metadata


    String getRawValue() {
        def columnValue = dataRow[importAttributeMapper.fileColumnAlias] ?: ''
        ((String) columnValue).trim()
    }

    String getImportEntityClassName() {
        importConfiguration.entityClass
    }

    String getEntityAttributePath() {
        importAttributeMapper.entityAttribute - (importEntityClassName + PATH_SEPARATOR)
    }

    MetaClass getImportEntityMetaClass() {
        metadata.getClass(importEntityClassName)
    }

    MetaPropertyPath getImportEntityPropertyPath() {
        importEntityMetaClass.getPropertyPath(entityAttributePath)
    }

    MetaProperty getMetaProperty() {
        importEntityMetaClass.getPropertyNN(entityAttributePath)
    }

    boolean isCustomScriptBindingRequest() {
        importAttributeMapper.customAttributeBindScript
    }

    boolean isAssociationBindingRequest() {
        importEntityPropertyPath.metaProperties.size() > 1
    }

    boolean isDatatypeBindingRequest() {
        metaProperty.type == MetaProperty.Type.DATATYPE
    }

    boolean isEnumBindingRequest() {
        metaProperty.type == MetaProperty.Type.ENUM
    }
}
