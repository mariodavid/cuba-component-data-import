package de.diedavids.cuba.dataimport.dto

import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper

class ImportDataImpl implements ImportData {
    List<DataRow> rows = []
    List<String> columns = []

    @Override
    boolean isCompatibleWith(List<ImportAttributeMapper> attributeMappers) {
        attributeMappers.every { attributeMapper ->
            columns[attributeMapper.fileColumnNumber] == attributeMapper.fileColumnAlias
        }
    }
}