package de.diedavids.cuba.dataimport.converter

import com.haulmont.cuba.core.entity.FileDescriptor
import org.springframework.stereotype.Component

@Component
class DataConverterFactory {

    ImportDataConverter createTableDataConverter(FileDescriptor fileDescriptor) {
        switch (fileDescriptor.extension) {
            case 'xlsx': return new ExcelImportDataConverter()
            case 'csv': return new CsvImportDataConverter()
            default: throw new FileNotSupportedException()
        }
    }
}
