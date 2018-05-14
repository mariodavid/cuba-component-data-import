package de.diedavids.cuba.dataimport.data

import spock.lang.Specification

class EntityAttributeValueFactorySpec extends Specification {


    def "ofMap returns a list of EntityAttributeValue elements for each key in the map"() {
        given:
        def sut = new EntityAttributeValueFactory()

        when:
        def entityAttributeValues = sut.ofMap(name: 'testname', description: 'test description')

        then:
        entityAttributeValues.size() == 2

        and:
        entityAttributeValues[0].entityAttribute == 'name'
        entityAttributeValues[0].value == 'testname'

        and:
        entityAttributeValues[1].entityAttribute == 'description'
        entityAttributeValues[1].value == 'test description'
    }

}
