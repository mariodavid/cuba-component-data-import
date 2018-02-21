package de.diedavids.cuba.dataimport.web.importwizard

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.data.DsBuilder
import com.haulmont.cuba.gui.data.DsContext
import com.haulmont.cuba.gui.data.impl.ValueCollectionDatasourceImpl
import com.haulmont.cuba.gui.upload.FileUploadingAPI
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import com.xlson.groovycsv.CsvParser
import com.xlson.groovycsv.PropertyMapper

import javax.inject.Inject

class ImportWizard extends AbstractWindow {

    public static final String WIZARD_STEP_2 = 'step2'
    public static final String WIZARD_STEP_1 = 'step1'
    public static final String WIZARD_STEP_3 = 'step3'

    @Inject Accordion wizardAccordion
    @Inject Button closeWizard

    @Inject FileUploadField importFileUploadBtn
    @Inject FileUploadingAPI fileUploadingAPI

    @Inject ComponentsFactory componentsFactory

    Table resultTable


    @Inject
    protected BoxLayout resultTableBox
    private final static int MAX_ROWS_IMPORT_PREVIEW = 25


    @Override
    void init(Map<String, Object> params) {

        initUploadFileSucceedListener()
        initUploadFileErrorListener()
    }

    protected initUploadFileSucceedListener() {
        importFileUploadBtn.addFileUploadSucceedListener(new FileUploadField.FileUploadSucceedListener() {
            @Override
            void fileUploadSucceed(FileUploadField.FileUploadSucceedEvent e) {
                File file = fileUploadingAPI.getFile(importFileUploadBtn.fileId)


                wizardAccordion.getTab(WIZARD_STEP_2).enabled = true
                wizardAccordion.selectedTab = WIZARD_STEP_2

                Accordion.Tab step1 = wizardAccordion.getTab(WIZARD_STEP_1)
                step1.caption = "${step1.caption} $check"
                step1.enabled = false

                importFile(file)
            }
        })
    }




    void importFile(File file) {


        TableData tableData = parseFile(file)


        def dynamicTableCreator = new DynamicTableCreator(
                dsContext: dsContext,
                frame: frame,
                componentsFactory: componentsFactory
        )

        dynamicTableCreator.createTable(tableData, resultTableBox)

        /*
        ValueCollectionDatasourceImpl sqlResultDs = createDatasource(result)
        createResultTable(sqlResultDs)
        */

    }

    private TableData parseFile(File file) {
        def csvRows = new CsvParser().parse(file.text)

        def tableData = new TableData()
        int i = 0
        csvRows.each { PropertyMapper row ->
            if (i < MAX_ROWS_IMPORT_PREVIEW) {
                tableData.columns = row.columns.keySet()
                tableData.rows << DataRow.ofMap(row.toMap())
            }
            i++
        }
        tableData
    }

    protected initUploadFileErrorListener() {
        importFileUploadBtn.addFileUploadErrorListener(new UploadField.FileUploadErrorListener() {
            @Override
            void fileUploadError(UploadField.FileUploadErrorEvent e) {
                showNotification(formatMessage('fileUploadError'), Frame.NotificationType.ERROR)
            }
        })
    }


    protected String getCheck() {
        formatMessage('check')
    }

    void cancelWizard() {
        close(CLOSE_ACTION_ID)
    }

    void closeWizard() {
        close(CLOSE_ACTION_ID)
    }


}


class DataRow {

    def values = []
    Map<String, Object> columns = [:]

    static DataRow ofMap(Map<String, Object> data) {
        def row = new DataRow()

        int i = 0
        data.each { k, v ->
            row.columns[k] = i
            row.values << v
            i++
        }

        row
    }

    KeyValueEntity toKevValueEntity() {
        def result = new KeyValueEntity()

        toMap().each {String k, Object v ->
            result.setValue(k, v)
        }
        result
    }

    def propertyMissing(String name) {
        def index = columns[name]
        if (index != null) {
            values[index]
        } else {
            throw new MissingPropertyException(name)
        }
    }

    def getAt(Integer index) {
        values[index]
    }


    String toString() {
        columns.collect { key, index -> "$key: ${values[index]}" }.join(', ')
    }

    Map toMap() {
        def sortedKeys = columns.keySet().sort { columns[it] }
        [sortedKeys, values].transpose().collectEntries()
    }
}

class TableData {
    List<DataRow> rows
    List<String> columns
}


class DynamicTableCreator {

    ComponentsFactory componentsFactory

    Frame frame

    DsContext dsContext


    Table createTable(TableData tableData, BoxLayout tableWrapper) {
        ValueCollectionDatasourceImpl tableDs = createDatasource(tableData)
        createDynamicTable(tableDs, tableWrapper)
    }


    private ValueCollectionDatasourceImpl createDatasource(TableData tableData) {
        ValueCollectionDatasourceImpl tableDs = createValueCollectionDs()
        tableData.rows.each { tableDs.includeItem(it.toKevValueEntity()) }
        tableData.columns.each { tableDs.addProperty(it) }
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

        Table table = componentsFactory.createComponent(Table)
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