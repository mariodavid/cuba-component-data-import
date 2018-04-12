package de.diedavids.cuba.dataimport.web.importfile

import com.haulmont.cuba.core.entity.FileDescriptor
import com.haulmont.cuba.core.global.AppBeans
import de.diedavids.cuba.dataimport.converter.DataConverterFactory
import de.diedavids.cuba.dataimport.converter.ImportDataConverter
import de.diedavids.cuba.dataimport.dto.ImportData

class ImportFileParser {

    ImportFileHandler importFileHandler

    DataConverterFactory dataConverterFactory

    ImportData parseFile() {
        FileDescriptor fileDescriptor = importFileHandler.uploadedFileDescriptor
        File file = importFileHandler.uploadedFile

        parseFile(fileDescriptor, file)
    }

    ImportData parseFile(FileDescriptor fileDescriptor, File file) {
        ImportDataConverter converter = dataConverterFactory.createTableDataConverter(fileDescriptor)
        converter.convert(file)


    }


}
