package de.diedavids.cuba.dataimport.web.importconfiguration

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.app.FileStorageService
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.components.AbstractEditor
import com.haulmont.cuba.gui.components.FieldGroup
import com.haulmont.cuba.gui.components.LookupField
import com.haulmont.cuba.gui.data.CollectionDatasource
import com.haulmont.cuba.gui.data.Datasource
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import de.diedavids.cuba.dataimport.converter.DataConverterFactory
import de.diedavids.cuba.dataimport.converter.ImportAttributeMapperCreator
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.web.importfile.ImportFileParser
import de.diedavids.cuba.dataimport.web.util.EntityClassSelector
import org.apache.commons.io.FileUtils

import javax.inject.Inject

class ImportConfigurationCreate extends AbstractEditor<ImportConfiguration> {


    private static final String ENTITY_CLASS_FIELD_NAME = 'entityClass'

    @Inject
    private Datasource<ImportConfiguration> importConfigurationDs

    @Inject
    private CollectionDatasource<ImportAttributeMapper, UUID> importAttributeMappersDs

    @Inject
    private FieldGroup fieldGroup

    @Inject
    private ComponentsFactory componentsFactory

    @Inject
    EntityClassSelector entityClassSelector

    @Inject
    ImportAttributeMapperCreator importAttributeMapperCreator

    @Inject
    Metadata metadata

    @Inject
    DataConverterFactory dataConverterFactory

    @Inject
    FileStorageService fileStorageService


    ImportFileParser importFileParser

    @Override
    void init(Map<String, Object> params) {
        initEntityClassField()
        initImportFileParser()
    }

    void initImportFileParser() {
        importFileParser = new ImportFileParser(
                dataConverterFactory: dataConverterFactory
        )
    }


    private void initEntityClassField() {
        FieldGroup.FieldConfig entityClassFieldConfig = fieldGroup.getField(ENTITY_CLASS_FIELD_NAME)

        LookupField lookupField = componentsFactory.createComponent(LookupField)

        lookupField.setDatasource(importConfigurationDs, ENTITY_CLASS_FIELD_NAME)
        lookupField.setOptionsMap(entityClassSelector.entitiesLookupFieldOptions)


        entityClassFieldConfig.setComponent(lookupField)
    }

    @Override
    protected boolean preCommit() {
        parseAttributeMappersForConfiguration()
    }

    private boolean parseAttributeMappersForConfiguration() {
        List<ImportAttributeMapper> mappers = createAttributeMappers(importData)
        if (mappers) {
            assignMappersToConfiguration(mappers)
            return true
        }
    }

    private void assignMappersToConfiguration(List<ImportAttributeMapper> mappers) {
        mappers.each {
            it.configuration = item
            importAttributeMappersDs.addItem(it)
        }
        importConfigurationDs.item.importAttributeMappers = mappers
    }

    private List<ImportAttributeMapper> createAttributeMappers(ImportData importData) {
        MetaClass selectedEntity = metadata.getClass(item.entityClass)
        importAttributeMapperCreator.createMappers(importData, selectedEntity)
    }

    private ImportData getImportData() {
        def fileBytes = fileStorageService.loadFile(item.template)
        def tmpFile = File.createTempFile("importfile-${item.name}", null)
        FileUtils.writeByteArrayToFile(tmpFile, fileBytes)

        importFileParser.parseFile(item.template, tmpFile)
    }
}