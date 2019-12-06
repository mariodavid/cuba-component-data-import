package de.diedavids.cuba.dataimport.integration

import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.View
import com.haulmont.cuba.core.global.ViewRepository
import de.diedavids.cuba.dataimport.AbstractDbIntegrationTest
import de.diedavids.cuba.dataimport.entity.*
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbTeam
import de.diedavids.cuba.dataimport.service.UniqueEntityFinderService
import org.junit.Before
import org.junit.Test
import spock.lang.Subject

import static org.assertj.core.api.Assertions.assertThat

class UniqueEntityFinderServiceBeanIntegrationTest extends AbstractDbIntegrationTest {


    @Subject
    UniqueEntityFinderService sut

    ImportConfiguration importConfiguration
    ViewRepository viewRepository

    @Before
    void setUp() throws Exception {


        super.setUp()

        sut = AppBeans.get(UniqueEntityFinderService.NAME)
        viewRepository = AppBeans.get(ViewRepository)

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'height', fileColumnAlias: 'height', fileColumnNumber: 2),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )


        clearTable("DDCDI_MLB_PLAYER")
        clearTable("DDCDI_MLB_TEAM")
    }


    @Test
    void "findUniqueEntity finds an existing entity that fulfills the unique configurations with a given entity"() {

        //given:
        MlbTeam existingBalTeam = createAndStoreMlbTeam('Baltimore Orioles', 'BAL')
        MlbTeam entityToBeImported = createMlbTeam('Baltimore Orioles', 'BAL')

        List<UniqueConfiguration> uniqueConfigurations = createCodeAndNameUniqueConfiguration()

        //when:
        MlbTeam existingUniqueEntityResult = sut.findEntity(
                entityToBeImported,
                uniqueConfigurations,
                viewRepository.getView(metadata.getClass(MlbTeam), View.LOCAL)
        ) as MlbTeam

        //then:
        assertThat(existingUniqueEntityResult).isNotNull()
        assertThat(existingUniqueEntityResult).isEqualTo(existingBalTeam)

        cont.deleteRecord(existingBalTeam)
    }


    @Test
    void "findUniqueEntity finds no entity that fulfills the unique configurations with a given entity"() {

        MlbTeam existingBalTeam = createAndStoreMlbTeam('Baltimore Orioles', 'BAL')
        MlbTeam entityToBeImported = createMlbTeam('Other Baltimore Team', 'BAL')

        List<UniqueConfiguration> uniqueConfigurations = createCodeAndNameUniqueConfiguration()

        MlbTeam existingUniqueEntityResult = sut.findEntity(
                entityToBeImported,
                uniqueConfigurations,
                viewRepository.getView(metadata.getClass(MlbTeam), View.LOCAL)
        ) as MlbTeam

        assertThat(existingUniqueEntityResult).isNull()

        cont.deleteRecord(existingBalTeam)
    }

    private List<UniqueConfiguration> createCodeAndNameUniqueConfiguration() {
        Collection<UniqueConfiguration> uniqueConfigurations = [
                new UniqueConfiguration(
                        entityAttributes: [
                                new UniqueConfigurationAttribute(entityAttribute: 'code'),
                                new UniqueConfigurationAttribute(entityAttribute: 'name'),
                        ],
                        policy: UniquePolicy.SKIP
                )
        ]
        uniqueConfigurations
    }

    private MlbTeam createAndStoreMlbTeam(String name, String code) {
        dataManager.commit(createMlbTeam(name, code))
    }

    private MlbTeam createMlbTeam(String name, String code) {
        MlbTeam existingBalTeam = metadata.create(MlbTeam)
        existingBalTeam.name = name
        existingBalTeam.code = code
        existingBalTeam
    }

}
