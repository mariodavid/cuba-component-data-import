package de.diedavids.cuba.dataimport.dto

import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper

class ImportDataImpl implements ImportData {
    List<DataRow> rows = []
    List<String> columns = []

    @Override
    ColumnValidationResult isCompatibleWith(List<ImportAttributeMapper> attributeMappers) {
        List<String> invalidColumns = attributeMappers
                .findAll { attributeMapper -> validateColumn(attributeMapper)}
                .collect { it.fileColumnAlias }

        new ColumnValidationResult(invalidColumns.isEmpty(), invalidColumns)
    }

    private boolean validateColumn(ImportAttributeMapper attributeMapper) {
        attributeMapper.isRequiredColumn && !columns.contains(attributeMapper.fileColumnAlias)
    }
}