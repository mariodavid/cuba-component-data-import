package de.diedavids.cuba.dataimport.integration

import com.haulmont.cuba.core.global.AppBeans
import de.diedavids.cuba.dataimport.AbstractImportIntegrationTest
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportExecution
import de.diedavids.cuba.dataimport.entity.ImportLogRecord
import de.diedavids.cuba.dataimport.entity.ImportExecutionRecordCategory
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.service.GenericDataImporterService
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class ImportExecutionIntegrationTest extends AbstractImportIntegrationTest {


    protected GenericDataImporterService sut

    protected ImportConfiguration importConfiguration

    protected SimpleDataLoader simpleDataLoader

    @Before
    void setUp() throws Exception {
        super.setUp()

        sut = AppBeans.get(GenericDataImporterService.NAME)
        simpleDataLoader = AppBeans.get(SimpleDataLoader.NAME)

        clearTable("DDCDI_MLB_TEAM")
        clearTable("DDCDI_IMPORT_LOG_RECORD")
        clearTable("DDCDI_IMPORT_LOG")
    }


    @Test
    void "doDataImport stores the import log and returns the value"() {

        // given:
        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'code', fileColumnAlias: 'code', fileColumnNumber: 1),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )

        // and:
        ImportData importData = createData([
                [name: null, code: 'BSN'],
                [name: 'Baltimore Orioles', code: 'BAL']
        ])

        // when:
        ImportExecution importExecution = sut.doDataImport(importConfiguration, importData)

        // then:
        ImportExecution persistedImportLog = simpleDataLoader.load(ImportExecution, importExecution.getId(), "importExecution-with-records-view")
        assertThat(persistedImportLog.records.size()).isEqualTo(1)

        // and:
        ImportLogRecord importExecutionRecord = persistedImportLog.records[0]
        assertThat(importExecutionRecord.category).isEqualTo(ImportExecutionRecordCategory.VALIDATION)
    }

}
