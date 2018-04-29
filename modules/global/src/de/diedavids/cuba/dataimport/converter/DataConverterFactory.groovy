package de.diedavids.cuba.dataimport.converter

import com.haulmont.cuba.core.entity.FileDescriptor
import org.springframework.stereotype.Component

@Component('ddcdi_DataConverterFactory')
class DataConverterFactory {

    ImportDataConverter createTableDataConverter(FileDescriptor fileDescriptor) {
        switch (fileDescriptor.extension) {
            case 'xlsx': return new ExcelImportDataConverter()
            case 'csv': return new CsvImportDataConverter()
            case 'json': return new JsonImportDataConverter()
            case 'xml': return new XmlImportDataConverter()
            default: throw new FileNotSupportedException()
        }
    }
}
