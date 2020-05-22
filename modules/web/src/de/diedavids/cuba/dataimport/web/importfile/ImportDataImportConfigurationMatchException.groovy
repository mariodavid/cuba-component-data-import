package de.diedavids.cuba.dataimport.web.importfile

import de.diedavids.cuba.dataimport.dto.ColumnValidationResult

class ImportDataImportConfigurationMatchException extends RuntimeException {

    ImportDataImportConfigurationMatchException(ColumnValidationResult result) {
        super('Import File does not match import configuration. Please check required columns: '
                + result.getColumns().join(", "))
    }
}
