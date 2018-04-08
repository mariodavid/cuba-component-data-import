package de.diedavids.cuba.dataimport.web.importwizard

import com.haulmont.cuba.core.entity.FileDescriptor
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.web.datapreview.converter.CsvTableDataConverter
import de.diedavids.cuba.dataimport.web.datapreview.converter.ExcelTableDataConverter
import de.diedavids.cuba.dataimport.web.datapreview.converter.TableDataConverter

class ImportFileParser {

    ImportFileHandler importFileHandler


    ImportData parseFile() {
        TableDataConverter converter = createTableDataConverter(importFileHandler.uploadedFileDescriptor)
        converter.convert(importFileHandler.uploadedFile.text)
    }

    private TableDataConverter createTableDataConverter(FileDescriptor fileDescriptor) {
        switch (fileDescriptor.extension) {
            case 'xlsx': return new ExcelTableDataConverter()
            case 'csv': return new CsvTableDataConverter()
            default: throw new FileNotSupportedException()
        }
    }

}
