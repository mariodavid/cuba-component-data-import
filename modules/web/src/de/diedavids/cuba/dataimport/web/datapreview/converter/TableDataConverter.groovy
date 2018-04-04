package de.diedavids.cuba.dataimport.web.datapreview.converter

import de.diedavids.cuba.dataimport.dto.ImportData

interface TableDataConverter {

    ImportData convert(String content)
}