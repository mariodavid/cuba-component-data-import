package de.diedavids.cuba.dataimport.web.importwizard

import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.upload.FileUploadingAPI
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import com.xlson.groovycsv.CsvParser
import com.xlson.groovycsv.PropertyMapper
import de.diedavids.cuba.dataimport.web.datapreview.DataRow
import de.diedavids.cuba.dataimport.web.datapreview.DynamicTableCreator
import de.diedavids.cuba.dataimport.web.datapreview.ImportData
import de.diedavids.cuba.dataimport.web.datapreview.csv.CsvTableDataConverter

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

    @Inject
    protected BoxLayout resultTableBox


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
        ImportData importData = new CsvTableDataConverter().convert(file.text)

        DynamicTableCreator dynamicTableCreator = createDynamicTableCreator()
        dynamicTableCreator.createTable(importData, resultTableBox)

    }

    private DynamicTableCreator createDynamicTableCreator() {
        def dynamicTableCreator = new DynamicTableCreator(
                dsContext: dsContext,
                frame: frame,
                componentsFactory: componentsFactory
        )
        dynamicTableCreator
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