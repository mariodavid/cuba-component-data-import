package de.diedavids.cuba.dataimport.web.datapreview.converter

import com.xlson.groovycsv.CsvParser
import com.xlson.groovycsv.PropertyMapper
import de.diedavids.cuba.dataimport.dto.DataRowImpl
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.dto.ImportDataImpl

class CsvTableDataConverter implements TableDataConverter {

    @Override
    ImportData convert(String content) {

        def result = new ImportDataImpl()

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

    private DataRow addToTableData(ImportDataImpl importData, PropertyMapper row) {
        def dataRow = DataRowImpl.ofMap(row.toMap())
        importData.rows << dataRow
        dataRow
    }


}
