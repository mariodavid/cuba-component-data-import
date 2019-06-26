package de.diedavids.cuba.dataimport.web.importfile

import com.haulmont.cuba.core.entity.FileDescriptor
import de.diedavids.cuba.dataimport.converter.DataConverterFactory
import de.diedavids.cuba.dataimport.converter.ImportDataConverter
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportConfiguration

class ImportFileParser {

    ImportFileHandler importFileHandler

    DataConverterFactory dataConverterFactory

    ImportData parseFile(String fileCharset) {
        FileDescriptor fileDescriptor = importFileHandler.uploadedFileDescriptor
        File file = importFileHandler.uploadedFile

        parseFile(fileDescriptor, file, fileCharset)
    }

    ImportData parseFile(ImportConfiguration importConfiguration) {
        def importData = parseFile(importConfiguration.fileCharset)

        if (!importDataMatchesImportConfiguration(importData, importConfiguration)) {
            throw new ImportDataImportConfigurationMatchException()
        }

        importData

    }


    boolean importDataMatchesImportConfiguration(ImportData importData, ImportConfiguration importConfiguration) {
        importData.isCompatibleWith(importConfiguration.importAttributeMappers)
    }

    ImportData parseFile(FileDescriptor fileDescriptor, File file, String fileCharset) {
        ImportDataConverter converter = dataConverterFactory.createTableDataConverter(fileDescriptor)
        converter.convert(file, fileCharset)
    }


}
