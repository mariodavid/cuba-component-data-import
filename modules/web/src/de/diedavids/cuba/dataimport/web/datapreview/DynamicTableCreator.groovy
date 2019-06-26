package de.diedavids.cuba.dataimport.web.datapreview

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.BoxLayout
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.data.DsBuilder
import com.haulmont.cuba.gui.data.DsContext
import com.haulmont.cuba.gui.data.impl.ValueCollectionDatasourceImpl
import de.diedavids.cuba.dataimport.dto.ImportData

class DynamicTableCreator {

    UiComponents uiComponents

    Frame frame

    DsContext dsContext


    Table createTable(ImportData importData, BoxLayout tableWrapper) {
        ValueCollectionDatasourceImpl tableDs = createDatasource(importData)
        createDynamicTable(tableDs, tableWrapper)
    }


    private ValueCollectionDatasourceImpl createDatasource(ImportData importData) {
        ValueCollectionDatasourceImpl tableDs = createValueCollectionDs()
        importData.rows.each { tableDs.includeItem(it.toKevValueEntity()) }
        importData.columns.each { tableDs.addProperty(it) }
        tableDs
    }

    protected ValueCollectionDatasourceImpl createValueCollectionDs() {
        createDsBuilder()
                .reset()
                .setAllowCommit(false)
                .buildValuesCollectionDatasource()
    }

    protected DsBuilder createDsBuilder() {
        DsBuilder.create(dsContext)
    }

    private Table createDynamicTable(ValueCollectionDatasourceImpl tableDs, BoxLayout tableWrapper) {
        tableWrapper.removeAll()

        Table table = uiComponents.create(Table)
        table.frame = frame

        addTableColumns(tableDs, table)

        table.datasource = tableDs
        table.setSizeFull()
        table.setSortable(false)
        table.setContextMenuEnabled(false)
        tableWrapper.add(table)

        table
    }


    private void addTableColumns(ValueCollectionDatasourceImpl tableDs, Table table) {
        MetaClass meta = tableDs.metaClass
        for (MetaProperty metaProperty : meta.properties) {
            Table.Column column = new Table.Column(meta.getPropertyPath(metaProperty.name))
            column.caption = metaProperty.name
            table.addColumn(column)
        }
    }
}