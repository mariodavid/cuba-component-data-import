package de.diedavids.cuba.dataimport.web.importfile

class ImportDataImportConfigurationMatchException extends RuntimeException {

    ImportDataImportConfigurationMatchException() {
        super('Import File does not match import configuration')
    }
}
