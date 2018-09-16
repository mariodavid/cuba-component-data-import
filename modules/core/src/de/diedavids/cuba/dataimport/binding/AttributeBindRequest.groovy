package de.diedavids.cuba.dataimport.binding

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.chile.core.model.MetaPropertyPath
import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributes
import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributesUtils
import com.haulmont.cuba.core.global.Metadata
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeMapperMode
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
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
        if (importAttributeMapper.attributeType == AttributeType.ASSOCIATION_ATTRIBUTE && importAttributeMapper.associationLookupAttribute) {
            (importAttributeMapper.entityAttribute + PATH_SEPARATOR + importAttributeMapper.associationLookupAttribute ?: '') - (importEntityClassName + PATH_SEPARATOR)
        }
        else {
            (importAttributeMapper.entityAttribute ?: '') - (importEntityClassName + PATH_SEPARATOR)
        }
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
        importAttributeMapper.customAttributeBindScript && importAttributeMapper.mapperMode == AttributeMapperMode.CUSTOM
    }

    boolean isAssociationBindingRequest() {
        importAttributeMapper.attributeType == AttributeType.ASSOCIATION_ATTRIBUTE
    }

    boolean isDatatypeBindingRequest() {
        metaProperty?.type == MetaProperty.Type.DATATYPE
    }

    boolean isEnumBindingRequest() {
        metaProperty?.type == MetaProperty.Type.ENUM
    }

    boolean isDynamicAttributeBindingRequest() {
        importAttributeMapper.attributeType == AttributeType.DYNAMIC_ATTRIBUTE &&
                dynamicAttributes.getAttributeForMetaClass(importEntityMetaClass, entityAttributePath) as boolean
    }

    Class getJavaType() {
        if (datatypeBindingRequest || enumBindingRequest || dynamicAttributeBindingRequest) {
            metaProperty.javaType
        }
        else if (associationBindingRequest) {
            importEntityPropertyPath.metaProperty.javaType
        }
    }

    boolean isValidAssociationBindingRequest() {
        isAssociationBindingRequest() && importAttributeMapper.associationLookupAttribute
    }

}
