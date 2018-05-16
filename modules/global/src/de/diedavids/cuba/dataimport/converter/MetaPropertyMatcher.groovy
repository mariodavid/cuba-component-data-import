package de.diedavids.cuba.dataimport.converter

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import de.diedavids.cuba.dataimport.entity.AttributeType
import org.apache.commons.lang.StringUtils
import org.springframework.stereotype.Component

@Component('ddcdi_MetaPropertyMatcher')
class MetaPropertyMatcher {


    String findEntityAttributeForColumn(String column, MetaClass selectedEntity) {

        MetaProperty possibleProperty = findPropertyByColumn(selectedEntity, column)

        String result = ''
        if (possibleProperty && isSimpleDatatype(possibleProperty)) {
                result = possibleProperty.name
        }
        result
    }

    private boolean isSimpleDatatype(MetaProperty possibleProperty) {
        possibleProperty.type != MetaProperty.Type.ASSOCIATION && possibleProperty.type != MetaProperty.Type.COMPOSITION
    }

    private MetaProperty findPropertyByColumn(MetaClass selectedEntity, String column) {

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
            column.toLowerCase().startsWith(it) ||
                    column.toLowerCase().endsWith(it)
        }
    }

    private Map<String, Integer> calculateLevensteinDistances(List<String> propertiesNames, String column) {
        propertiesNames.collectEntries {
            [(it): StringUtils.getLevenshteinDistance(it, column)]
        }
    }

    AttributeType findAttributeTypeForColumn(String column, MetaClass selectedEntity) {
        def metaProperty = findPropertyByColumn(selectedEntity, column)

        switch(metaProperty?.type) {
           case MetaProperty.Type.ASSOCIATION: return AttributeType.ASSOCIATION_ATTRIBUTE
           case MetaProperty.Type.DATATYPE: return AttributeType.DIRECT_ATTRIBUTE
           case MetaProperty.Type.ENUM: return AttributeType.DIRECT_ATTRIBUTE
           default: return null
        }
    }
}