package de.diedavids.cuba.dataimport.web.datapreview.csv

import com.xlson.groovycsv.CsvParser
import com.xlson.groovycsv.PropertyMapper
import de.diedavids.cuba.dataimport.web.datapreview.DataRow
import de.diedavids.cuba.dataimport.web.datapreview.ImportData

class CsvTableDataConverter {

    ImportData convert(String content) {

        def result = new ImportData()

        def csvRows = parseCSV(content)
        csvRows.each { PropertyMapper row ->
            DataRow dataRow = addToTableData(result, row)
            result.columns = dataRow.columnNames
        }
        result
    }

    private Iterator parseCSV(String content) {
        new CsvParser().parse(content)
    }

    private DataRow addToTableData(ImportData importData, PropertyMapper row) {
        def dataRow = DataRow.ofMap(row.toMap())
        importData.rows << dataRow
        dataRow
    }


}
