package de.diedavids.cuba.dataimport.core.entitybinder

import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.example.MlbPlayer
import de.diedavids.cuba.dataimport.entity.example.MlbTeam
import de.diedavids.cuba.dataimport.entity.example.State
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat;


class EntityBinderDatatypesIntegrationTest extends AbstractEntityBinderIntegrationTest{


    @Test
    void "bindAttributes creates an Entity with a correct String Attribute"() {

        ImportData importData = createData([
                [name: "Simpson", team: "NY Yankees", height: 120]
        ])

        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getName()).isEqualTo("Simpson")
    }

    @Test
    void "bindAttributes creates an Entity with a correct Integer Attribute"() {

        ImportData importData = createData([
                [name: "Simpson", team: "NY Yankees", height: 120]
        ])

        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getHeight()).isEqualTo(120)
    }

    @Test
    void "bindAttributes creates an Entity with a correct Double Attribute"() {

        ImportData importData = createData([
                [age: "22.5d"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.age', fileColumnAlias: 'age', fileColumnNumber: 3),
                ]
        )
        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getAge()).isEqualTo(22.5d)
    }

    @Test
    void "bindAttributes ignores not valid Data formats for Double"() {

        ImportData importData = createData([
                [age: "NotADouble"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.age', fileColumnAlias: 'age', fileColumnNumber: 3),
                ]
        )
        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getAge()).isNull()
    }


    @Test
    void "bindAttributes uses the ImportAttributeMapper to bind the data row columns to the correct entity attributes"() {

        ImportData importData = createData([
                [lastname: "Simpson"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.name', fileColumnAlias: 'lastname', fileColumnNumber: 0)
                ]
        )

        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getName()).isEqualTo("Simpson")
    }


    @Test
    void "bindAttributes creates an Entity with a correct Enum value"() {

        ImportData importData = createData([
                [state: "AL"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbTeam',
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbTeam.state', fileColumnAlias: 'state'),
                ]
        )
        MlbTeam entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbTeam()) as MlbTeam

        assertThat(entity.getState()).isEqualTo(State.AL)
    }


}