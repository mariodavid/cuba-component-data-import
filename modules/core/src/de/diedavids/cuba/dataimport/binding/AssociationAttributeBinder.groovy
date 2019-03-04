package de.diedavids.cuba.dataimport.binding

import com.haulmont.cuba.core.entity.Entity
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.service.MultipleAssociationValuesFoundException
import groovy.util.logging.Slf4j


@Slf4j
class AssociationAttributeBinder implements AttributeBinder {

    SimpleDataLoader simpleDataLoader

    DatatypeFactory datatypeFactory

    @Override
    void bindAttribute(Entity entity, AttributeBindRequest bindRequest) {

        try {
            handleAssociationAttribute(bindRequest, entity)
        }
        catch (MultipleAssociationValuesFoundException e) {
            e.dataRow = bindRequest.dataRow
            e.propertyPath = bindRequest.importEntityPropertyPath
            log.warn("Multiple associations found for data row: [${e.dataRow}] and attribute: ${e.propertyPath} with value ${e.value}. Found associations: ${e.allResults}. Will be ignored.")
        }
    }

    private void handleAssociationAttribute(AttributeBindRequest bindRequest, Entity entity) {

        if (bindRequest.validAssociationBindingRequest) {
            def propertyPathFromAssociation = bindRequest.importEntityPropertyPath.path.drop(1)
            def propertyPath = propertyPathFromAssociation.join('.')
            def associationJavaType = bindRequest.importEntityPropertyPath.metaProperties[0].range.asClass().javaClass as Class<? extends Entity>

            def associationProperty = bindRequest.importEntityPropertyPath.metaProperties[0].name
            def associationValue = loadAssociationValue(bindRequest, associationJavaType, propertyPath, datatypeFactory.getValue(bindRequest))

            try {
                entity.setValueEx(associationProperty, associationValue)
            }
            catch (IllegalArgumentException e) {
                log.warn("Association bind request could not be executed. Trying to set $associationValue to $associationProperty. Will be ignored.")
                log.debug('Detailed exception', e)
            }
        }
        else {
            log.warn('Invalid Association bind request. Will be ignored.')
        }
    }

    private loadAssociationValue(AttributeBindRequest bindRequest, Class<? extends Entity> associationJavaType, String propertyPath, Object rawValue) {
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
