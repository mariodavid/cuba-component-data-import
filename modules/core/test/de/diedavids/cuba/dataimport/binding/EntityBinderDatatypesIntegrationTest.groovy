package de.diedavids.cuba.dataimport.binding

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
    void "bindAttributes binds a string attribute"() {

        ImportData importData = createData([
                [name: "Simpson", height: 120]
        ])

        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getName()).isEqualTo("Simpson")
    }

    @Test
    void "bindAttributes binds an integer attribute"() {

        ImportData importData = createData([
                [name: "Simpson", height: 120]
        ])

        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getHeight()).isEqualTo(120)
    }

    @Test
    void "bindAttributes binds a double attribute"() {

        ImportData importData = createData([
                [age: "22.5"]
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
    void "bindAttributes binds a boolean attribute"() {

        ImportData importData = createData([
                [leftHanded: "true"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ]
        )
        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getLeftHanded()).isTrue()
    }

    @Test
    void "bindAttributes binds a boolean attribute with a custom booleanTrueValue definition"() {

        ImportData importData = createData([
                [leftHanded: "Yes"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                booleanTrueValue: "Yes",
                booleanFalseValue: "No",
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ]
        )
        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getLeftHanded()).isTrue()
    }



    @Test
    void "bindAttributes binds a boolean attribute with an empty custom booleanTrueValue definition (case - true: '')"() {

        ImportData importData = createData([
                [leftHanded: ""]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                booleanTrueValue: "",
                booleanFalseValue: "No",
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ]
        )
        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getLeftHanded()).isTrue()
    }


    @Test
    void "bindAttributes binds a boolean attribute with an empty custom booleanTrueValue definition (case - false: 'No')"() {

        ImportData importData = createData([
                [leftHanded: "No"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                booleanTrueValue: "",
                booleanFalseValue: "No",
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ]
        )
        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getLeftHanded()).isFalse()
    }

    @Test
    void "bindAttributes binds a boolean attribute with with wrong case"() {

        ImportData importData = createData([
                [leftHanded: "yes"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                booleanTrueValue: "Yes",
                booleanFalseValue: "No",
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ]
        )
        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getLeftHanded()).isTrue()
    }


    @Test
    void "bindAttributes binds a boolean attribute with a custom booleanFalseValue definition"() {

        ImportData importData = createData([
                [leftHanded: "No"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                booleanTrueValue: "Yes",
                booleanFalseValue: "No",
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ]
        )
        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getLeftHanded()).isFalse()
    }



    @Test
    void "bindAttributes binds a boolean attribute with an empty custom booleanFalseValue definition (case - true: 'X')"() {

        ImportData importData = createData([
                [leftHanded: "X"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                booleanTrueValue: "X",
                booleanFalseValue: "",
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ]
        )
        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getLeftHanded()).isTrue()
    }


    @Test
    void "bindAttributes binds a boolean attribute with an empty custom booleanFalseValue definition (case - false: '')"() {

        ImportData importData = createData([
                [leftHanded: ""]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                booleanTrueValue: "X",
                booleanFalseValue: "",
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ]
        )
        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getLeftHanded()).isFalse()
    }

    @Test
    void "bindAttributes binds a boolean attribute not if no custom value matches"() {

        ImportData importData = createData([
                [leftHanded: "X"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                booleanTrueValue: "Yes",
                booleanFalseValue: "No",
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ]
        )
        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getLeftHanded()).isNull()
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
    void "bindAttributes binds an enum value"() {

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