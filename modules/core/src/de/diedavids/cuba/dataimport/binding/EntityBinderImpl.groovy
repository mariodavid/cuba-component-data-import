package de.diedavids.cuba.dataimport.binding

import com.haulmont.cuba.core.app.dynamicattributes.DynamicAttributes
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.Metadata
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import javax.inject.Inject

@Slf4j
@Component(EntityBinder.NAME)
class EntityBinderImpl implements EntityBinder {

    @Inject
    Metadata metadata

    @Inject
    AttributeBinderFactory attributeBinderFactory

    @Inject
    DynamicAttributes dynamicAttributes


    @Override
    Entity bindAttributesToEntity(ImportConfiguration importConfiguration, DataRow dataRow, Entity entity, Map<String, Object> defaultValues = [:]) {

        defaultValues.each { k, v ->
            entity.setValue(k,v)
        }

        importConfiguration.importAttributeMappers.each { ImportAttributeMapper importAttributeMapper ->
            tryToBindAttribute(importConfiguration, dataRow, entity, importAttributeMapper)
        }

        entity
    }

    private void tryToBindAttribute(ImportConfiguration importConfiguration, DataRow dataRow, Entity entity, ImportAttributeMapper importAttributeMapper) {

        if (importAttributeMapper.isBindable()) {
            AttributeBindRequest bindRequest = createAttributeBindRequest(importConfiguration, dataRow, importAttributeMapper)
            AttributeBinder binder = attributeBinderFactory.createAttributeBinderFromBindingRequest(bindRequest)
            binder?.bindAttribute(entity, bindRequest)
        } else {
            log.warn("Import Attribute Mapper is not bindable: [$importAttributeMapper.fileColumnNumber, $importAttributeMapper.fileColumnAlias]. Will be ignored.")
        }

    }

    AttributeBindRequest createAttributeBindRequest(ImportConfiguration importConfiguration, DataRow dataRow, ImportAttributeMapper importAttributeMapper) {

        new AttributeBindRequest(
                importConfiguration: importConfiguration,
                importAttributeMapper: importAttributeMapper,
                dataRow: dataRow,
                metadata: metadata,
                dynamicAttributes: dynamicAttributes
        )
    }

}