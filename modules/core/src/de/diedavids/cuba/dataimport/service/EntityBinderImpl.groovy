package de.diedavids.cuba.dataimport.service

import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.chile.core.model.MetaPropertyPath
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.Metadata
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import javax.inject.Inject
import java.text.SimpleDateFormat

@Slf4j
@Component(EntityBinder.NAME)
class EntityBinderImpl implements EntityBinder {

    @Inject
    Metadata metadata

    @Inject
    SimpleDataLoader simpleDataLoader

    private static final String PATH_SEPERATOR = '.'


    @Override
    Entity bindAttributes(ImportConfiguration importConfiguration, DataRow dataRow, Entity entity) {

        importConfiguration.importAttributeMappers.each { ImportAttributeMapper importAttributeMapper ->
            bindAttribute(importConfiguration, dataRow, entity, importAttributeMapper)
        }

        entity
    }

    private void bindAttribute(ImportConfiguration importConfiguration, DataRow dataRow, Entity entity, ImportAttributeMapper importAttributeMapper) {

        def importEntityClassName = importConfiguration.entityClass

        String rawValue = ((String) dataRow[importAttributeMapper.fileColumnAlias]).trim()

        def entityAttribute = importAttributeMapper.entityAttribute - (importEntityClassName + PATH_SEPERATOR)
        MetaPropertyPath path = metadata.getClass(importEntityClassName).getPropertyPath(entityAttribute)

        if (isAssociatedAttribute(path)) {
            handleAssociationAttribute(path, rawValue, entity)
        }
        else {
            MetaProperty metaProperty = metadata.getClass(importEntityClassName).getPropertyNN(entityAttribute)
            def value = getValue(metaProperty, rawValue, dataRow, importConfiguration)
            entity.setValueEx(entityAttribute, value)
        }
    }


    private void handleAssociationAttribute(MetaPropertyPath path, String rawValue, Entity entity) {
        def propertyPathFromAssociation = path.path.drop(1)
        def propertyPath = propertyPathFromAssociation.join(PATH_SEPERATOR)
        def associationJavaType = path.metaProperties[0].javaType
        def associationProperty = path.metaProperties[0].name
        def associationValue = simpleDataLoader.loadByProperty(associationJavaType, propertyPath, rawValue)

        entity.setValueEx(associationProperty, associationValue)
    }

    private boolean isAssociatedAttribute(MetaPropertyPath path) {
        path.metaProperties.size() > 1
    }

    private getValue(MetaProperty metaProperty, String rawValue, DataRow dataRow, ImportConfiguration importConfiguration) {
        def value = null


        if (metaProperty.type == MetaProperty.Type.ENUM) {
            value = handleEnumValues(metaProperty, rawValue)

        }
        else if (metaProperty.type == MetaProperty.Type.DATATYPE) {
            value = handleDatatypeValue(metaProperty, rawValue, dataRow, importConfiguration)
        }


        value
    }

    private handleEnumValues(MetaProperty metaProperty, String rawValue) {
        metaProperty.javaType.fromId(rawValue)
    }

    private handleDatatypeValue(MetaProperty metaProperty, String rawValue, DataRow dataRow, ImportConfiguration importConfiguration) {
        switch (metaProperty.javaType) {
            case Integer: return getIntegerValue(rawValue, dataRow)
            case Double: return getDoubleValue(rawValue, dataRow)
            case Date: return getDateValue(importConfiguration, rawValue)
            case String: return getStringValue(rawValue)
        }
    }

    private Integer getIntegerValue(String rawValue, DataRow dataRow) {
        try {
            return Integer.parseInt(rawValue)
        }
        catch (NumberFormatException e) {
            log.warn("Number could not be read: '$rawValue' in [$dataRow]. Will be ignored.")
        }
    }

    private String getStringValue(String rawValue) {
        rawValue
    }

    @SuppressWarnings('SimpleDateFormatMissingLocale')
    private Date getDateValue(ImportConfiguration importConfiguration, String rawValue) {
        SimpleDateFormat formatter = new SimpleDateFormat(importConfiguration.dateFormat)
        formatter.parse(rawValue)
    }

    private Double getDoubleValue(String rawValue, DataRow dataRow) {
        try {
            return Double.parseDouble(rawValue)
        }
        catch (NumberFormatException e) {
            log.warn("Number could not be read: '$rawValue' in [$dataRow]. Will be ignored.")
        }
    }

}