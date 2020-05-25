package de.diedavids.cuba.dataimport.dto

import de.diedavids.cuba.dataimport.converter.CsvImportDataConverter
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeType
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import spock.lang.Specification

class ImportDataImplSpec extends Specification {

    ImportDataImpl sut


    def "ImportData is compatible with attribute mappers, if all headers match an entry in the required attribute mappers fileColumnAlias"() {

        given:

        ImportData sut = new CsvImportDataConverter().convert('''name,lastname
Mark,Andersson
Pete,Hansen''')

        def attributeMappers = [
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name',
                        fileColumnAlias: 'name', fileColumnNumber: 0, isRequiredColumn: true),
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'lastname',
                        fileColumnAlias: 'lastname', fileColumnNumber: 1, isRequiredColumn: true),
        ]

        expect:
        sut.isCompatibleWith(attributeMappers).isValid()
    }

    def "ImportData is incompatible with attribute mappers, if one header does not match any entry in the required attribute mappers fileColumnAlias"() {

        given:

        ImportData sut = new CsvImportDataConverter().convert('''name,lastname
Mark,Andersson
Pete,Hansen''')

        def attributeMappers = [
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name',
                        fileColumnAlias: 'name', fileColumnNumber: 0, isRequiredColumn: true),
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'lastname',
                        fileColumnAlias: 'firstName', fileColumnNumber: 1, isRequiredColumn: true),
        ]

        expect:
        !sut.isCompatibleWith(attributeMappers).isValid()
    }


    def "ImportData is compatible with attribute mappers, even if one header does not match any entry in the required attribute mappers fileColumnNumber"() {

        given:

        ImportData sut = new CsvImportDataConverter().convert('''name,lastname
Mark,Andersson
Pete,Hansen''')

        def attributeMappers = [
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'lastname',
                        fileColumnAlias: 'lastname', fileColumnNumber: 0, isRequiredColumn: true),
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name',
                        fileColumnAlias: 'name', fileColumnNumber: 1, isRequiredColumn: true),
        ]

        expect:
        sut.isCompatibleWith(attributeMappers).isValid()
    }

    def "ImportData is compatible with attribute mappers, if one header does not match any entry in the not required attribute mappers fileColumnAlias"() {

        given:

        ImportData sut = new CsvImportDataConverter().convert('''name,lastname
Mark,Andersson
Pete,Hansen''')

        def attributeMappers = [
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name',
                        fileColumnAlias: 'name', fileColumnNumber: 0, isRequiredColumn: true),
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'lastname',
                        fileColumnAlias: 'firstname', fileColumnNumber: 1, isRequiredColumn: false),
        ]

        expect:
        sut.isCompatibleWith(attributeMappers).isValid()
    }

    def "ImportData is compatible with attribute mappers, if header is missing for not required attribute mappers fileColumnAlias"() {

        given:

        ImportData sut = new CsvImportDataConverter().convert('''name,lastname
Mark,Andersson
Pete,Hansen''')

        def attributeMappers = [
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'name',
                        fileColumnAlias: 'name', fileColumnNumber: 0, isRequiredColumn: true),
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'firstname',
                        fileColumnAlias: 'firstname', fileColumnNumber: 1, isRequiredColumn: false),
                new ImportAttributeMapper(attributeType: AttributeType.DIRECT_ATTRIBUTE, entityAttribute: 'lastname',
                        fileColumnAlias: 'lastname', fileColumnNumber: 2, isRequiredColumn: true),
        ]

        expect:
        sut.isCompatibleWith(attributeMappers).isValid()
    }
}
