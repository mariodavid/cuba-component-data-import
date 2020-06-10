package de.diedavids.cuba.dataimport.dto

import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper

class ImportDataImpl implements ImportData {
    List<DataRow> rows = []
    List<String> columns = []

    @Override
    ColumnValidationResult isCompatibleWith(List<ImportAttributeMapper> attributeMappers) {
        List<String> invalidColumns = attributeMappers
                .findAll { attributeMapper -> isInvalidColumn(attributeMapper) }
                *.fileColumnAlias

        new ColumnValidationResult(invalidColumns.isEmpty(), invalidColumns)
    }

    private boolean isInvalidColumn(ImportAttributeMapper attributeMapper) {
        attributeMapper.isRequiredColumn && !columns.contains(attributeMapper.fileColumnAlias)
    }
}