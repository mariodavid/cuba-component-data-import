package de.diedavids.cuba.dataimport.binding

import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy
import de.diedavids.cuba.dataimport.entity.example.sales.Customer
import de.diedavids.cuba.dataimport.entity.example.sales.CustomerPriority
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbPlayer
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbTeam
import de.diedavids.cuba.dataimport.entity.example.mlb.State
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat;


class EntityBinderDatatypesIntegrationTest extends AbstractEntityBinderIntegrationTest{


    @Test
    void "bindAttributes binds a string attribute"() {

        ImportData importData = createData([
                [name: "Simpson", height: 120]
        ])

        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getName()).isEqualTo("Simpson")
    }

    @Test
    void "bindAttributes binds an integer attribute"() {

        ImportData importData = createData([
                [name: "Simpson", height: 120]
        ])

        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

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
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'age', fileColumnAlias: 'age', fileColumnNumber: 3),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getAge()).isEqualTo(22.5d)
    }

    @Test
    void "bindAttributes binds a BigDecimal attribute"() {

        ImportData importData = createData([
                [annualSalary: "10"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'annualSalary', fileColumnAlias: 'annualSalary'),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getAnnualSalary()).isEqualTo(BigDecimal.TEN)
    }

    @Test
    void "bindAttributes binds a boolean attribute"() {

        ImportData importData = createData([
                [leftHanded: "true"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

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
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

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
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

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
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

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
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

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
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

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
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

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
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

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
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'leftHanded', fileColumnAlias: 'leftHanded', fileColumnNumber: 0),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

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
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'age', fileColumnAlias: 'age', fileColumnNumber: 3),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

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
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'lastname', fileColumnNumber: 0)
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )

        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

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
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'state', fileColumnAlias: 'state'),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbTeam entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbTeam()) as MlbTeam

        assertThat(entity.getState()).isEqualTo(State.AL)
    }

    @Test
    void "bindAttributes binds an integer based (id) enum value"() {

        ImportData importData = createData([
                [priority: "HIGH"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$Customer',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'ddcdi$Customer.priority', fileColumnAlias: 'priority'),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        Customer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new Customer()) as Customer

        assertThat(entity.getPriority()).isEqualTo(CustomerPriority.HIGH)
    }

    @Test
    void "bindAttributes binds enum regardless of the case"() {

        ImportData importData = createData([
                [priority: "high"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$Customer',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'ddcdi$Customer.priority', fileColumnAlias: 'priority'),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        Customer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new Customer()) as Customer

        assertThat(entity.getPriority()).isEqualTo(CustomerPriority.HIGH)
    }

    @Test
    void "bindAttributes binds nothing in case of enum and null value"() {

        ImportData importData = createData([
                [priority: ""]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$Customer',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'ddcdi$Customer.priority', fileColumnAlias: 'priority'),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        Customer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new Customer()) as Customer

        assertThat(entity.getPriority()).isNull()
    }


    @Test
    void "bindAttributes binds nothing in case of wrong enum value"() {

        ImportData importData = createData([
                [priority: "MIDDLE"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$Customer',
                importAttributeMappers: [
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'ddcdi$Customer.priority', fileColumnAlias: 'priority'),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        Customer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new Customer()) as Customer

        assertThat(entity.getPriority()).isNull()
    }


}