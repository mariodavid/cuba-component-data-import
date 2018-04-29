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

        def json = parseJson(content)

        json.each {
            result.columns = getColumns(it)
            addToTableData(result, it)
        }

        result
    }

    private Object parseJson(String content) {
        new JsonSlurper().parseText(content)
    }

    private List<String> getColumns(it) {
        new ArrayList(it.keySet())
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

}
