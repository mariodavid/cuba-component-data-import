package de.diedavids.cuba.dataimport.binding

import com.haulmont.cuba.core.entity.Entity
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.service.MultipleAssociationValuesFoundException
import groovy.util.logging.Slf4j


@Slf4j
class AssociationAttributeBinder implements AttributeBinder {

    SimpleDataLoader simpleDataLoader

    @Override
    void bindAttribute(Entity entity, AttributeBindRequest bindRequest) {
        try {
            def value = handleAssociationAttribute(bindRequest, entity)
            if (value) {
                entity.setValueEx(bindRequest.entityAttributePath, value)
            }
        }
        catch (MultipleAssociationValuesFoundException e) {
            e.dataRow = bindRequest.dataRow
            e.propertyPath = bindRequest.importEntityPropertyPath
            log.warn("Multiple associations found for data row: [${e.dataRow}] and attribute: ${e.propertyPath} with value ${e.value}. Found associations: ${e.allResults}. Will be ignored.")
        }
    }

    private void handleAssociationAttribute(AttributeBindRequest bindRequest, Entity entity) {
        def propertyPathFromAssociation = bindRequest.importEntityPropertyPath.path.drop(1)
        def propertyPath = propertyPathFromAssociation.join('.')
        def associationJavaType = bindRequest.importEntityPropertyPath.metaProperties[0].javaType
        def associationProperty = bindRequest.importEntityPropertyPath.metaProperties[0].name

        def associationValue = loadAssociationValue(bindRequest, associationJavaType, propertyPath, bindRequest.rawValue)
        entity.setValueEx(associationProperty, associationValue)
    }

    private loadAssociationValue(AttributeBindRequest bindRequest, Class<?> associationJavaType, String propertyPath, String rawValue) {
        def allResults = simpleDataLoader.loadAllByProperty(associationJavaType, propertyPath, rawValue)

        if (allResults.size() > 1) {
            throw new MultipleAssociationValuesFoundException(value: rawValue, allResults: allResults)
        } else {
            if (allResults.empty) {
                log.warn("No associations found for data row: [${bindRequest.dataRow}] and attribute: [${propertyPath}] with value [${rawValue}]. Will be ignored.")
            }
            else {
                return allResults.first()
            }
        }
    }

}
