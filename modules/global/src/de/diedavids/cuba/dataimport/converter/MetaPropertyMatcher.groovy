package de.diedavids.cuba.dataimport.converter

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import org.apache.commons.lang.StringUtils
import org.springframework.stereotype.Component

@Component('ddcdi_MetaPropertyMatcher')
class MetaPropertyMatcher {


    String findEntityAttributeForColumn(String column, MetaClass selectedEntity) {

        //MetaProperty possibleProperty = findPropertyByColumn(selectedEntity, column)

        String result = findPropertyByColumn(selectedEntity, column) //''
        /*if (possibleProperty && isSimpleDatatype(possibleProperty)) {
            result = possibleProperty.name
        }*/

        result
    }

    List<String> listProperties(List<String> s, String prev, MetaClass selectedEntity) {
        if (selectedEntity && s!=null && selectedEntity.getProperties() ) {
            selectedEntity.getProperties().each {
                if (prev == null || prev.isEmpty())
                    prev = ""
                else if (!prev.endsWith("."))
                    prev += "."

                if (isSimpleDatatype(it)) {
                    def val = prev.concat(it.name)
                    if(!s.contains(val))
                        s.add(val)
                } else {
                    def nn = prev.concat(it.name)
                    if (!prev.contains(it.name)){
                        def nextEntityName = it.getRange().asClass();
                        if(selectedEntity!=nextEntityName) {
                            listProperties(s, nn, nextEntityName)
                        }
                    }


                }
            }
            return s;
        }
        return null
    }

    private boolean isSimpleDatatype(MetaProperty possibleProperty) {
        possibleProperty.type != MetaProperty.Type.ASSOCIATION && possibleProperty.type != MetaProperty.Type.COMPOSITION
    }

    private String findPropertyByColumn(MetaClass selectedEntity, String column) {

        if (selectedEntity == null) {
            return null
        }

        def propertiesNames =  listProperties(new ArrayList<String>(), "", selectedEntity) //selectedEntity.properties*.name

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

        if (mostLikelyMatch && mostLikelyMatch.value < 5) {
            return mostLikelyMatch.key
        }
    }

    private String searchForDirectMatch(List<String> propertiesNames, column) {
        propertiesNames.find {
            if (it != null) {
                it = it.toLowerCase();
            }
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