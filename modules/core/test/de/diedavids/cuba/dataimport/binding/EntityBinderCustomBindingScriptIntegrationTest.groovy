package de.diedavids.cuba.dataimport.binding

import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeMapperMode
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbPlayer
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat;


class EntityBinderCustomBindingScriptIntegrationTest extends AbstractEntityBinderIntegrationTest {

    @Test
    void "bindAttributes executes the custom binding script if set for a attribute mapper"() {

        ImportData importData = createData([
                [name: "Simpson"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(
                                attributeType: AttributeType.DIRECT_ATTRIBUTE,
                                entityAttribute: 'name',
                                fileColumnAlias: 'name',
                                fileColumnNumber: 3,

                                mapperMode: AttributeMapperMode.CUSTOM,
                                customAttributeBindScript: "return 'NameFromCustomScript'"
                        ),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getName()).isEqualTo("NameFromCustomScript")
    }


    @Test
    void "bindAttributes ignores the custom binding script if set for a attribute mapper but the mapper type is AUTO"() {

        ImportData importData = createData([
                [name: "Simpson"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(
                                attributeType: AttributeType.DIRECT_ATTRIBUTE,
                                entityAttribute: 'name',
                                fileColumnAlias: 'name',
                                fileColumnNumber: 3,


                                mapperMode: AttributeMapperMode.AUTOMATIC,
                                customAttributeBindScript: "return 'NameFromCustomScript'"
                        ),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getName()).isEqualTo("Simpson")
    }


    @Test
    void "custom groovy script has access to the dataManager bean"() {

        ImportData importData = createData([
                [name: "Simpson"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(
                                attributeType: AttributeType.DIRECT_ATTRIBUTE,
                                entityAttribute: 'name',
                                fileColumnAlias: 'name',
                                fileColumnNumber: 3,

                                mapperMode: AttributeMapperMode.CUSTOM,
                                customAttributeBindScript: """
if (dataManager && dataManager instanceof com.haulmont.cuba.core.global.DataManager) {
    return "dataManager is available"
}
else {
    return "dataManager is not available"
}
"""
                        ),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer


        assertThat(entity.getName()).isEqualTo("dataManager is available")
    }


    @Test
    void "custom groovy script has access to the rawValue"() {

        ImportData importData = createData([
                [name: "Simpson"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(
                                attributeType: AttributeType.DIRECT_ATTRIBUTE,
                                entityAttribute: 'name',
                                fileColumnAlias: 'name',
                                fileColumnNumber: 3,

                                mapperMode: AttributeMapperMode.CUSTOM,
                                customAttributeBindScript: "return rawValue + '-custom'"
                        ),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )
        MlbPlayer entity = sut.bindAttributesToEntity(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer


        assertThat(entity.getName()).isEqualTo("Simpson-custom")
    }

}