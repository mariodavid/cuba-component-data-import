package de.diedavids.cuba.dataimport.converter

import de.diedavids.cuba.dataimport.dto.DataRowImpl
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.dto.ImportDataImpl
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Row

class ExcelImportDataConverter implements ImportDataConverter {

    @Override
    ImportData convert(String content) {
        throw new IllegalArgumentException('String conversion is not supported in case of Excel')
    }

    @Override
    ImportData convert(File file, String fileCharset) {
        def result = new ImportDataImpl()

        parse(file, result)

        result
    }

    private void parse(File file, ImportData result) {

        def reader = new ExcelReader(file)

        DataFormatter dataFormatter = new DataFormatter()

        reader.eachLine(labels: true) { Row row ->
            def rowResult = [:]
            labels.each {
                rowResult["$it"] = ''
            }

            row.cellIterator().each { Cell cell ->
                rowResult[labels[cell.columnIndex]] = dataFormatter.formatCellValue(cell)
            }

            result.columns = labels
            def dataRow = DataRowImpl.ofMap(rowResult)
            if (!dataRow.empty) {
                result.rows << dataRow
            }
        }


    }


}
