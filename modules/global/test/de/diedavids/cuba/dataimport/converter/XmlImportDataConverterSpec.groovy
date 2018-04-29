package de.diedavids.cuba.dataimport.converter

import de.diedavids.cuba.dataimport.dto.ImportData
import spock.lang.Specification

class XmlImportDataConverterSpec extends Specification {
    private sut
    private String INPUT_STRING = '''<root>
    <entry>
        <Name>Users</Name>
        <Description>The users of the system</Description>
    </entry>
    <entry>
        <Name>Moderators</Name>
        <Description>The mods of the system</Description>
    </entry>
</root>'''

    void setup() {
        sut = new XmlImportDataConverter()
    }

    def "convert contains the correct amount of columns"() {
        when:
        ImportData result = this.sut.convert(INPUT_STRING)
        then:
        result.columns.size() == 2
    }

    def "convert contains two DataRows"() {
        when:
        ImportData result = sut.convert(INPUT_STRING)
        then:
        result.rows.size() == 2
    }

    def "convert contains the correct values for the DataRows"() {
        when:
        ImportData result = sut.convert(INPUT_STRING)
        then:
        result.rows[0].Name == 'Users'
        result.rows[0].Description == 'The users of the system'
    }
}
