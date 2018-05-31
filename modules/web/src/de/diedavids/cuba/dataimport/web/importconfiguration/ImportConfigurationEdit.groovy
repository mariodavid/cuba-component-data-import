package de.diedavids.cuba.dataimport.web.importconfiguration

import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.WindowManager
import com.haulmont.cuba.gui.components.AbstractEditor
import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.data.CollectionDatasource
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeMapperMode
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper

import javax.inject.Inject

class ImportConfigurationEdit extends AbstractEditor<ImportConfiguration> {

    @Inject
    CollectionDatasource<ImportAttributeMapper, UUID> importAttributeMappersDs

    @Inject
    Table<ImportAttributeMapper> importAttributeMappersTable

    @Inject
    Metadata metadata


    void newCustomAttributeMapper() {
        openAttributeMapperEditor(createCustomAttributeMapperInstance())
    }


    void newAutomaticAttributeMapper() {
        openAttributeMapperEditor(createAutomaticAttributeMapperInstance())
    }



    private AbstractEditor openAttributeMapperEditor(ImportAttributeMapper importAttributeMapper) {
        openEditor(importAttributeMapper, WindowManager.OpenType.DIALOG, [:], importAttributeMappersDs)
    }

    private ImportAttributeMapper createCustomAttributeMapperInstance() {
        def importAttributeMapper = metadata.create(ImportAttributeMapper)
        importAttributeMapper.mapperMode = AttributeMapperMode.CUSTOM
        importAttributeMapper.configuration = item
        importAttributeMapper.attributeType = null
        importAttributeMapper
    }

    private ImportAttributeMapper createAutomaticAttributeMapperInstance() {
        def importAttributeMapper = metadata.create(ImportAttributeMapper)
        importAttributeMapper.configuration = item
        importAttributeMapper
    }

}