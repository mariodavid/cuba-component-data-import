package de.diedavids.cuba.dataimport.binding

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.chile.core.model.MetaPropertyPath
import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributes
import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributesUtils
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

    DynamicAttributes dynamicAttributes


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
        if (dynamicAttributeBindingRequest) {
            DynamicAttributesUtils.getMetaPropertyPath(importEntityMetaClass, entityAttributePath)
        } else {
            importEntityMetaClass.getPropertyPath(entityAttributePath)
        }
    }

    MetaProperty getMetaProperty() {
        if (dynamicAttributeBindingRequest) {
            DynamicAttributesUtils.getMetaPropertyPath(importEntityMetaClass, entityAttributePath).metaProperty
        } else {
            importEntityMetaClass.getProperty(entityAttributePath)
        }
    }

    boolean isCustomScriptBindingRequest() {
        importAttributeMapper.customAttributeBindScript
    }

    boolean isAssociationBindingRequest() {
        importEntityPropertyPath?.metaProperties?.size() > 1 ?: false
    }

    boolean isDatatypeBindingRequest() {
        metaProperty?.type == MetaProperty.Type.DATATYPE
    }

    boolean isEnumBindingRequest() {
        metaProperty?.type == MetaProperty.Type.ENUM
    }

    boolean isDynamicAttributeBindingRequest() {
        dynamicAttributes.getAttributeForMetaClass(importEntityMetaClass, entityAttributePath) as boolean
    }

    Class getJavaType() {
        metaProperty.javaType
    }
}
