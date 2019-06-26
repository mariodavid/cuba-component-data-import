package de.diedavids.cuba.dataimport.converter

import de.diedavids.cuba.dataimport.dto.ImportData

interface ImportDataConverter {

    ImportData convert(String content)
    ImportData convert(File file, String fileCharset)
}