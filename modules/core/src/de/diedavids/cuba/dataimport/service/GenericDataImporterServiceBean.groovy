package de.diedavids.cuba.dataimport.service

import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.chile.core.model.MetaPropertyPath
import com.haulmont.chile.core.model.Range
import com.haulmont.cuba.core.app.importexport.EntityImportExportAPI
import com.haulmont.cuba.core.app.importexport.EntityImportView
import com.haulmont.cuba.core.app.importexport.ReferenceImportBehaviour
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.validation.EntityValidationException
import de.diedavids.cuba.dataimport.binding.EntityBinder
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportLog
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import javax.inject.Inject

@Slf4j
@Service(GenericDataImporterService.NAME)
class GenericDataImporterServiceBean implements GenericDataImporterService {

    @Inject
    Metadata metadata

    @Inject
    EntityImportExportAPI entityImportExportAPI

    @Inject
    EntityBinder dataImportEntityBinder

    @Override
    boolean doDataImport(ImportConfiguration importConfiguration, ImportData importData) {

        def entities = createEntities(importConfiguration, importData)

        def importEntityMetaClass = metadata.getClass(importConfiguration.entityClass)
        def importEntityClass = importEntityMetaClass.javaClass

        EntityImportView importView = creteEntityImportView(importEntityClass, importConfiguration)

        try {
            entityImportExportAPI.importEntities(entities, importView, true)
            return true
        }
        catch (EntityValidationException e) {
            log.error("Import failed due to validation errors of one or more entities", e)
            return false
        }
    }

    private EntityImportView creteEntityImportView(Class importEntityClass, ImportConfiguration importConfiguration) {
        EntityImportView importView = new EntityImportView(importEntityClass)
                .addLocalProperties()
        addAssociationPropertiesToImportView(importConfiguration, importView)

        importView
    }

    private void addAssociationPropertiesToImportView(ImportConfiguration importConfiguration, importView) {
        importConfiguration.importAttributeMappers.each { ImportAttributeMapper importAttributeMapper ->
            def importEntityClassName = importConfiguration.entityClass

            def entityAttribute = importAttributeMapper.entityAttribute - (importEntityClassName + '.')
            MetaPropertyPath path = metadata.getClass(importEntityClassName).getPropertyPath(entityAttribute)

            if (isAssociatedAttribute(path)) {
                def associationMetaProperty = path.metaProperties[0]
                def associationMetaPropertyName = associationMetaProperty.name
                if (associationMetaProperty.type == MetaProperty.Type.ASSOCIATION && associationMetaProperty.range.cardinality == Range.Cardinality.MANY_TO_ONE) {
                    importView.addManyToOneProperty(associationMetaPropertyName, ReferenceImportBehaviour.IGNORE_MISSING)
                }
            }
        }
    }

    private boolean isAssociatedAttribute(MetaPropertyPath path) {
        path.metaProperties.size() > 1
    }


    Collection<Entity> createEntities(ImportConfiguration importConfiguration, ImportData importData) {
        importData.rows.collect {
            createEntityFromRow(importConfiguration, it)
        }
    }

    Entity createEntityFromRow(ImportConfiguration importConfiguration, DataRow dataRow) {
        Entity entityInstance = createEntityInstance(importConfiguration)

        bindAttributes(importConfiguration, dataRow, entityInstance)
    }

    Entity bindAttributes(ImportConfiguration importConfiguration, DataRow dataRow, Entity entity) {
        dataImportEntityBinder.bindAttributes(importConfiguration, dataRow, entity)
    }

    private Entity createEntityInstance(ImportConfiguration importConfiguration) {
        metadata.create(importConfiguration.entityClass)
    }
}