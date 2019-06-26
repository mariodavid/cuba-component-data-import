package de.diedavids.cuba.dataimport.converter

import de.diedavids.cuba.dataimport.dto.ImportData
import spock.lang.Specification

class ExcelImportDataConverterSpec extends Specification {

    private sut = new ExcelImportDataConverter()

    /**
     * customer-list.xlsx

     Customer ID	Customer Name	    Segment	    Country	        City	    State	    Postal Code     Region
     CG-12520	    Claire Gute	        Consumer	United States	Henderson	Kentucky	42420	        South
     CG-12520	    Claire Gute	        Consumer	United States	Henderson	Kentucky	42420	        South

     */

    def "convert contains the correct amount of columns"() {

        when:
        ImportData result = sut.convert(customerList, "UTF-8")
        then:
        result.columns.size() == 8
    }


    def "convert contains two DataRows"() {
        when:
        ImportData result = sut.convert(customerList, "UTF-8")
        then:
        result.rows.size() == 2
    }

    def "convert removes empty DataRows"() {
        when:
        ImportData result = sut.convert(customerListWithEmptyRows, "UTF-8")

        then: 'the file contains 11 rows but only 9 contain content'
        result.rows.size() == 9
    }

    def "convert contains the correct values for the DataRows"() {
        when:
        ImportData result = sut.convert(customerList, "UTF-8")
        then:
        result.rows[0]['customer id'] == 'CG-12520'
        result.rows[0]['customer name'] == 'Claire Gute'
        result.rows[0]['segment'] == 'Consumer'
    }

    private File getCustomerList() {
        getExcelFile("customer-list.xlsx")
    }

    private File getExcelFile(String filename) {
        def resource = this.getClass().getResource("/de/diedavids/cuba/dataimport/converter/examples/$filename")
        new File(resource.getFile())
    }

    private File getCustomerListWithEmptyRows() {
        getExcelFile("customer-list-with-empty-rows-at-the-bottom.xlsx")
    }
}
