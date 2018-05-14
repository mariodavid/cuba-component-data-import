package de.diedavids.cuba.dataimport.service

import com.haulmont.cuba.core.global.AppBeans
import de.diedavids.cuba.dataimport.AbstractImportIntegrationTest
import de.diedavids.cuba.dataimport.data.SimpleDataLoader
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.*
import de.diedavids.cuba.dataimport.entity.example.MlbTeam
import de.diedavids.cuba.dataimport.entity.example.State
import de.diedavids.cuba.dataimport.service.GenericDataImporterService
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

class GenericDataImporterServiceBeanTest extends AbstractImportIntegrationTest {


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
    void "doDataImport updates the existing entity if there is a unique match with policy UPDATE"() {


        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(entityAttribute: 'code', fileColumnAlias: 'code', fileColumnNumber: 2),
                        new ImportAttributeMapper(entityAttribute: 'state', fileColumnAlias: 'state', fileColumnNumber: 2),
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
                        new ImportAttributeMapper(entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(entityAttribute: 'code', fileColumnAlias: 'code', fileColumnNumber: 2),
                        new ImportAttributeMapper(entityAttribute: 'state', fileColumnAlias: 'state', fileColumnNumber: 2),
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
