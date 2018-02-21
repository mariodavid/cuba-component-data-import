package de.diedavids.cuba.dataimport.web.datapreview.csv

import de.diedavids.cuba.dataimport.web.datapreview.ImportData

interface TableDataConverter {

    ImportData convert(String content)
}