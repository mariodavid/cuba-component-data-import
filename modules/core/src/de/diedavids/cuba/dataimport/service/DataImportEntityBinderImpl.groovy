package de.diedavids.cuba.dataimport.service

import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.Metadata
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import javax.inject.Inject
import java.text.SimpleDateFormat

@Slf4j
@Component(DataImportEntityBinder.NAME)
class DataImportEntityBinderImpl implements DataImportEntityBinder {

    @Inject
    Metadata metadata


    @Override
    Entity bindAttributes(ImportConfiguration importConfiguration, DataRow dataRow, Entity entity) {

        importConfiguration.importAttributeMappers.each { ImportAttributeMapper importAttributeMapper ->
            bindAttribute(importConfiguration, dataRow, entity, importAttributeMapper)
        }

        entity
    }

    private void bindAttribute(ImportConfiguration importConfiguration, DataRow dataRow, Entity entity, ImportAttributeMapper importAttributeMapper) {

        def importEntityClassName = importConfiguration.entityClass

        def entityAttribute = importAttributeMapper.entityAttribute - (importEntityClassName + '.')

        MetaProperty metaProperty = metadata.getClass(importEntityClassName).getPropertyNN(entityAttribute)
        String rawValue = ((String) dataRow[importAttributeMapper.fileColumnAlias]).trim()

        def value = getValue(metaProperty, rawValue, dataRow, importConfiguration)


        entity.setValueEx(entityAttribute, value)
    }

    private getValue(MetaProperty metaProperty, String rawValue, DataRow dataRow, ImportConfiguration importConfiguration) {
        def value = null


        if (metaProperty.type == MetaProperty.Type.ENUM) {
            value = metaProperty.javaType.fromId(rawValue)

        }
        else if (metaProperty.type == MetaProperty.Type.DATATYPE) {
            if (metaProperty.javaType == Integer) {
                value = getIntegerValue(rawValue, dataRow)
            }

            if (metaProperty.javaType == Double) {
                value = getDoubleValue(rawValue, dataRow)

            }

            if (metaProperty.javaType == Date) {
                value = getDateValue(importConfiguration, rawValue)
            }

            if (metaProperty.javaType == String) {
                value = getStringValue(rawValue)
            }
        }


        value
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