package de.diedavids.cuba.dataimport.web.importwizard

import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.data.Datasource
import com.haulmont.cuba.gui.upload.FileUploadingAPI
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import de.diedavids.cuba.dataimport.converter.DataConverterFactory
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportLog
import de.diedavids.cuba.dataimport.service.GenericDataImporterService
import de.diedavids.cuba.dataimport.service.ImportWizardService
import de.diedavids.cuba.dataimport.web.datapreview.DynamicTableCreator
import de.diedavids.cuba.dataimport.web.importfile.ImportFileHandler
import de.diedavids.cuba.dataimport.web.importfile.ImportFileParser

import javax.inject.Inject

class ImportWithImportConfigurationWizard extends AbstractEditor<ImportConfiguration> {

    public static final String WIZARD_STEP_1 = 'step1'
    public static final String WIZARD_STEP_2 = 'step2'
    public static final String WIZARD_STEP_3 = 'step3'

    @Inject
    BoxLayout resultTableBox

    @Inject
    Accordion wizardAccordion

    @Inject
    Button closeWizard

    @Inject
    DataConverterFactory dataConverterFactory

    @Inject
    FileUploadField importFileUploadBtn

    @Inject
    FileUploadingAPI fileUploadingAPI

    ImportFileHandler importFileHandler

    ImportFileParser importFileParser

    @Inject
    Datasource<ImportConfiguration> importConfigurationDs

    @Inject
    GenericDataImporterService genericDataImporterService

    ImportData importData

    @Inject
    Action closeWizardAction

    @Inject
    DataManager dataManager

    @Inject
    Datasource<ImportLog> importLogDs

    @Inject
    ImportWizardService importWizardService

    @Inject
    ComponentsFactory componentsFactory

    @Override
    void init(Map<String, Object> params) {
        initImportFileHandler()
        initImportFileParser()
    }

    void initImportFileParser() {
        importFileParser = new ImportFileParser(
                importFileHandler: importFileHandler,
                dataConverterFactory: dataConverterFactory
        )
    }

    private void initImportFileHandler() {
        importFileHandler = new ImportFileHandler(
                importFileUploadBtn: importFileUploadBtn,
                fileUploadingAPI: fileUploadingAPI,
                dataManager: dataManager
        )
        importFileHandler.onUploadSuccess { toStep2() }
        importFileHandler.onUploadError {
            showNotification(formatMessage('fileUploadError'), Frame.NotificationType.ERROR)
        }
    }

    void toStep2() {
        switchTabs(WIZARD_STEP_1, WIZARD_STEP_2)
        showFilenameInStep1Title()
        parseFileAndDisplay()
        closeWizardAction.enabled = true
    }

    private void showFilenameInStep1Title() {
        Accordion.Tab step1Tab = wizardAccordion.getTab(WIZARD_STEP_1)
        step1Tab.caption += " - ${importFileHandler.fileName}"
    }

    private DynamicTableCreator createDynamicTableCreator() {
        def dynamicTableCreator = new DynamicTableCreator(
                dsContext: dsContext,
                frame: frame,
                componentsFactory: componentsFactory
        )
        dynamicTableCreator
    }

    protected String getCheck() {
        formatMessage('check')
    }

    void cancelWizard() {
        close(CLOSE_ACTION_ID)
    }

    void closeWizard() {
        close(CLOSE_ACTION_ID, true)
    }

    void parseFileAndDisplay() {
        importData = importFileParser.parseFile()
        DynamicTableCreator dynamicTableCreator = createDynamicTableCreator()
        dynamicTableCreator.createTable(importData, resultTableBox)
    }

    void startImport() {
        ImportLog importLog = genericDataImporterService.doDataImport(importConfigurationDs.item, importData)
        importLogDs.item = importLog
        toStep3()
    }

    void toStep3() {
        switchTabs(WIZARD_STEP_2, WIZARD_STEP_3)
        closeWizardAction.enabled = true
    }


    private void switchTabs(String previousTabName, String nextTabName) {
        wizardAccordion.getTab(nextTabName).enabled = true
        wizardAccordion.selectedTab = nextTabName

        Accordion.Tab previousTab = wizardAccordion.getTab(previousTabName)
        previousTab.caption = "${previousTab.caption} $check"
        previousTab.enabled = false
    }
}