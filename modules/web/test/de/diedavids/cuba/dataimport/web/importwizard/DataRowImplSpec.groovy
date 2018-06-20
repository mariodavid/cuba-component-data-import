package de.diedavids.cuba.dataimport.web.importwizard

import com.haulmont.cuba.core.entity.KeyValueEntity
import de.diedavids.cuba.dataimport.dto.DataRow
import de.diedavids.cuba.dataimport.dto.DataRowImpl
import spock.lang.Specification

class DataRowImplSpec extends Specification {


    def "toKeyValueEntity creates a KV Entity with one column out of the DataRow"() {
        given:
        def sut = new DataRowImpl(
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
        def sut = new DataRowImpl(
                columns: [
                        "name"     : 0,
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
        DataRow sut = DataRowImpl.ofMap([
                "name"     : "meier",
                "firstName": "mario",
        ])

        then:
        sut.name == "meier"
        sut.firstName == "mario"
    }

    def "isEmpty is true if there are no keys in it"() {

        given:
        DataRow sut = DataRowImpl.ofMap(input)

        expect:
        isEmpty == sut.isEmpty()

        where:
        input                     || isEmpty
        [:]                       || true
        [empty: '']               || true
        [empty: '', empty2: null] || true
        [foo: 'bar']              || false
        [empty: '', foo: 'bar']   || false
    }
}
