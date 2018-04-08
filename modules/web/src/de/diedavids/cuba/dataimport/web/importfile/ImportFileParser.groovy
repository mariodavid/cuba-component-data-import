package de.diedavids.cuba.dataimport.web.importfile

import com.haulmont.cuba.core.entity.FileDescriptor
import de.diedavids.cuba.dataimport.converter.DataConverterFactory
import de.diedavids.cuba.dataimport.converter.ImportDataConverter
import de.diedavids.cuba.dataimport.dto.ImportData

class ImportFileParser {

    ImportFileHandler importFileHandler

    DataConverterFactory dataConverterFactory

    ImportData parseFile() {
        def fileDescriptor = importFileHandler.uploadedFileDescriptor
        def fileContent = importFileHandler.uploadedFile.text

        parseFile(fileDescriptor, fileContent)
    }

    ImportData parseFile(FileDescriptor fileDescriptor, String fileContent) {
        ImportDataConverter converter = dataConverterFactory.createTableDataConverter(fileDescriptor)
        converter.convert(fileContent)
    }


}
