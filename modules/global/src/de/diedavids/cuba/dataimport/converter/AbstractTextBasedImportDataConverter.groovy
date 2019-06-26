package de.diedavids.cuba.dataimport.converter

import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.dto.DataRowImpl
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.dto.ImportDataImpl

abstract class AbstractTextBasedImportDataConverter<T> implements ImportDataConverter {

    @Override
    ImportData convert(File file, String fileCharset) {
        convert(file.getText(fileCharset))
    }

    @Override
    ImportData convert(String content) {
        ImportData result = new ImportDataImpl()

        def entries = parse(content)

        doConvert(entries, result)

        result
    }

    /**
     * parses the file content as a String and returns a parse result (T)
     * @param content the file content as a String
     * @return the parse result (T)
     */
    abstract protected T parse(String content)




    /**
     * hook method for execution the conversion after the file has parsed via AbstractTextBasedImportDataConverter.parse
     * @param entries the parsed entries
     * @param result the ImportData result
     */
    abstract protected void doConvert(T entries, ImportData result)


    protected DataRow addToTableData(ImportData importData, Map<String, Object> row) {
        def dataRow = DataRowImpl.ofMap(row)
        if (dataRow.empty) {
            return
        }

        importData.rows << dataRow
        dataRow
    }


}
