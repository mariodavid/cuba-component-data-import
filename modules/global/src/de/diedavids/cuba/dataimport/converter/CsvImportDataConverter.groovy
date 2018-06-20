package de.diedavids.cuba.dataimport.converter

import com.xlson.groovycsv.CsvParser
import com.xlson.groovycsv.PropertyMapper
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.dto.ImportData

class CsvImportDataConverter extends AbstractTextBasedImportDataConverter<Iterator> {


    @Override
    protected void doConvert(Iterator entries, ImportData result) {
        entries.each { PropertyMapper row ->

            DataRow dataRow = addToTableData(result, row.toMap())
            if (dataRow) {
                result.columns = dataRow.columnNames
            }
        }
    }

    @Override
    protected Iterator parse(String content) {
        new CsvParser().parse(content)
    }

}
