package de.diedavids.cuba.dataimport.service

import com.haulmont.cuba.core.global.AppBeans
import de.diedavids.cuba.dataimport.AbstractImportIntegrationTest
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.*
import de.diedavids.cuba.dataimport.entity.example.MlbTeam
import de.diedavids.cuba.dataimport.service.GenericDataImporterService
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class GenericDataImporterServiceBeanPreCommitScriptTest extends AbstractImportIntegrationTest {


    protected GenericDataImporterService sut

    protected ImportConfiguration importConfiguration

    protected SimpleDataLoader simpleDataLoader

    @Before
    void setUp() throws Exception {
        super.setUp()

        sut = AppBeans.get(GenericDataImporterService.NAME)
        simpleDataLoader = AppBeans.get(SimpleDataLoader.NAME)


        clearTable("DDCDI_MLB_TEAM")
    }


    @Test
    void "doDataImport imports an entity depending on the result of the preCommitScript"() {


        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(entityAttribute: 'code', fileColumnAlias: 'code', fileColumnNumber: 2),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION,
                preCommitScript: """
if (entity.code == 'BAL') {
    return true
}
else {
    return false
}
"""
        )

        ImportData importData = createData([
                [name: 'Boston Braves', code: 'BSN'],
                [name: 'Baltimore Orioles', code: 'BAL']
        ])

        ImportLog importLog = sut.doDataImport(importConfiguration, importData)
        def mlbTeams = simpleDataLoader.loadAll(MlbTeam)
        def baltimoreTeam = mlbTeams.first()

        assertThat(mlbTeams.size()).isEqualTo(1)
        assertThat(baltimoreTeam.name).isEqualTo("Baltimore Orioles")
        assertThat(baltimoreTeam.code).isEqualTo("BAL")


        //and:
        assertThat(importLog.entitiesPreCommitSkipped).isEqualTo(1)
    }


    @Test
    void "doDataImport imports an entity if there is no preCommitScript given"() {


        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(entityAttribute: 'code', fileColumnAlias: 'code', fileColumnNumber: 2),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )

        ImportData importData = createData([
                [name: 'Boston Braves', code: 'BSN'],
                [name: 'Baltimore Orioles', code: 'BAL']
        ])

        ImportLog importLog = sut.doDataImport(importConfiguration, importData)

        def mlbTeams = simpleDataLoader.loadAll(MlbTeam)

        assertThat(mlbTeams.size()).isEqualTo(2)
        assertThat(importLog.entitiesPreCommitSkipped).isEqualTo(0)
    }

}
