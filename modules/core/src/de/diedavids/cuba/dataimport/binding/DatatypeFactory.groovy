package de.diedavids.cuba.dataimport.binding


import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Slf4j
@Component('ddcdi_DatatypeFactory')
class DatatypeFactory {

    public static final String NAME = 'ddcdi_DatatypeFactory'

    Object getValue(AttributeBindRequest bindRequest) {
        switch (bindRequest.javaType) {
            case Integer: return getIntegerValue(bindRequest.rawValue, bindRequest.dataRow)
            case Long: return getLongValue(bindRequest.rawValue, bindRequest.dataRow)
            case Double: return getDoubleValue(bindRequest.rawValue, bindRequest.dataRow)
            case Date: return getDateValue(bindRequest.importConfiguration, bindRequest.rawValue)
            case LocalDate: return getLocalDateValue(bindRequest.importConfiguration, bindRequest.rawValue)
            case Boolean: return getBooleanValue(bindRequest.importConfiguration, bindRequest.rawValue, bindRequest.dataRow)
            case BigDecimal: return getBigDecimalValue(bindRequest.rawValue, bindRequest.dataRow)
            case String: return getStringValue(bindRequest.rawValue)
            case Enum: return getEnumValue(bindRequest)
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

    private Long getLongValue(String rawValue, DataRow dataRow) {
        try {
            return Long.parseLong(rawValue)
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
        catch (Exception e) {
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

    @SuppressWarnings('SimpleDateFormatMissingLocale')
    private LocalDate getLocalDateValue(ImportConfiguration importConfiguration, String rawValue) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(importConfiguration.dateFormat);
        //convert String to LocalDate
        LocalDate localDate = LocalDate.parse(rawValue, formatter);
        return localDate
    }

    private Double getDoubleValue(String rawValue, DataRow dataRow) {
        try {
            return Double.parseDouble(rawValue)
        }
        catch (NumberFormatException e) {
            log.warn("Number could not be read: '$rawValue' in [$dataRow]. Will be ignored.")
        }
    }

    private BigDecimal getBigDecimalValue(String rawValue, DataRow dataRow) {
        try {
            return new BigDecimal(rawValue)
        }
        catch (NumberFormatException e) {
            log.warn("Number could not be read: '$rawValue' in [$dataRow]. Will be ignored.")
        }
    }

    private getEnumValue(AttributeBindRequest bindRequest) {
        Class<Enum> enumType = bindRequest.javaType as Class<Enum>
        def value = bindRequest.rawValue.toUpperCase()
        if (enumType.isEnum() && value) {
            try {
                enumType.valueOf(enumType, value)
            }
            catch (IllegalArgumentException e) {
                log.info("Enum value could not be found: $value for Enum: ${enumType.simpleName}. Will be ignored")
                log.debug('Details: ', e)
            }
        }
    }

}
