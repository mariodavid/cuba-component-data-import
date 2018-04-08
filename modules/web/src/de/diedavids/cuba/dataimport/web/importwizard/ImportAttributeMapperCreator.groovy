package de.diedavids.cuba.dataimport.web.importwizard

import com.haulmont.chile.core.model.MetaClass
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.web.util.MetaPropertyMatcher
import org.springframework.stereotype.Component

import javax.inject.Inject

@Component
class ImportAttributeMapperCreator {

    @Inject
    MetaPropertyMatcher metaPropertyMatcher

    List<ImportAttributeMapper> createMappers(ImportData importData, MetaClass selectedEntity) {
        importData.columns.withIndex().collect { String column, index ->
            new ImportAttributeMapper(
                    entityAttribute: metaPropertyMatcher.findEntityAttributeForColumn(column, selectedEntity),
                    fileColumnAlias: column,
                    fileColumnNumber: index,
            )
        }
    }
}
