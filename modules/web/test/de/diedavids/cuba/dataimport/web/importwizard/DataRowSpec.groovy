package de.diedavids.cuba.dataimport.web.importwizard

import com.haulmont.cuba.core.entity.KeyValueEntity
import spock.lang.Specification

class DataRowSpec extends Specification {


    def "toKeyValueEntity creates a KV Entity with one column out of the DataRow"() {
        given:
        def sut = new DataRow(
                columns: [
                        "name": 0,
                ],
                values: ["david"]
        )

        when:
        KeyValueEntity result = sut.toKevValueEntity()

        then:
        result.getValue("name") == "david"
    }

    def "toKeyValueEntity creates a KV Entity with two columns out of the DataRow"() {
        given:
        def sut = new DataRow(
                columns: [
                        "name": 0,
                        "firstName": 1,
                ],
                values: ["meier", "mario"]
        )

        when:
        KeyValueEntity result = sut.toKevValueEntity()

        then:
        result.getValue("name") == "meier"
        result.getValue("firstName") == "mario"
    }


    def "ofMap will create a DataRow out of a Map"() {

        when:
        DataRow sut = DataRow.ofMap([
                "name": "meier",
                "firstName": "mario",
        ])

        then:
        sut.name == "meier"
        sut.firstName == "mario"
    }
}
