package de.diedavids.cuba.dataimport.binding

import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbPlayer
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbTeam
import de.diedavids.cuba.dataimport.entity.example.mlb.State
import org.junit.Before
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat;


class EntityBinderAssociationIntegrationTest extends AbstractEntityBinderIntegrationTest {


    @Before
    void setUp() throws Exception {
        super.setUp()

        clearTable("DDCDI_MLB_PLAYER")
        clearTable("DDCDI_MLB_TEAM")
    }

    @Test
    void "bindAttributes creates an Entity with a association value of type String"() {


        ImportData importData = createData([
                [team: "BAL"]
        ])


        MlbTeam balTeam = metadata.create(MlbTeam)
        balTeam.name = 'Baltimore Orioles'
        balTeam.code = 'BAL'
        balTeam.state = State.MA

        dataManager.commit(balTeam)

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(
                                attributeType: AttributeType.ASSOCIATION_ATTRIBUTE,
                                entityAttribute: 'team',
                                associationLookupAttribute: 'code',
                                fileColumnAlias: 'team'
                        )
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )


        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer


        assertThat(entity.getTeam()).isEqualTo(balTeam)

        cont.deleteRecord(balTeam)
    }

    @Test
    void "bindAttributes creates an Entity with a association value of type Integer"() {


        ImportData importData = createData([
                [team: 6578897]
        ])


        MlbTeam balTeam = metadata.create(MlbTeam)
        balTeam.name = 'Baltimore Orioles'
        balTeam.code = 'BAL'
        balTeam.state = State.MA
        balTeam.telephone = 6578897


        dataManager.commit(balTeam)

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(
                                attributeType: AttributeType.ASSOCIATION_ATTRIBUTE,
                                entityAttribute: 'team',
                                associationLookupAttribute: 'telephone',
                                fileColumnAlias: 'team'
                        )
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )


        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getTeam()).isEqualTo(balTeam)

        cont.deleteRecord(balTeam)
    }

    @Test
    void "bindAttributes creates an Entity with a association value of type Enum"() {


        ImportData importData = createData([
                [team: State.MA]
        ])


        MlbTeam balTeam = metadata.create(MlbTeam)
        balTeam.name = 'Baltimore Orioles'
        balTeam.code = 'BAL'
        balTeam.state = State.MA


        dataManager.commit(balTeam)

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(
                                attributeType: AttributeType.ASSOCIATION_ATTRIBUTE,
                                entityAttribute: 'team',
                                associationLookupAttribute: 'state',
                                fileColumnAlias: 'team'
                        )
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )


        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getTeam()).isEqualTo(balTeam)

        cont.deleteRecord(balTeam)
    }


    @Test
    void "bindAttributes cannot create an entity if the entity attribute mapper has no association Lookup attribute defined"() {


        ImportData importData = createData([
                [team: "BAL"]
        ])


        MlbTeam balTeam = metadata.create(MlbTeam)
        balTeam.name = 'Baltimore Orioles'
        balTeam.code = 'BAL'
        balTeam.state = State.MA

        dataManager.commit(balTeam)

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(
                                attributeType: AttributeType.ASSOCIATION_ATTRIBUTE,
                                entityAttribute: 'team',
                                associationLookupAttribute: null,
                                fileColumnAlias: 'team'
                        )
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )


        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer


        assertThat(entity.getTeam()).isNull()

        cont.deleteRecord(balTeam)
    }


    @Test
    void "bindAttributes creates an Entity with a non-unique association value"() {


        ImportData importData = createData([
                [team_state: "MA"]
        ])


        MlbTeam team1InMaryland = metadata.create(MlbTeam)
        team1InMaryland.name = 'Baltimore Orioles'
        team1InMaryland.code = 'BAL'
        team1InMaryland.state = State.MA

        MlbTeam team2InMaryland = metadata.create(MlbTeam)
        team2InMaryland.name = 'Hagerstown Owls'
        team2InMaryland.code = 'HOW'
        team2InMaryland.state = State.MA

        dataManager.commit(team1InMaryland)
        dataManager.commit(team2InMaryland)

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [

                        new ImportAttributeMapper(
                                attributeType: AttributeType.ASSOCIATION_ATTRIBUTE,
                                entityAttribute: 'team',
                                associationLookupAttribute: 'state',
                                fileColumnAlias: 'team_state'
                        )
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )


        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer


        assertThat(entity.getTeam()).isNull()

        cont.deleteRecord(team1InMaryland)
        cont.deleteRecord(team2InMaryland)
    }

    @Test
    void "bindAttributes creates an Entity with a not-existing association value"() {


        ImportData importData = createData([
                [team: "NOT_EXISTING_TEAM_CODE"]
        ])

        MlbTeam balTeam = metadata.create(MlbTeam)
        balTeam.name = 'Baltimore Orioles'
        balTeam.code = 'BAL'
        balTeam.state = State.MA

        dataManager.commit(balTeam)

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(
                                attributeType: AttributeType.ASSOCIATION_ATTRIBUTE,
                                entityAttribute: 'team',
                                associationLookupAttribute: 'code',
                                fileColumnAlias: 'team'
                        )
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )

        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getTeam()).isNull()

        cont.deleteRecord(balTeam)
    }

}