package de.diedavids.cuba.dataimport.converter

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import org.springframework.stereotype.Component

import javax.inject.Inject

@Component('ddcdi_ImportAttributeMapperCreator')
class ImportAttributeMapperCreator {

    @Inject
    MetaPropertyMatcher metaPropertyMatcher

    List<ImportAttributeMapper> createMappers(ImportData importData, MetaClass selectedEntity) {
        importData.columns.withIndex().collect { String column, Integer index ->
            createMapper(index, column, selectedEntity)
        }
    }

    ImportAttributeMapper createMapper(Integer fileColumnNumber, String column, MetaClass selectedEntity) {

        MetaProperty columnMetaProperty = metaPropertyMatcher.findPropertyByColumn(selectedEntity, column)
        def attrType = metaPropertyMatcher.findAttributeTypeForColumn(selectedEntity, column)

        def result = new ImportAttributeMapper(
                entityAttribute: columnMetaProperty?.name,
                attributeType: attrType,
                fileColumnAlias: column,
                fileColumnNumber: fileColumnNumber,
        )
        result
    }

}
