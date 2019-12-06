package de.diedavids.cuba.dataimport.integration

import com.haulmont.cuba.core.global.AppBeans
import de.diedavids.cuba.dataimport.AbstractImportIntegrationTest
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy
import de.diedavids.cuba.dataimport.entity.UniqueConfiguration
import de.diedavids.cuba.dataimport.entity.UniqueConfigurationAttribute
import de.diedavids.cuba.dataimport.entity.UniquePolicy
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeMapperMode
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbPlayer
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbTeam
import de.diedavids.cuba.dataimport.entity.example.mlb.State
import de.diedavids.cuba.dataimport.service.GenericDataImporterService
import org.junit.Before
import org.junit.Test
import spock.lang.Issue

import static org.assertj.core.api.Assertions.assertThat


class ImportViewIntegrationTest extends AbstractImportIntegrationTest {


    GenericDataImporterService sut

    ImportConfiguration importConfiguration

    SimpleDataLoader simpleDataLoader

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
                        nameAttributeMapper(),
                        teamAttributeMapper()
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )

        //and:
        MlbTeam balTeam = storeBalTeam()
        MlbTeam howTeam = storeHowTeam()


        //and:
        ImportData importData = createData([
                [name: 'Adam Donachie', team: 'BAL'],
                [name: 'Paul Bako', team: 'HOW']
        ])

        //when:
        sut.doDataImport(importConfiguration, importData)


        //then:
        def mlbPlayers = simpleDataLoader.loadAll(MlbPlayer, 'mlbPlayer-view')

        def balPlayer = mlbPlayers.find { it.team == balTeam }
        assertThat(balPlayer).isNotNull()

    }


    /**
     * given two teams: BAL and HOW are in the DB
     * and one player with a reference to the team HOW inserted
     * when updating the player to change team reference to BAL
     * then the reference should be updated to BAL
     */
    @Issue("https://github.com/mariodavid/cuba-component-data-import/issues/151")
    @Test
    void "when a player updates a reference with matching unique configuration it is correctly updated"() {

        //given:
        importConfiguration = insertImportConfiguration()

        //and:
        MlbTeam balTeam = storeBalTeam()
        MlbTeam howTeam = storeHowTeam()


        //and:
        ImportData insertImportData = createData([
                [name: 'Paul Bako', team: 'HOW']
        ])

        //and:
        sut.doDataImport(importConfiguration, insertImportData)


        //when:
        def updateImportConfiguration = updateImportConfiguration()

        //and:
        ImportData updateImportData = createData([
                [name: 'Paul Bako', team: 'BAL']
        ])

        sut.doDataImport(updateImportConfiguration, updateImportData)

        and:
        def updatedMlbPlayers = simpleDataLoader.loadAll(MlbPlayer, 'mlbPlayer-view')
        def updatedPaul = updatedMlbPlayers[0]

        //then:
        assertThat(updatedMlbPlayers.size()).isEqualTo(1)

        //and:
        assertThat(updatedPaul.team).isEqualTo(balTeam)
    }

    private void storeHowTeam() {
        MlbTeam howTeam = metadata.create(MlbTeam)
        howTeam.name = 'Hagerstown Owls'
        howTeam.code = 'HOW'
        howTeam.state = State.MA
        dataManager.commit(howTeam)
    }

    private MlbTeam storeBalTeam() {
        MlbTeam balTeam = metadata.create(MlbTeam)
        balTeam.name = 'Baltimore Orioles'
        balTeam.code = 'BAL'
        balTeam.state = State.MA
        dataManager.commit(balTeam)
    }

    private ImportConfiguration insertImportConfiguration() {
        new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        nameAttributeMapper(),
                        teamAttributeMapper()
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
    }

    private ImportAttributeMapper teamAttributeMapper() {
        new ImportAttributeMapper(
                mapperMode: AttributeMapperMode.AUTOMATIC,
                attributeType: AttributeType.ASSOCIATION_ATTRIBUTE,
                entityAttribute: 'team',
                associationLookupAttribute: 'code',
                fileColumnAlias: 'team',
                fileColumnNumber: 1
        )
    }

    private ImportAttributeMapper nameAttributeMapper() {
        new ImportAttributeMapper(
                attributeType: AttributeType.DIRECT_ATTRIBUTE,
                entityAttribute: 'name',
                fileColumnAlias: 'name',
                fileColumnNumber: 0
        )
    }

    private ImportConfiguration updateImportConfiguration() {
        new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        nameAttributeMapper(),
                        teamAttributeMapper()
                ],
                uniqueConfigurations: [new UniqueConfiguration(
                        entityAttributes: [
                                new UniqueConfigurationAttribute(entityAttribute: 'name'),
                        ],
                        policy: UniquePolicy.UPDATE
                )],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
    }


}
