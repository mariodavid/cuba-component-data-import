package de.diedavids.cuba.dataimport.web.util

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import org.apache.commons.lang.StringUtils
import org.springframework.stereotype.Component

@Component
class MetaPropertyMatcher {


    String findEntityAttributeForColumn(String column, MetaClass selectedEntity) {

        MetaProperty possibleProperty = findPropertyByColumn(selectedEntity, column)

        possibleProperty?.toString() ?: ''

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
}