package de.diedavids.cuba.dataimport.converter

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import org.apache.commons.lang.StringUtils
import org.springframework.stereotype.Component

@Component('ddcdi_MetaPropertyMatcher')
class MetaPropertyMatcher {

    private static final SEPARATOR = '.'


    String findEntityAttributeForColumn(String column, MetaClass selectedEntity) {

        //MetaProperty possibleProperty = findPropertyByColumn(selectedEntity, column)

        String result = findPropertyByColumn(selectedEntity, column) //''
        /*if (possibleProperty && isSimpleDatatype(possibleProperty)) {
            result = possibleProperty.name
        }*/

        result
    }

    List<String> listProperties(List<String> s, String prev, MetaClass selectedEntity) {
        if (!selectedEntity?.properties || s == null) {
            return []
        }

        selectedEntity.properties.each {
            def tempPrev = appendPathSeparator(prev ?: '')
            def attribute = tempPrev.concat(it.name)

            if (isSimpleDatatype(it) && !s.contains(attribute)) {
                s.add(attribute)
            } else if (!tempPrev.contains(it.name) && it.range) {
                def nextEntityName = it.range.asClass()
                if (selectedEntity != nextEntityName) {
                    listProperties(s, attribute, nextEntityName)
                }
            }
        }
        s
    }

    private String appendPathSeparator(String s) {
        def result = s
        if (result && result.size() > 1 && !result.endsWith(SEPARATOR)) {
            result += SEPARATOR
        }
        result
    }

    private boolean isSimpleDatatype(MetaProperty possibleProperty) {
        possibleProperty.type != MetaProperty.Type.ASSOCIATION && possibleProperty.type != MetaProperty.Type.COMPOSITION
    }

    private String findPropertyByColumn(MetaClass selectedEntity, String column) {

        if (selectedEntity == null) {
            return null
        }

        def propertiesNames = listProperties([], '', selectedEntity)
        //selectedEntity.properties*.name

        String match = null

        def directMatch = searchForDirectMatch(propertiesNames, column)

        if (directMatch) {
            match = directMatch
        } else {
            match = findNearestMatchIfPossible(propertiesNames, column)
        }

        //match ? selectedEntity.getProperty(match) : null
        match ?: null
    }

    private String findNearestMatchIfPossible(List<String> propertiesNames, String column) {
        def distances = calculateLevensteinDistances(propertiesNames, column)
        def mostLikelyMatch = distances.min { it.value }

        if (mostLikelyMatch?.value < 5) {
            return mostLikelyMatch.key
        }
    }

    private String searchForDirectMatch(List<String> propertiesNames, column) {
        propertiesNames.find {
            def lowercasePropertyName = it?.toLowerCase()
            column.toLowerCase().startsWith(lowercasePropertyName) ||
                    column.toLowerCase().endsWith(lowercasePropertyName)
        }
    }

    private Map<String, Integer> calculateLevensteinDistances(List<String> propertiesNames, String column) {
        propertiesNames.collectEntries {
            [(it): StringUtils.getLevenshteinDistance(it, column)]
        }
    }
}