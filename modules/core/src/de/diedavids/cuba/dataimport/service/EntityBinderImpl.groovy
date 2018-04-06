package de.diedavids.cuba.dataimport.service

import com.haulmont.chile.core.model.MetaClass
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

        String entityAttribute = importAttributeMapper.entityAttribute - (importEntityClassName + PATH_SEPERATOR)
        MetaClass importEntityMetaClass = metadata.getClass(importEntityClassName)
        MetaPropertyPath path = importEntityMetaClass.getPropertyPath(entityAttribute)

        if (isAssociatedAttribute(path)) {
            try {
                handleAssociationAttribute(path, rawValue, entity)
            }
            catch (MultipleAssociationValuesFoundException e) {
                e.dataRow = dataRow
                e.propertyPath = path
                log.warn("Multiple associations found for data row: [${e.dataRow}] and attribute: ${e.propertyPath} with value ${e.value}. Found associations: ${e.allResults}. Will be ignored.")
            }
        } else {
            MetaProperty metaProperty = importEntityMetaClass.getPropertyNN(entityAttribute)
            def value = getValue(metaProperty, rawValue, dataRow, importConfiguration)
            entity.setValueEx(entityAttribute, value)
        }
    }


    private void handleAssociationAttribute(MetaPropertyPath path, String rawValue, Entity entity) {
        def propertyPathFromAssociation = path.path.drop(1)
        def propertyPath = propertyPathFromAssociation.join(PATH_SEPERATOR)
        def associationJavaType = path.metaProperties[0].javaType
        def associationProperty = path.metaProperties[0].name

        def associationValue = loadAssociationValue(associationJavaType, propertyPath, rawValue)
        entity.setValueEx(associationProperty, associationValue)
    }

    private loadAssociationValue(Class<?> associationJavaType, String propertyPath, String rawValue) {
        def allResults = simpleDataLoader.loadAllByProperty(associationJavaType, propertyPath, rawValue)

        if (allResults.size() > 1) {
            throw new MultipleAssociationValuesFoundException(value: rawValue, allResults: allResults)
        } else {
            return allResults.first()
        }
    }

    private boolean isAssociatedAttribute(MetaPropertyPath path) {
        path.metaProperties.size() > 1
    }

    private getValue(MetaProperty metaProperty, String rawValue, DataRow dataRow, ImportConfiguration importConfiguration) {
        def value = null


        if (metaProperty.type == MetaProperty.Type.ENUM) {
            value = handleEnumValues(metaProperty, rawValue)

        } else if (metaProperty.type == MetaProperty.Type.DATATYPE) {
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
            case Boolean: return getBooleanValue(importConfiguration, rawValue, dataRow)
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

    @SuppressWarnings('BooleanMethodReturnsNull')
    private Boolean getBooleanValue(ImportConfiguration importConfiguration, String rawValue, DataRow dataRow) {

        def customBooleanTrueValue = importConfiguration.booleanTrueValue
        def customBooleanFalseValue = importConfiguration.booleanFalseValue

        if (customBooleanTrueValue || customBooleanFalseValue) {
            if (customBooleanTrueValue.equalsIgnoreCase(rawValue)) {
                return true
            }

            return customBooleanFalseValue.equalsIgnoreCase(rawValue) ? false : null
        }


        try {
            return Boolean.parseBoolean(rawValue)
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