package de.diedavids.cuba.dataimport.converter

import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.dto.DataRowImpl
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.dto.ImportDataImpl
import groovy.json.JsonSlurper

class JsonImportDataConverter implements ImportDataConverter {
    @Override
    ImportData convert(String content) {
        def result = new ImportDataImpl()

        def json = new JsonSlurper().parseText(content)


        json.each {
            result.columns = new ArrayList(it.keySet())
            addToTableData(result, it)
        }

        result
    }

    private DataRow addToTableData(ImportDataImpl importData, Map row) {
        def dataRow = DataRowImpl.ofMap(row)
        importData.rows << dataRow
        dataRow
    }

    @Override
    ImportData convert(File file) {
        convert(file.text)
    }

    /*

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

    @Override
    ImportData convert(File file) {
        convert(file.text)
    }

    private Iterator parseCSV(String content) {
        new CsvParser().parse(content)
    }

    private DataRow addToTableData(ImportDataImpl importData, PropertyMapper row) {
        def dataRow = DataRowImpl.ofMap(row.toMap())
        importData.rows << dataRow
        dataRow
    }
     */
}
