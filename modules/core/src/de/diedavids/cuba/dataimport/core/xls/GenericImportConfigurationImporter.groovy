package de.diedavids.cuba.dataimport.core.xls

import com.haulmont.cuba.core.app.importexport.EntityImportExportAPI
import com.haulmont.cuba.core.app.importexport.EntityImportView
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.Metadata
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportLog
import org.springframework.stereotype.Component

import javax.inject.Inject

@Component
class GenericImportConfigurationImporter {

    @Inject
    Metadata metadata

    @Inject
    EntityImportExportAPI entityImportExportAPI

    ImportLog doDataImport(ImportConfiguration importConfiguration, ImportData importData) {

        def entities = createEntities(importConfiguration, importData)

        def importEntityClass = metadata.getClass(importConfiguration.entityClass).javaClass

        EntityImportView importView = new EntityImportView(importEntityClass)
                .addLocalProperties()

        entityImportExportAPI.importEntities(entities, importView, true)

        null
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

        importConfiguration.importAttributeMappers.each { ImportAttributeMapper importAttributeMapper ->
            def entityAttribute = importAttributeMapper.entityAttribute - (importConfiguration.entityClass + '.')
            entity.setValueEx(entityAttribute, dataRow[importAttributeMapper.fileColumnAlias])
        }

        entity
    }

    private Entity createEntityInstance(ImportConfiguration importConfiguration) {
        metadata.create(importConfiguration.entityClass)
    }
}
