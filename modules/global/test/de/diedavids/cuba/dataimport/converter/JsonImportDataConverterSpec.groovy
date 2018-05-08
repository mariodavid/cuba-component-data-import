package de.diedavids.cuba.dataimport.converter

import de.diedavids.cuba.dataimport.dto.ImportData
import spock.lang.Specification

class JsonImportDataConverterSpec extends Specification {
    private sut
    private String JSON_INPUT = '''[
  {
    "Name": "Mark",
    "Lastname": "Andersson",
    "Address": {
        "street": "Dorfkrug 1",
        "postcode": "51665",
        "city": "Bad Neuendorf"
    },
    "orders": [
        {
            "orderId": 1
        },
        {
            "orderId": 2
        }
    ]
  },
  {
    "Name": "Pete",
    "Lastname": "Hansen",
    "Address": {
        "street": "Meiereiweg 16",
        "postcode": "87616",
        "city": "Homburg"
    },
    "orders": [
        {
            "orderId": 3
        },
        {
            "orderId": 4
        }
    ]
  }
]'''


    void setup() {
        sut = new JsonImportDataConverter()
    }

    def "convert contains the correct amount of columns"() {

        when:
        ImportData result = sut.convert(JSON_INPUT)
        then:
        result.columns.size() == 4
    }

    def "convert contains two DataRows"() {
        when:
        ImportData result = sut.convert(JSON_INPUT)
        then:
        result.rows.size() == 2
    }

    def "convert contains the correct values for the DataRows"() {
        when:
        ImportData result = sut.convert(JSON_INPUT)
        then:
        result.rows[0].Name == 'Mark'
        result.rows[0].Lastname == 'Andersson'
    }

    def "convert enables access to nested JSON sturcture"() {
        when:
        ImportData result = sut.convert(JSON_INPUT)
        then:
        result.rows[0].Address
        result.rows[0].Address.street == "Dorfkrug 1"
        result.rows[0].Address.postcode == "51665"
        result.rows[0].Address.city == "Bad Neuendorf"
    }

    def "convert enables access to nested JSON Array"() {
        when:
        ImportData result = sut.convert(JSON_INPUT)
        then:
        result.rows[0].orders
        result.rows[0].orders.size() == 2
        result.rows[0].orders[0].orderId == 1
    }
}
