package de.diedavids.cuba.dataimport.dto

import de.diedavids.cuba.dataimport.converter.CsvImportDataConverter
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import spock.lang.Specification

class ImportDataImplSpec extends Specification {

    ImportDataImpl sut


    def "ImportData is compatible with attribute mappers, if all headers match an entry in the attribute mappers fileColumnAlias"() {

        given:

        ImportData sut = new CsvImportDataConverter().convert('''name,lastname
Mark,Andersson
Pete,Hansen''')

        def attributeMappers = [
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'lastname', fileColumnAlias: 'lastname', fileColumnNumber: 1),
        ]

        expect:
        sut.isCompatibleWith(attributeMappers)
    }

    def "ImportData is incompatible with attribute mappers, if one header does not match any entry in the attribute mappers fileColumnAlias"() {

        given:

        ImportData sut = new CsvImportDataConverter().convert('''name,lastname
Mark,Andersson
Pete,Hansen''')

        def attributeMappers = [
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 0),
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'lastname', fileColumnAlias: 'firstName', fileColumnNumber: 1),
        ]

        expect:
        !sut.isCompatibleWith(attributeMappers)
    }


    def "ImportData is incompatible with attribute mappers, if one header does not match any entry in the attribute mappers fileColumnNumber"() {

        given:

        ImportData sut = new CsvImportDataConverter().convert('''name,lastname
Mark,Andersson
Pete,Hansen''')

        def attributeMappers = [
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'lastname', fileColumnAlias: 'lastname', fileColumnNumber: 0),
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name', fileColumnAlias: 'name', fileColumnNumber: 1),
        ]

        expect:
        !sut.isCompatibleWith(attributeMappers)
    }
}
