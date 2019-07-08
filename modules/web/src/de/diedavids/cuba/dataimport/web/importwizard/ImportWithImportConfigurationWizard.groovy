package de.diedavids.cuba.dataimport.web.importwizard


import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.data.Datasource
import com.haulmont.cuba.gui.upload.FileUploadingAPI
import de.diedavids.cuba.dataimport.converter.DataConverterFactory
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportExecution
import de.diedavids.cuba.dataimport.service.GenericDataImporterService
import de.diedavids.cuba.dataimport.service.ImportWizardService
import de.diedavids.cuba.dataimport.web.datapreview.DynamicTableCreator
import de.diedavids.cuba.dataimport.web.importfile.ImportDataImportConfigurationMatchException
import de.diedavids.cuba.dataimport.web.importfile.ImportFileHandler
import de.diedavids.cuba.dataimport.web.importfile.ImportFileParser
import groovy.util.logging.Slf4j

import javax.inject.Inject

@Slf4j
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
    Action startImport

    @Inject
    DataManager dataManager

    @Inject
    Datasource<ImportExecution> importExecutionDs

    @Inject
    ImportWizardService importWizardService


    @Inject
    UiComponents uiComponents

    Map<String, Object> defaultValues

    @Override
    void init(Map<String, Object> params) {
        initImportFileHandler()
        initImportFileParser()

        defaultValues = params.get('defaultValues') as Map<String, Object>
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
        importFileHandler.onUploadSuccess { toStep2()}
        importFileHandler.onUploadError {
            showNotification(formatMessage('fileUploadError'), Frame.NotificationType.ERROR)
        }
    }


    void toStep1() {
        switchTabs(WIZARD_STEP_2, WIZARD_STEP_1)
        showDefaultStep1Title()
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

    private void showDefaultStep1Title() {
        Accordion.Tab step1Tab = wizardAccordion.getTab(WIZARD_STEP_1)
        step1Tab.caption = formatMessage('stepUploadFile')
    }

    private DynamicTableCreator createDynamicTableCreator() {
        new DynamicTableCreator(
                dsContext: dsContext,
                frame: frame,
                uiComponents: uiComponents
        )
    }

    protected String getCheck() {
        formatMessage('check')
    }

    void cancelWizard() {
        close(CLOSE_ACTION_ID)
    }

    void closeWizard() {
        close(COMMIT_ACTION_ID, true)
    }

    void parseFileAndDisplay() {
        try {
            importData = importFileParser.parseFile(item)
            startImport.enabled = true
            DynamicTableCreator dynamicTableCreator = createDynamicTableCreator()
            dynamicTableCreator.createTable(importData, resultTableBox)
        }
        catch (ImportDataImportConfigurationMatchException e) {
            log.error(e.message, e)

            startImport.enabled = false
            showNotification(formatMessage('uploadFileDoesNotMatchExpectedStructure'), Frame.NotificationType.ERROR)

            toStep1()
        }
    }


    void startImport() {
        ImportExecution importExecution = genericDataImporterService.doDataImport(importConfigurationDs.item, importData, defaultValues)
        importExecutionDs.item = importExecution
        importExecutionDs.item.file = importFileHandler.saveFile()
        dataManager.commit(importExecution)

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