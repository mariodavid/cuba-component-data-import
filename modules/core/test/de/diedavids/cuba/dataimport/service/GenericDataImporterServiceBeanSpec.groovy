package de.diedavids.cuba.dataimport.service

import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.Metadata
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.dto.DataRowImpl
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.dto.ImportDataImpl
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportTransactionStrategy
import de.diedavids.cuba.dataimport.entity.example.mlb.MlbPlayer
import de.diedavids.cuba.dataimport.binding.EntityBinder
import spock.lang.Specification

class GenericDataImporterServiceBeanSpec extends Specification {

    private Metadata metadata = Mock(Metadata)
    private GenericDataImporterServiceBean sut


    EntityBinder dataImportEntityBinder = Mock(EntityBinder)
    void setup() {

        sut = new GenericDataImporterServiceBean(
                metadata: metadata,
                dataImportEntityBinder: dataImportEntityBinder
        )

        metadata.create('ddcdi$MlbPlayer') >> new MlbPlayer()

        dataImportEntityBinder.bindAttributesToEntity(_,_,_,_) >> { ImportConfiguration importConfiguration, DataRow dataRow, Entity entity, Map<String, Object> defaultValues -> return entity }
    }

    def "createEntities creates an entity for every row in the import data"() {
        given:
        ImportDataImpl importData = createData([
                [name: "Simpson", team: "NY Yankees"],
                [name: "Krababbel", team: "NY Yankees"]
        ])

        and:
        ImportConfiguration importConfiguration = new ImportConfiguration(
                entityClass: 'ddcdi$MlbPlayer',
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
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
                entityClass: 'ddcdi$MlbPlayer',
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )

        when:
        def result = sut.createEntities(importConfiguration, importData)

        then:
        result[0].entity instanceof MlbPlayer
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
                        new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                ],
                transactionStrategy: ImportTransactionStrategy.SINGLE_TRANSACTION
        )


        when:
        sut.createEntities(importConfiguration, importData)[0].entity as MlbPlayer

        then:
        1 * dataImportEntityBinder.bindAttributesToEntity(importConfiguration, importData.rows[0], _, _)
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
