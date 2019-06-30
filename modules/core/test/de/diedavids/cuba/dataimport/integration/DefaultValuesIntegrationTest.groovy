package de.diedavids.cuba.dataimport.integration

import com.haulmont.cuba.core.app.importexport.EntityImportView
import com.haulmont.cuba.core.app.importexport.ReferenceImportBehaviour
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

import java.util.function.Consumer
import java.util.function.Function

import static org.assertj.core.api.Assertions.assertThat


class DefaultValuesIntegrationTest extends AbstractImportIntegrationTest {


    protected GenericDataImporterService sut

    protected ImportConfiguration importConfiguration

    protected SimpleDataLoader simpleDataLoader

    @Before
    void setUp() throws Exception {
        super.setUp()

        sut = AppBeans.get(GenericDataImporterService.NAME)
        simpleDataLoader = AppBeans.get(SimpleDataLoader.NAME)

        clearTable("DDCDI_MLB_TEAM")
        clearTable("DDCDI_MLB_PLAYER")

    }


    @Test
    void "doDataImport sets a local default value attribute correctly"() {

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
        sut.doDataImport(importConfiguration, importData, [
                leftHanded: true
        ])


        //then:
        def mlbPlayers = simpleDataLoader.loadAll(MlbPlayer)
        assertThat(mlbPlayers.size()).isEqualTo(2)

        mlbPlayers.each {
            assertThat(it.leftHanded).isTrue()
        }

    }

    @Test
    void "doDataImport sets a reference default value attribute correctly"() {

        //given:
        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0)
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )

        //and:
        MlbTeam balTeam = metadata.create(MlbTeam)
        balTeam.name = 'Baltimore Orioles'
        balTeam.code = 'BAL'
        balTeam.state = State.MA
        dataManager.commit(balTeam)


        //and:
        ImportData importData = createData([
                [name: 'Adam Donachie'],
                [name: 'Paul Bako']
        ])

        //when:
        sut.doDataImport(importConfiguration, importData, [
                team: balTeam
        ], new Consumer<EntityImportView>() {
            @Override
            void accept(EntityImportView entityImportView) {
                entityImportView.addManyToOneProperty("team", ReferenceImportBehaviour.IGNORE_MISSING)
            }
        })


        //then:
        def mlbPlayers = simpleDataLoader.loadAll(MlbPlayer, 'mlbPlayer-view')

        mlbPlayers.each {
            assertThat(it.team).isEqualTo(balTeam)
        }
    }



}
