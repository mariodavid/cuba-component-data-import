package de.diedavids.cuba.dataimport.converter

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import de.diedavids.cuba.dataimport.entity.AttributeType
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import org.apache.commons.lang.StringUtils
import org.springframework.stereotype.Component

@Component('ddcdi_MetaPropertyMatcher')
class MetaPropertyMatcher {


    ImportAttributeMapper findEntityAttributeForColumn(Integer index, String column, MetaClass selectedEntity) {

        def inputColumnList = getAttributePath(column)
        MetaProperty possibleProperty = findPropertyByColumn(selectedEntity, inputColumnList[(0)])
        def attrType = findAttributeTypeForColumn(inputColumnList[(0)], selectedEntity)
        def possiblePropertyName =  (attrType) ? possibleProperty?.name : ''

        def result = new ImportAttributeMapper(
                entityAttribute: possiblePropertyName,
                attributeType: attrType,
                fileColumnAlias: column,
                fileColumnNumber: index,
        )
        if (possibleProperty && !isSimpleDatatype(possibleProperty)) {
            MetaClass assocAttribute = possibleProperty.range?.asClass()
            if (possiblePropertyName && assocAttribute) {
                result.associationLookupAttribute = findPropertyByColumn(assocAttribute, inputColumnList[(1)])?.name
            }
        }
        result
    }

    String[] getAttributePath(String column) {
        if (!column) {
            return ['']
        }
        def inputColumnWithDot = column.tokenize('.')
        inputColumnWithDot
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

    AttributeType findAttributeTypeForColumn(String column, MetaClass selectedEntity) {
        def metaProperty = findPropertyByColumn(selectedEntity, column)

        switch (metaProperty?.type) {
            case MetaProperty.Type.ASSOCIATION: return AttributeType.ASSOCIATION_ATTRIBUTE
            case MetaProperty.Type.DATATYPE: return AttributeType.DIRECT_ATTRIBUTE
            case MetaProperty.Type.ENUM: return AttributeType.DIRECT_ATTRIBUTE
            default: return null
        }
    }
}