package de.diedavids.cuba.dataimport.web.importwizard

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.cuba.gui.components.BoxLayout
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.data.DsBuilder
import com.haulmont.cuba.gui.data.DsContext
import com.haulmont.cuba.gui.data.impl.ValueCollectionDatasourceImpl
import com.haulmont.cuba.web.gui.WebComponentsFactory
import spock.lang.Specification

class DynamicTableCreatorSpec extends Specification {


    def "createTable will add a column for every meta properties in the datasource"() {

        given:
        def componentsFactory = Mock(WebComponentsFactory)
        def mockTable = Mock(Table)
        componentsFactory.createComponent(Table) >> mockTable

        and:
        def mockDatasource = Mock(ValueCollectionDatasourceImpl)

        def mockTableMetaClasss = Mock(MetaClass)
        mockTableMetaClasss.getProperties() >> [
                Mock(MetaProperty),
                Mock(MetaProperty),
        ]
        mockDatasource.getMetaClass() >> mockTableMetaClasss


        and:
        def sut = new MockableDynamicTableCreator(
                componentsFactory: componentsFactory,
                dsContext: Mock(DsContext),
                frame: Mock(Frame),
                mockDatasource: mockDatasource
        )

        when:
        sut.createTable(new TableData(
                columns: ['name', 'firstName'],
                rows: [
                        DataRow.ofMap(name: "David", "firstName": "mario"),
                        DataRow.ofMap(name: "Kleister", "firstName": "Kleiner"),
                ]
        ), Mock(BoxLayout))


        then:
        2 * mockTable.addColumn(_)
    }
}


class MockableDynamicTableCreator extends DynamicTableCreator {

    ValueCollectionDatasourceImpl mockDatasource

    @Override
    protected ValueCollectionDatasourceImpl createValueCollectionDs() {
        mockDatasource
    }
}