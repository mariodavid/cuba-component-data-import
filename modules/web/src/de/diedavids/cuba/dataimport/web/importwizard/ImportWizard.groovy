package de.diedavids.cuba.dataimport.web.importwizard

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.entity.FileDescriptor
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.data.CollectionDatasource
import com.haulmont.cuba.gui.data.Datasource
import com.haulmont.cuba.gui.upload.FileUploadingAPI
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import de.diedavids.cuba.dataimport.dto.ImportData
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.service.DataImportService
import de.diedavids.cuba.dataimport.service.GenericDataImporterService
import de.diedavids.cuba.dataimport.web.datapreview.DynamicTableCreator
import de.diedavids.cuba.dataimport.web.datapreview.converter.CsvTableDataConverter
import de.diedavids.cuba.dataimport.web.datapreview.converter.ExcelTableDataConverter
import de.diedavids.cuba.dataimport.web.datapreview.converter.TableDataConverter
import de.diedavids.cuba.dataimport.web.util.EntityClassSelector
import de.diedavids.cuba.dataimport.web.util.MetaPropertyMatcher

import javax.inject.Inject

class ImportWizard extends AbstractWindow {

    public static final String WIZARD_STEP_2 = 'step2'
    public static final String WIZARD_STEP_1 = 'step1'
    public static final String WIZARD_STEP_3 = 'step3'
    public static final String WIZARD_STEP_4 = 'step4'

    @Inject
    Accordion wizardAccordion
    @Inject
    Button closeWizard

    @Inject
    FileUploadField importFileUploadBtn
    @Inject
    FileUploadingAPI fileUploadingAPI

    @Inject
    ComponentsFactory componentsFactory

    @Inject
    protected BoxLayout resultTableBox

    @Inject
    LookupField entityLookup

    @Inject
    Metadata metadata

    FileDescriptor uploadedFileDescriptor
    File uploadedFile

    @Inject
    Table mapAttributesTable

    @Inject
    CollectionDatasource<ImportAttributeMapper, UUID> importAttributeMappersDatasource

    @Inject
    Datasource<ImportConfiguration> importConfigurationDs

    @Inject
    DataImportService dataImportService

    @Inject
    GenericDataImporterService genericDataImporterService

    private ImportData importData

    @Inject
    Action closeWizardAction

    @Inject
    EntityClassSelector entityClassSelector

    @Inject
    MetaPropertyMatcher metaPropertyMatcher


    @Override
    void init(Map<String, Object> params) {

        initUploadFileSucceedListener()
        initUploadFileErrorListener()

        entityLookup.setOptionsMap(entityClassSelector.entitiesLookupFieldOptions)

        importConfigurationDs.setItem(metadata.create(ImportConfiguration))

        initEntityClassPropertyChangeListener()

    }

    private initEntityClassPropertyChangeListener() {
        importConfigurationDs.addItemPropertyChangeListener(new Datasource.ItemPropertyChangeListener() {
            @Override
            void itemPropertyChanged(Datasource.ItemPropertyChangeEvent e) {

                if (e.property == 'entityClass') {
                    MetaClass selectedEntity = metadata.getClass(e.value.toString())
                    importData = parseFile(uploadedFileDescriptor, uploadedFile)


                    def mappers = createMappers(importData, selectedEntity)
                    mappers.each {
                        importAttributeMappersDatasource.addItem(it)
                    }

                    importConfigurationDs.item.importAttributeMappers = mappers

                    mapAttributesTable.visible = true
                }
            }
        })
    }

    private List<ImportAttributeMapper> createMappers(ImportData importData, MetaClass selectedEntity) {
        importData.columns.withIndex().collect { String column, index ->
            new ImportAttributeMapper(
                    entityAttribute: metaPropertyMatcher.findEntityAttributeForColumn(column, selectedEntity),
                    fileColumnAlias: column,
                    fileColumnNumber: index,
            )
        }
    }


    protected initUploadFileSucceedListener() {
        importFileUploadBtn.addFileUploadSucceedListener(new FileUploadField.FileUploadSucceedListener() {
            @Override
            void fileUploadSucceed(FileUploadField.FileUploadSucceedEvent e) {
                File file = fileUploadingAPI.getFile(importFileUploadBtn.fileId)

                uploadedFileDescriptor = importFileUploadBtn.fileDescriptor
                uploadedFile = file
                showStep2()
            }


        })
    }


    private void switchTabs(String previousTabName, String nextTabName) {
        wizardAccordion.getTab(nextTabName).enabled = true
        wizardAccordion.selectedTab = nextTabName

        Accordion.Tab previousTab = wizardAccordion.getTab(previousTabName)
        previousTab.caption = "${previousTab.caption} $check"
        previousTab.enabled = false
    }

    void showStep2() {
        switchTabs(WIZARD_STEP_1, WIZARD_STEP_2)
    }


    void toStep3() {
        switchTabs(WIZARD_STEP_2, WIZARD_STEP_3)
    }

    void parseFileAndDisplay(FileDescriptor fileDescriptor, File file) {

        importData = parseFile(fileDescriptor, file)

        DynamicTableCreator dynamicTableCreator = createDynamicTableCreator()
        dynamicTableCreator.createTable(importData, resultTableBox)

    }

    private ImportData parseFile(FileDescriptor fileDescriptor, File file) {
        TableDataConverter converter = createTableDataConverter(fileDescriptor)
        converter.convert(file.text)
    }

    private TableDataConverter createTableDataConverter(FileDescriptor fileDescriptor) {
        switch (fileDescriptor.extension) {
            case 'xlsx': return new ExcelTableDataConverter()
            case 'csv': return new CsvTableDataConverter()
            default: throw new FileNotSupportedException()
        }
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
        genericDataImporterService.doDataImport(importConfigurationDs.item, importData)

        showNotification('Data Import successful', Frame.NotificationType.TRAY)

        close(CLOSE_ACTION_ID)
    }

    void toStep4() {

        switchTabs(WIZARD_STEP_3, WIZARD_STEP_4)

        parseFileAndDisplay(uploadedFileDescriptor, uploadedFile)

        closeWizardAction.enabled = true

    }
}