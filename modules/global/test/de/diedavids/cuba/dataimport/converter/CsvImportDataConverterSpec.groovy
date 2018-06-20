package de.diedavids.cuba.dataimport.converter

import de.diedavids.cuba.dataimport.dto.ImportData
import spock.lang.Specification

class CsvImportDataConverterSpec extends Specification {

    def "convert contains the correct amount of columns"() {
        given:
        def sut = new CsvImportDataConverter()
        when:
        ImportData result = sut.convert('''Name,Lastname
Mark,Andersson
Pete,Hansen''')
        then:
        result.columns.size() == 2
    }

    def "convert contains two DataRows"() {
        given:
        def sut = new CsvImportDataConverter()
        when:
        ImportData result = sut.convert('''Name,Lastname
Mark,Andersson
Pete,Hansen''')
        then:
        result.rows.size() == 2
    }

    def "convert removes empty DataRows"() {
        given:
        def sut = new CsvImportDataConverter()
        when:
        ImportData result = sut.convert('''Name,Lastname
Mark,Andersson
,
Pete,Hansen''')
        then:
        result.rows.size() == 2
    }

    def "convert contains the correct values for the DataRows"() {
        given:
        def sut = new CsvImportDataConverter()
        when:
        ImportData result = sut.convert('''Name,Lastname
Mark,Andersson''')
        then:
        result.rows[0].Name == 'Mark'
        result.rows[0].Lastname == 'Andersson'
    }
}
