package de.diedavids.cuba.dataimport.converter

import de.diedavids.cuba.dataimport.dto.ImportData
import spock.lang.Specification

class JsonImportDataConverterSpec extends Specification {
    private sut
    private String JSON_STRING = '''[
  {
    "Name": "Mark",
    "Lastname": "Andersson"
  },
  {
    "Name": "Pete",
    "Lastname": "Hansen"
  }
]'''


    void setup() {
        sut = new JsonImportDataConverter()
    }

    def "convert contains the correct amount of columns"() {

        when:
        ImportData result = sut.convert(JSON_STRING)
        then:
        result.columns.size() == 2
    }

    def "convert contains two DataRows"() {
        when:
        ImportData result = sut.convert(JSON_STRING)
        then:
        result.rows.size() == 2
    }

    def "convert contains the correct values for the DataRows"() {
        when:
        ImportData result = sut.convert(JSON_STRING)
        then:
        result.rows[0].Name == 'Mark'
        result.rows[0].Lastname == 'Andersson'
    }
}
