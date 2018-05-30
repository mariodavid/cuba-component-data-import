package de.diedavids.cuba.dataimport.web.importwizard

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.WindowManager
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.data.CollectionDatasource
import com.haulmont.cuba.gui.data.Datasource
import com.haulmont.cuba.gui.upload.FileUploadingAPI
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import de.diedavids.cuba.dataimport.converter.DataConverterFactory
import de.diedavids.cuba.dataimport.converter.ImportAttributeMapperCreator
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.attributemapper.AttributeMapperMode
import de.diedavids.cuba.dataimport.entity.attributemapper.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.entity.ImportLog
import de.diedavids.cuba.dataimport.service.GenericDataImporterService
import de.diedavids.cuba.dataimport.service.ImportWizardService
import de.diedavids.cuba.dataimport.web.datapreview.DynamicTableCreator
import de.diedavids.cuba.dataimport.web.importfile.ImportFileHandler
import de.diedavids.cuba.dataimport.web.importfile.ImportFileParser
import de.diedavids.cuba.dataimport.web.util.MetadataSelector
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Named

@Slf4j
class ImportWizard extends AbstractWindow {

    public static final String WIZARD_STEP_1 = 'step1'
    public static final String WIZARD_STEP_2 = 'step2'
    public static final String WIZARD_STEP_3 = 'step3'
    public static final String WIZARD_STEP_4 = 'step4'
    public static final String WIZARD_STEP_5 = 'step5'

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
    ComponentsFactory componentsFactory

    @Inject
    BoxLayout resultTableBox

    @Inject
    LookupField entityLookup

    @Inject
    Metadata metadata

    @Inject
    Table mapAttributesTable

    @Inject
    CollectionDatasource<ImportAttributeMapper, UUID> importAttributeMappersDatasource

    @Inject
    Datasource<ImportConfiguration> importConfigurationDs

    @Inject
    GenericDataImporterService genericDataImporterService

    ImportData importData

    @Inject
    Action closeWizardAction

    @Inject
    MetadataSelector metadataSelector

    @Inject
    Datasource<ImportLog> importLogDs

    @Named('reuseFieldGroup.name')
    TextField nameField
    @Named('reuseFieldGroup.comment')
    TextArea commentField

    @Inject
    ImportWizardService importWizardService

    @Inject
    DataManager dataManager

    @Inject
    ImportAttributeMapperCreator importAttributeMapperCreator

    ImportConfiguration defaultImportConfiguration

    @Inject
    Button toStep3

    @Override
    void init(Map<String, Object> params) {
        entityLookup.setOptionsMap(metadataSelector.entitiesLookupFieldOptions)
        importConfigurationDs.setItem(metadata.create(ImportConfiguration))
        initEntityClassPropertyChangeListener()
        initReusePropertyChangeListener()
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

    void initReusePropertyChangeListener() {
        importConfigurationDs.addItemPropertyChangeListener(new Datasource.ItemPropertyChangeListener() {
            @Override
            void itemPropertyChanged(Datasource.ItemPropertyChangeEvent e) {
                if (e.property == 'reuse') {
                    nameField.visible = e.value as Boolean
                    commentField.visible = e.value as Boolean
                }
            }
        })
    }

    private initEntityClassPropertyChangeListener() {
        importConfigurationDs.addItemPropertyChangeListener(new Datasource.ItemPropertyChangeListener() {
            @Override
            void itemPropertyChanged(Datasource.ItemPropertyChangeEvent e) {
                if (e.property == 'entityClass') {
                    MetaClass selectedEntity = metadata.getClass(e.value.toString())
                    importData = importFileParser.parseFile()
                    def mappers = importAttributeMapperCreator.createMappers(importData, selectedEntity)
                    mappers.each {
                        it.setConfiguration(importConfigurationDs.item)
                        importAttributeMappersDatasource.addItem(it)
                    }
                    importConfigurationDs.item.importAttributeMappers = mappers
                    mapAttributesTable.visible = true
                    toStep3.enabled = true
                }
            }
        })
    }

    void toStep2() {
        switchTabs(WIZARD_STEP_1, WIZARD_STEP_2)
        showFilenameInStep1Title()
        toStep3.enabled = false
    }

    private void showFilenameInStep1Title() {
        Accordion.Tab step1Tab = wizardAccordion.getTab(WIZARD_STEP_1)
        step1Tab.caption += " - ${importFileHandler.fileName}"
    }

    void toStep3() {
        switchTabs(WIZARD_STEP_2, WIZARD_STEP_3)
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
        if (importConfigurationDs.item.reuse) {
            importLogDs.item.file = importFileHandler.saveFile()

            importWizardService.saveImportConfiguration(
                    importConfigurationDs.item,
                    importAttributeMappersDatasource.items,
                    importLogDs.item
            )
        }
        close(CLOSE_ACTION_ID, true)
    }

    void toStep4() {
        switchTabs(WIZARD_STEP_3, WIZARD_STEP_4)
        parseFileAndDisplay()
    }

    void parseFileAndDisplay() {
        importData = importFileParser.parseFile()
        DynamicTableCreator dynamicTableCreator = createDynamicTableCreator()
        dynamicTableCreator.createTable(importData, resultTableBox)
    }

    void startImport() {
        try {
            ImportLog importLog = genericDataImporterService.doDataImport(importConfigurationDs.item, importData)
            importLogDs.item = importLog
            toStep5()
        } catch (Exception e) {
            log.error('An Error occurred during import', e)
            showNotification('An Error occurred during import. See logs for more information', Frame.NotificationType.ERROR)
        }
    }

    void toStep5() {
        switchTabs(WIZARD_STEP_4, WIZARD_STEP_5)
        closeWizardAction.enabled = true
    }

    private void switchTabs(String previousTabName, String nextTabName) {
        wizardAccordion.getTab(nextTabName).enabled = true
        wizardAccordion.selectedTab = nextTabName

        Accordion.Tab previousTab = wizardAccordion.getTab(previousTabName)
        def caption = previousTab.caption
        if (!caption.contains("$check")) {
            previousTab.caption = "${previousTab.caption} $check"
        }
        //allow to return back: true
        previousTab.enabled = true
    }

    void newCustomAttributeMapper() {
        def importAttributeMapper = metadata.create(ImportAttributeMapper)
        importAttributeMapper.mapperMode = AttributeMapperMode.CUSTOM
        importAttributeMapper.configuration = importConfigurationDs.item
        importAttributeMapper.attributeType = null
        openAttributeMapperEditor(importAttributeMapper)
    }


    void newAutomaticAttributeMapper() {
        def importAttributeMapper = metadata.create(ImportAttributeMapper)
        importAttributeMapper.configuration = importConfigurationDs.item
        openAttributeMapperEditor(importAttributeMapper)
    }

    private AbstractEditor openAttributeMapperEditor(ImportAttributeMapper importAttributeMapper) {
        openEditor(importAttributeMapper, WindowManager.OpenType.DIALOG, [:], importAttributeMappersDatasource)
    }


}