package de.diedavids.cuba.dataimport.web.importwizard

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.cuba.core.entity.KeyValueEntity
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.data.DsBuilder
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


        LinkedHashMap<String, List> result = parseFile(file)


        ValueCollectionDatasourceImpl sqlResultDs = createDatasource(result)
        createResultTable(sqlResultDs)

    }

    private LinkedHashMap<String, List> parseFile(File file) {
        def csvData = new CsvParser().parse(file.text)


        def results = [
                entities: [],
                columns : []
        ]
        int i = 0
        csvData.each { PropertyMapper row ->


            if (i < MAX_ROWS_IMPORT_PREVIEW) {
                results.columns = row.columns.keySet()

                def entity = new KeyValueEntity()

                row.toMap().each { String k, String v ->
                    entity.setValue(k, v)
                }
                results.entities << entity
            }

            i++
        }
        results
    }


    private ValueCollectionDatasourceImpl createDatasource(def result) {
        ValueCollectionDatasourceImpl resultDs = creteValueCollectionDs()
        result.entities.each { resultDs.includeItem(it) }
        result.columns.each { resultDs.addProperty(it) }
        resultDs
    }

    private ValueCollectionDatasourceImpl creteValueCollectionDs() {
        DsBuilder.create(dsContext).reset().setAllowCommit(false)
                .buildValuesCollectionDatasource()
    }

    private Table createResultTable(ValueCollectionDatasourceImpl resultDs) {
        if (resultTable) {
            resultTableBox.remove(resultTable)
        }
        resultTable = componentsFactory.createComponent(Table)
        resultTable.frame = frame

        addTableColumns(resultDs, resultTable)

        resultTable.datasource = resultDs
        resultTable.setSizeFull()
        resultTable.setSortable(false)
        resultTable.setContextMenuEnabled(false)
        resultTableBox.add(resultTable)

        resultTable
    }


    private void addTableColumns(ValueCollectionDatasourceImpl sqlResultDs, Table resultTable) {
        MetaClass meta = sqlResultDs.metaClass
        for (MetaProperty metaProperty : meta.properties) {
            Table.Column column = new Table.Column(meta.getPropertyPath(metaProperty.name))
            column.caption = metaProperty.name
            resultTable.addColumn(column)
        }
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