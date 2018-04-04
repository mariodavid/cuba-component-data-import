package de.diedavids.cuba.dataimport.core.xls

import com.haulmont.cuba.core.global.Metadata
import de.diedavids.cuba.dataimport.dto.DataRowImpl
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.dto.ImportDataImpl
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.example.MlbPlayer
import spock.lang.Specification

class GenericImportConfigurationImporterSpec extends Specification {
    private Metadata metadata = Mock(Metadata)
    private GenericImportConfigurationImporter sut

    void setup() {

        sut = new GenericImportConfigurationImporter(
                metadata: metadata
        )

        metadata.create('ddcdi$MlbPlayer') >> new MlbPlayer()
    }

    def "createEntities creates an entity for every row in the import data"() {
        given:
        ImportDataImpl importData = createData([
                [name: "Simpson", team: "NY Yankees"],
                [name: "Krababbel", team: "NY Yankees"]
        ])

        and:
        ImportConfiguration importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer'
        )

        when:
        def result = sut.createEntities(importConfiguration, importData)

        then:
        result.size() == 2
    }

    def "createEntities creates an entity of the correct type as defined in the import configuration"() {
        given:
        ImportDataImpl importData = createData([
                [name: "Simpson", team: "NY Yankees"]
        ])

        and:
        ImportConfiguration importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer'
        )

        when:
        def result = sut.createEntities(importConfiguration, importData)

        then:
        result[0] instanceof MlbPlayer
    }


    def "createEntities binds the attributes of the data row to the entity instance"() {
        given:
        ImportDataImpl importData = createData([
                [name: "Simpson", team: "NY Yankees", height: 120]
        ])



        and:
        ImportConfiguration importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                importAttributeMappers: [
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.name', fileColumnAlias: 'name', fileColumnNumber: 0),
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.team', fileColumnAlias: 'team', fileColumnNumber: 1),
                        new ImportAttributeMapper(entityAttribute: 'ddcdi$MlbPlayer.height', fileColumnAlias: 'height', fileColumnNumber: 2),
                ]
        )


        when:
        MlbPlayer mlbPlayer = sut.createEntities(importConfiguration, importData)[0] as MlbPlayer

        then:
        mlbPlayer.name == "Simpson"
        mlbPlayer.team == "NY Yankees"
        mlbPlayer.height == 120
    }

    private ImportData createData(List<Map<String, Object>> data) {
        ImportData importData = new ImportDataImpl()

        data.each {
            def dataRow = DataRowImpl.ofMap(it)
            importData.rows.add(dataRow)
        }

        importData
    }
}
