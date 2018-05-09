package de.diedavids.cuba.dataimport.converter

import de.diedavids.cuba.dataimport.dto.ImportData
import spock.lang.Specification

class XmlImportDataConverterSpec extends Specification {
    private sut
    private String XML_INPUT = '''<root>
    <entry>
        <Name>Users</Name>
        <Description>The users of the system</Description>
        <permission>
            <code>ALLOW_EVERYTHING</code>
            <name>Allow everything</name>
        </permission>
    </entry>
    <entry>
        <Name>Moderators</Name>
        <Description>The mods of the system</Description>
        <permission>
            <code>DENY_ALL</code>
            <name>Nothing is allowed</name>
        </permission>
    </entry>
</root>'''

    void setup() {
        sut = new XmlImportDataConverter()
    }

    def "convert contains the correct amount of columns"() {
        when:
        ImportData result = this.sut.convert(XML_INPUT)
        then:
        result.columns.size() == 3
    }

    def "convert contains two DataRows"() {
        when:
        ImportData result = sut.convert(XML_INPUT)
        then:
        result.rows.size() == 2
    }

    def "convert contains the correct values for the DataRows"() {
        when:
        ImportData result = sut.convert(XML_INPUT)
        then:
        result.rows[0].Name == 'Users'
        result.rows[0].Description == 'The users of the system'
    }

    def "convert enables access to nested XML sturcture"() {
        when:
        ImportData result = sut.convert(XML_INPUT)
        then:
        result.rows[0].permission.code == "ALLOW_EVERYTHING"
        result.rows[0].permission.name == "Allow everything"
    }
}
