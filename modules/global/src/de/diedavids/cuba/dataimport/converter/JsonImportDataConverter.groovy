package de.diedavids.cuba.dataimport.converter

import de.diedavids.cuba.dataimport.dto.ImportData
import groovy.json.JsonSlurper

class JsonImportDataConverter extends AbstractImportDataConverter<Object> {


    @Override
    protected doConvert(Object entries, ImportData result) {
        entries.each {
            result.columns = getColumns(it)
            addToTableData(result, it as Map<String, Object>)
        }
    }

    @Override
    protected Object parse(String content) {
        new JsonSlurper().parseText(content)
    }

}
