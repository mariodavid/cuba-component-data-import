package de.diedavids.cuba.dataimport.converter

import com.haulmont.chile.core.model.MetaClass
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import org.springframework.stereotype.Component

import javax.inject.Inject

@Component('ddcdi_ImportAttributeMapperCreator')
class ImportAttributeMapperCreator {

    @Inject
    MetaPropertyMatcher metaPropertyMatcher

    List<ImportAttributeMapper> createMappers(ImportData importData, MetaClass selectedEntity) {
        importData.columns.withIndex().collect { String column, index ->
                metaPropertyMatcher.findEntityAttributeForColumn(index, column, selectedEntity)
        }
    }


}
