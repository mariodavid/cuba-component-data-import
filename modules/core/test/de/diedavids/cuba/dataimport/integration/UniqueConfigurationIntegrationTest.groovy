package de.diedavids.cuba.dataimport.integration

import com.haulmont.cuba.core.global.AppBeans
import de.diedavids.cuba.dataimport.AbstractImportIntegrationTest
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.*
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbTeam
import de.diedavids.cuba.dataimport.entity.example.mlb.State
import de.diedavids.cuba.dataimport.service.GenericDataImporterService
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

class UniqueConfigurationIntegrationTest extends AbstractImportIntegrationTest {


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
    void "doDataImport updates the existing entity if there is a unique match with policy UPDATE"() {


        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'code', fileColumnAlias: 'code', fileColumnNumber: 2),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'state', fileColumnAlias: 'state', fileColumnNumber: 2),
                ],
                uniqueConfigurations: [new UniqueConfiguration(
                        entityAttributes: [
                                new UniqueConfigurationAttribute(entityAttribute: 'code'),
                                new UniqueConfigurationAttribute(entityAttribute: 'name'),
                        ],
                        policy: UniquePolicy.UPDATE
                )],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )



        MlbTeam existingBalTeam = createAndStoreMlbTeam('Baltimore Orioles', 'BAL', State.AL)

        ImportData importData = createData([
                [name: 'Baltimore Orioles', code: 'BAL', state: State.CA]
        ])


        sut.doDataImport(importConfiguration, importData)

        def storedExistingBalTeam = dataManager.reload(existingBalTeam, '_local')
        assertThat(storedExistingBalTeam.state).isEqualTo(State.CA)

        cont.deleteRecord(existingBalTeam)
    }


    @Test
    void "doDataImport does not update the existing entity if there is a unique match with policy SKIP"() {


        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'code', fileColumnAlias: 'code', fileColumnNumber: 2),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'state', fileColumnAlias: 'state', fileColumnNumber: 2),
                ],
                uniqueConfigurations: [new UniqueConfiguration(
                        entityAttributes: [
                                new UniqueConfigurationAttribute(entityAttribute: 'code'),
                                new UniqueConfigurationAttribute(entityAttribute: 'name'),
                        ],
                        policy: UniquePolicy.SKIP
                )],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )



        MlbTeam existingBalTeam = createAndStoreMlbTeam('Baltimore Orioles', 'BAL', State.AL)

        ImportData importData = createData([
                [name: 'Baltimore Orioles', code: 'BAL', state: State.CA]
        ])


        sut.doDataImport(importConfiguration, importData)

        def storedExistingBalTeam = dataManager.reload(existingBalTeam, '_local')
        assertThat(storedExistingBalTeam.state).isEqualTo(State.AL)
        cont.deleteRecord(existingBalTeam)
    }


    @Test
    void "doDataImport aborts the existing entity if there is a unique match with policy ABORT"() {


        // given
        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'code', fileColumnAlias: 'code', fileColumnNumber: 2),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'state', fileColumnAlias: 'state', fileColumnNumber: 2),
                ],
                uniqueConfigurations: [new UniqueConfiguration(
                        entityAttributes: [
                                new UniqueConfigurationAttribute(entityAttribute: 'code'),
                                new UniqueConfigurationAttribute(entityAttribute: 'name'),
                        ],
                        policy: UniquePolicy.ABORT
                )],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )



        // and
        MlbTeam existingBaltimoreOrioles = createAndStoreMlbTeam('Baltimore Orioles', 'BAL', State.AL)

        // when
        ImportData importData = createData([
                [name: 'Baltimore Orioles', code: 'BAL', state: State.CA],
                [name: 'Boston Braves', code: 'BSN', state: State.CA],
        ])

        sut.doDataImport(importConfiguration, importData)

        // then
        def baltimoreOrioles = dataManager.reload(existingBaltimoreOrioles, '_local')
        assertThat(baltimoreOrioles.state).isEqualTo(State.AL)


        // and
        assertThat(simpleDataLoader.loadAll(MlbTeam).size()).isEqualTo(1)

        cont.deleteRecord(existingBaltimoreOrioles)
    }


    @Test
    void "doDataImport imports all data if there is no unique match with policy ABORT"() {


        // given
        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'code', fileColumnAlias: 'code', fileColumnNumber: 2),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'state', fileColumnAlias: 'state', fileColumnNumber: 2),
                ],
                uniqueConfigurations: [new UniqueConfiguration(
                        entityAttributes: [
                                new UniqueConfigurationAttribute(entityAttribute: 'code'),
                                new UniqueConfigurationAttribute(entityAttribute: 'name'),
                        ],
                        policy: UniquePolicy.ABORT
                )],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )



        // when
        ImportData importData = createData([
                [name: 'Baltimore Orioles', code: 'BAL', state: State.CA],
                [name: 'Boston Braves', code: 'BSN', state: State.CA],
        ])

        sut.doDataImport(importConfiguration, importData)

        // then
        def allMlbTeams = simpleDataLoader.loadAll(MlbTeam)
        assertThat(allMlbTeams.size()).isEqualTo(2)

        allMlbTeams.each {
            cont.deleteRecord(it)
        }
    }




    private MlbTeam createAndStoreMlbTeam(String name, String code, State state) {
        dataManager.commit(createMlbTeam(name, code, state))
    }

    private MlbTeam createMlbTeam(String name, String code, State state) {
        MlbTeam existingBalTeam = metadata.create(MlbTeam)
        existingBalTeam.name = name
        existingBalTeam.code = code
        existingBalTeam.state = state
        existingBalTeam
    }

}
