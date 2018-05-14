package de.diedavids.cuba.dataimport

import de.diedavids.cuba.dataimport.dto.DataRowImpl
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.dto.ImportDataImpl

abstract class AbstractImportIntegrationTest extends AbstractDbIntegrationTest {


    ImportData createData(List<Map<String, Object>> data) {
        ImportData importData = new ImportDataImpl()

        data.each {
            def dataRow = DataRowImpl.ofMap(it)
            importData.rows.add(dataRow)
        }

        importData
    }


}
