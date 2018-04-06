package de.diedavids.cuba.dataimport.core.entitybinder

import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.example.MlbPlayer
import de.diedavids.cuba.dataimport.entity.example.MlbTeam
import de.diedavids.cuba.dataimport.entity.example.State
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
    void "bindAttributes creates an Entity with a association value"() {


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
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.team.code', fileColumnAlias: 'team'),
                ]
        )


        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer


        assertThat(entity.getTeam()).isEqualTo(balTeam)

        cont.deleteRecord(balTeam)
    }


    @Test
    void "bindAttributes creates an Entity with a non-unique association value"() {


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
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.team.code', fileColumnAlias: 'team'),
                ]
        )


        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer


        assertThat(entity.getTeam()).isEqualTo(balTeam)

        cont.deleteRecord(balTeam)
    }

}