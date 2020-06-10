package de.diedavids.cuba.dataimport.web.importfile

import de.diedavids.cuba.dataimport.dto.ColumnValidationResult

class ImportDataImportConfigurationMatchException extends RuntimeException {

    public static final String DELIMITER = ', '
    final private ColumnValidationResult result

    ImportDataImportConfigurationMatchException(ColumnValidationResult result) {
        super('Import File does not match import configuration. Please check required columns: '
                + result.columns.join(DELIMITER))
        this.result = result
    }

    String getResult() {
        result.columns.join(DELIMITER)
    }
}
