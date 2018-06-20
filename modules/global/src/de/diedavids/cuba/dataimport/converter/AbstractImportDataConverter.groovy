package de.diedavids.cuba.dataimport.converter

import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.dto.DataRowImpl
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.dto.ImportDataImpl

abstract class AbstractImportDataConverter<T> implements ImportDataConverter {

    @Override
    ImportData convert(File file) {
        convert(file.text)
    }

    @Override
    ImportData convert(String content) {
        ImportData result = new ImportDataImpl()

        def entries = parse(content)

        doConvert(entries, result)

        result
    }

    abstract protected doConvert(T entries, ImportData result)

    abstract protected T parse(String content)

    protected List<String> getColumns(it) {
        new ArrayList(it.keySet())
    }

    protected DataRow addToTableData(ImportData importData, Map<String, Object> row) {
        def dataRow = DataRowImpl.ofMap(row)
        if (dataRow.empty) {
            return
        }

        importData.rows << dataRow
        dataRow
    }


}
