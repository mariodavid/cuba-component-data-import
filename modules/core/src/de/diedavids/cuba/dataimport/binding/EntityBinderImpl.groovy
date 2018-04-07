package de.diedavids.cuba.dataimport.binding

import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.Metadata
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
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


    @Override
    Entity bindAttributes(ImportConfiguration importConfiguration, DataRow dataRow, Entity entity) {

        importConfiguration.importAttributeMappers.each { ImportAttributeMapper importAttributeMapper ->
            bindAttribute(importConfiguration, dataRow, entity, importAttributeMapper)
        }

        entity
    }

    private void bindAttribute(ImportConfiguration importConfiguration, DataRow dataRow, Entity entity, ImportAttributeMapper importAttributeMapper) {

        AttributeBindRequest bindRequest = createAttributeBindRequest(importConfiguration, dataRow, importAttributeMapper)
        AttributeBinder binder = attributeBinderFactory.createAttributeBinderFromBindingRequest(bindRequest)

        binder.bindAttribute(entity, bindRequest)

    }

    AttributeBindRequest createAttributeBindRequest(ImportConfiguration importConfiguration, DataRow dataRow, ImportAttributeMapper importAttributeMapper) {
        new AttributeBindRequest(
                importConfiguration: importConfiguration,
                importAttributeMapper: importAttributeMapper,
                dataRow: dataRow,
                metadata: metadata
        )
    }

}