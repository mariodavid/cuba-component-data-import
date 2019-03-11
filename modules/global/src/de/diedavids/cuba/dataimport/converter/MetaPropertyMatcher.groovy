package de.diedavids.cuba.dataimport.converter

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

@Component('ddcdi_MetaPropertyMatcher')
class MetaPropertyMatcher {


    MetaProperty findPropertyByColumn(MetaClass selectedEntity, String column) {

        def propertiesNames = selectedEntity.properties*.name

        String match = null

        def directMatch = searchForDirectMatch(propertiesNames, column)

        if (directMatch) {
            match = directMatch
        } else {
            match = findNearestMatchIfPossible(propertiesNames, column)
        }

        match ? selectedEntity.getProperty(match) : null
    }

    private String findNearestMatchIfPossible(List<String> propertiesNames, String column) {
        def distances = calculateLevensteinDistances(propertiesNames, column)
        def mostLikelyMatch = distances.min { it.value }

        if (mostLikelyMatch.value < 5) {
            return mostLikelyMatch.key
        }
    }

    private String searchForDirectMatch(List<String> propertiesNames, column) {
        propertiesNames.find {
            def value = it.toLowerCase()
            column.toLowerCase().startsWith(value) ||
                    column.toLowerCase().endsWith(value)
        }
    }

    private Map<String, Integer> calculateLevensteinDistances(List<String> propertiesNames, String column) {
        propertiesNames.collectEntries {
            [(it): StringUtils.getLevenshteinDistance(it, column)]
        }
    }

    AttributeType findAttributeTypeForColumn(MetaClass selectedEntity, String column) {
        def metaProperty = findPropertyByColumn(selectedEntity, column)

        switch (metaProperty?.type) {
            case MetaProperty.Type.ASSOCIATION: return AttributeType.ASSOCIATION_ATTRIBUTE
            case MetaProperty.Type.DATATYPE: return AttributeType.DIRECT_ATTRIBUTE
            case MetaProperty.Type.ENUM: return AttributeType.DIRECT_ATTRIBUTE
            default: return null
        }
    }
}