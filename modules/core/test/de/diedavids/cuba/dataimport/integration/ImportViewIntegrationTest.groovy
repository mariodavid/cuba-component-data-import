package de.diedavids.cuba.dataimport.integration

import com.haulmont.cuba.core.global.AppBeans
import de.diedavids.cuba.dataimport.AbstractImportIntegrationTest
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbPlayer
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbTeam
import de.diedavids.cuba.dataimport.entity.example.mlb.State
import de.diedavids.cuba.dataimport.service.GenericDataImporterService
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat


@Ignore
class ImportViewIntegrationTest extends AbstractImportIntegrationTest {


    protected GenericDataImporterService sut

    protected ImportConfiguration importConfiguration

    protected SimpleDataLoader simpleDataLoader

    @Before
    void setUp() throws Exception {
        super.setUp()

        sut = AppBeans.get(GenericDataImporterService.NAME)
        simpleDataLoader = AppBeans.get(SimpleDataLoader.NAME)


        clearTable("DDCDI_MLB_PLAYER")
        clearTable("DDCDI_MLB_TEAM")
    }


    @Test
    void "doDataImport adds all local properties to the import view"() {

        //given:
        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'height', fileColumnAlias: 'height', fileColumnNumber: 1),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )

        //and:
        ImportData importData = createData([
                [name: 'Adam Donachie', height: 74],
                [name: 'Paul Bako', height: 74]
        ])

        //when:
        sut.doDataImport(importConfiguration, importData)


        //then:
        def mlbPlayers = simpleDataLoader.loadAll(MlbPlayer)
        assertThat(mlbPlayers.size()).isEqualTo(2)

    }

    @Test
    void "doDataImport adds N:1 associations to the import view"() {

        //given:
        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(
                                attributeType: AttributeType.ASSOCIATION_ATTRIBUTE,
                                entityAttribute: 'team',
                                associationLookupAttribute: 'code',
                                fileColumnAlias: 'team',
                                fileColumnNumber: 1
                        )
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )

        //and:
        MlbTeam balTeam = metadata.create(MlbTeam)
        balTeam.name = 'Baltimore Orioles'
        balTeam.code = 'BAL'
        balTeam.state = State.MA
        dataManager.commit(balTeam)

        MlbTeam howTeam = metadata.create(MlbTeam)
        howTeam.name = 'Hagerstown Owls'
        howTeam.code = 'HOW'
        howTeam.state = State.MA
        dataManager.commit(howTeam)


        //and:
        ImportData importData = createData([
                [name: 'Adam Donachie', team: 'BAL'],
                [name: 'Paul Bako', team: 'HOW']
        ])

        //when:
        sut.doDataImport(importConfiguration, importData)


        //then:
        def mlbPlayers = simpleDataLoader.loadAll(MlbPlayer, 'mlbPlayer-view')

        mlbPlayers.each {
            println "hallo + " + it.name + ", team:" + it.team
        }
        def balPlayer = mlbPlayers.find { it.team == balTeam }
        assertThat(balPlayer).isNotNull()

    }



}
