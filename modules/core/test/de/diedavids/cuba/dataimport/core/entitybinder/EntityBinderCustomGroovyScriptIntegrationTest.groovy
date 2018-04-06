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


class EntityBinderCustomGroovyScriptIntegrationTest extends AbstractEntityBinderIntegrationTest {

    @Test
    void "bindAttributes executes the custom groovy script if set for a attribute mapper"() {

        ImportData importData = createData([
                [name: "Simpson"]
        ])

        importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(
                                entityAttribute: 'ddcdi$MlbPlayer.name',
                                fileColumnAlias: 'name',
                                fileColumnNumber: 3,
                                customAttributeBindScript: "return 'NameFromCustomScript'"
                        ),
                ]
        )
        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer

        assertThat(entity.getName()).isEqualTo("NameFromCustomScript")
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
                                entityAttribute: 'ddcdi$MlbPlayer.name',
                                fileColumnAlias: 'name',
                                fileColumnNumber: 3,
                                customAttributeBindScript: """
if (dataManager && dataManager instanceof com.haulmont.cuba.core.global.DataManager) {
    return "dataManager is available"
}
else {
    return "dataManager is not available"
}
"""
                        ),
                ]
        )
        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer


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
                                entityAttribute: 'ddcdi$MlbPlayer.name',
                                fileColumnAlias: 'name',
                                fileColumnNumber: 3,
                                customAttributeBindScript: "return rawValue + '-custom'"
                        ),
                ]
        )
        MlbPlayer entity = sut.bindAttributes(importConfiguration, importData.rows[0], new MlbPlayer()) as MlbPlayer


        assertThat(entity.getName()).isEqualTo("Simpson-custom")
    }

}