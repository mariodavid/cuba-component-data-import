package de.diedavids.cuba.dataimport.service

import com.haulmont.cuba.core.global.AppBeans
import de.diedavids.cuba.dataimport.AbstractImportIntegrationTest
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportLog
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy
import de.diedavids.cuba.dataimport.entity.example.MlbTeam
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class GenericDataImporterServiceBeanTransactionStrategyTest extends AbstractImportIntegrationTest {


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
    void "doDataImport imports no entity if the TransactionStrategy is SINGLE_TRANSACTION and there is an validation error in one of the entities"() {


        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'code', fileColumnAlias: 'code', fileColumnNumber: 2),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )


        ImportData importData = createData([
                [name: null, code: 'BSN'], //null is not allowed for name of MlbTeam
                [name: 'Baltimore Orioles', code: 'BAL']
        ])

        ImportLog importLog = sut.doDataImport(importConfiguration, importData)
        def mlbTeams = simpleDataLoader.loadAll(MlbTeam)

        assertThat(mlbTeams.size()).isEqualTo(0)


        //and:
        assertThat(importLog.entitiesImportSuccess).isEqualTo(0)
    }


    @Test
    void "doDataImport imports all entities if the TransactionStrategy is SINGLE_TRANSACTION and there are no validation errors"() {


        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'code', fileColumnAlias: 'code', fileColumnNumber: 2),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )


        ImportData importData = createData([
                [name: 'Boston Braves', code: 'BSN'], //null is not allowed for name of MlbTeam
                [name: 'Baltimore Orioles', code: 'BAL']
        ])

        ImportLog importLog = sut.doDataImport(importConfiguration, importData)
        def mlbTeams = simpleDataLoader.loadAll(MlbTeam)

        assertThat(mlbTeams.size()).isEqualTo(2)


        //and:
        assertThat(importLog.entitiesProcessed).isEqualTo(2)
        assertThat(importLog.entitiesImportSuccess).isEqualTo(2)
        assertThat(importLog.entitiesImportValidationError).isEqualTo(0)
    }


    @Test
    void "doDataImport imports one entity if the TransactionStrategy is TRANSACTION_PER_ENTITY and there is an validation error in the other one of the entities"() {


        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'code', fileColumnAlias: 'code', fileColumnNumber: 2),
                ],
                transactionStrategy: ImportTransactionStrategy.TRANSACTION_PER_ENTITY
        )


        ImportData importData = createData([
                [name: null, code: 'BSN'], //null is not allowed for name of MlbTeam
                [name: 'Baltimore Orioles', code: 'BAL']
        ])

        ImportLog importLog = sut.doDataImport(importConfiguration, importData)
        def mlbTeams = simpleDataLoader.loadAll(MlbTeam)
        def baltimoreTeam = mlbTeams.first()

        assertThat(mlbTeams.size()).isEqualTo(1)

        assertThat(baltimoreTeam.name).isEqualTo("Baltimore Orioles")
        assertThat(baltimoreTeam.code).isEqualTo("BAL")

        //and:
        assertThat(importLog.entitiesProcessed).isEqualTo(2)
        assertThat(importLog.entitiesImportSuccess).isEqualTo(1)
        assertThat(importLog.entitiesImportValidationError).isEqualTo(1)
    }


}
