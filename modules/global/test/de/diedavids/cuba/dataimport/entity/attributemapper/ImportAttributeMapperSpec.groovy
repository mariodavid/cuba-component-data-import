package de.diedavids.cuba.dataimport.entity.attributemapper

import spock.lang.Specification

class ImportAttributeMapperSpec extends Specification {

    def "A custom ImportAttributeMapper is bindable if there is an entity attribute"() {
        given:
        def sut = new ImportAttributeMapper(
                mapperMode: AttributeMapperMode.CUSTOM,
                entityAttribute: "myAttribute"
        )
        expect:
        sut.bindable
    }

    def "A custom ImportAttributeMapper is not bindable if there is no entity attribute"() {
        given:
        def sut = new ImportAttributeMapper(
                mapperMode: AttributeMapperMode.CUSTOM,
                entityAttribute: null
        )
        expect:
        !sut.bindable
    }

    def "An automatic ImportAttributeMapper is not bindable if there is an entity attribute and a attributeType"() {
        given:
        def sut = new ImportAttributeMapper(
                mapperMode: AttributeMapperMode.CUSTOM,
                entityAttribute: "myAttribute",
                attributeType: AttributeType.DIRECT_ATTRIBUTE
        )
        expect:
        sut.bindable
    }
}
