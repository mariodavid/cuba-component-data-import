package de.diedavids.cuba.dataimport.converter

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.cuba.core.global.Metadata
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import org.springframework.stereotype.Component

import javax.inject.Inject

@Component('ddcdi_ImportAttributeMapperCreator')
class ImportAttributeMapperCreator {

    @Inject
    MetaPropertyMatcher metaPropertyMatcher

    @Inject
    Metadata metadata

    List<ImportAttributeMapper> createMappers(ImportData importData, MetaClass selectedEntity) {
        importData.columns.withIndex().collect { String column, Integer index ->
            createMapper(index, column, selectedEntity)
        }
    }

    ImportAttributeMapper createMapper(Integer fileColumnNumber, String column, MetaClass selectedEntity) {

        MetaProperty columnMetaProperty = metaPropertyMatcher.findPropertyByColumn(selectedEntity, column)
        def attrType = metaPropertyMatcher.findAttributeTypeForColumn(selectedEntity, column)

        createMapperInstance(columnMetaProperty, attrType, column, fileColumnNumber)

    }

    private ImportAttributeMapper createMapperInstance(MetaProperty columnMetaProperty, AttributeType attrType, String column, int fileColumnNumber) {
        def result = metadata.create(ImportAttributeMapper)
        result.entityAttribute = columnMetaProperty?.name
        result.attributeType = attrType
        result.fileColumnAlias = column
        result.fileColumnNumber = fileColumnNumber
        result
    }

}
