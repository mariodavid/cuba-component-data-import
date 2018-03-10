package de.diedavids.cuba.dataimport.web.importwizard

import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.entity.FileDescriptor
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.Security
import com.haulmont.cuba.gui.components.*
import com.haulmont.cuba.gui.data.CollectionDatasource
import com.haulmont.cuba.gui.upload.FileUploadingAPI
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
import com.haulmont.cuba.security.entity.EntityOp
import de.diedavids.cuba.dataimport.entity.ImportAttributeMapper
import de.diedavids.cuba.dataimport.entity.example.MlbPlayer
import de.diedavids.cuba.dataimport.web.datapreview.DynamicTableCreator
import de.diedavids.cuba.dataimport.web.datapreview.ImportData
import de.diedavids.cuba.dataimport.web.datapreview.csv.CsvTableDataConverter
import de.diedavids.cuba.dataimport.web.datapreview.csv.ExcelTableDataConverter
import de.diedavids.cuba.dataimport.web.datapreview.csv.TableDataConverter

import com.haulmont.chile.core.model.MetaClass
import org.apache.commons.lang.StringUtils

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

    @Inject
    LookupField entityLookup

    @Inject
    Metadata metadata

    FileDescriptor uploadedFileDescriptor
    File uploadedFile
    
    String selectedEntityName

    @Inject
    Table mapAttributesTable

    @Inject
    CollectionDatasource mapAttributesDatasource


    @Override
    void init(Map<String, Object> params) {

        initUploadFileSucceedListener()
        initUploadFileErrorListener()

        entityLookup.addValueChangeListener(new Component.ValueChangeListener() {
            @Override
            void valueChanged(Component.ValueChangeEvent e) {

                MetaClass selectedEntity = metadata.getClass(e.value.toString())
                ImportData importData = parseFile(uploadedFileDescriptor, uploadedFile)

                mapAttributesDatasource.refresh([data: createMappers(importData, selectedEntity)])

                mapAttributesTable.visible = true
            }

        })
    }

    private List<ImportAttributeMapper> createMappers(ImportData importData, MetaClass selectedEntity) {
        importData.columns.withIndex().collect { String column, index ->
            new ImportAttributeMapper(
                    entityAttribute: findEntityAttributeForColumn(column, selectedEntity),
                    fileColumnAlias: column,
                    fileColumnNumber: index
            )
        }
    }

    private String findEntityAttributeForColumn(String column, MetaClass selectedEntity) {

        MetaProperty possibleProperty = findPropertyByColumn(selectedEntity, column)

        possibleProperty?.toString() ?: ""

    }

    private MetaProperty findPropertyByColumn(MetaClass selectedEntity, String column) {

        def propertiesNames = selectedEntity.properties.collect {
            it.name
        }

        def distances = [:]
        def match = null

        def directMatch = propertiesNames.find {
            def distance = StringUtils.getLevenshteinDistance(it, column)
            distances.put(it, distance)

            column.toLowerCase().startsWith(it) ||
            column.toLowerCase().endsWith(it)
        }

        if (directMatch) {
            match = directMatch
        }
        else {
            def mostLikelyMatch = distances.min { it.value }

            if (mostLikelyMatch.value < 5) {
                match = mostLikelyMatch.key
            }
        }

        if (match) {
            selectedEntity.getProperty(match)
        }
        else {
            null
        }
    }

    protected initUploadFileSucceedListener() {
        importFileUploadBtn.addFileUploadSucceedListener(new FileUploadField.FileUploadSucceedListener() {
            @Override
            void fileUploadSucceed(FileUploadField.FileUploadSucceedEvent e) {
                File file = fileUploadingAPI.getFile(importFileUploadBtn.fileId)

                uploadedFileDescriptor = importFileUploadBtn.fileDescriptor
                uploadedFile = file

                switchTabs(WIZARD_STEP_1, WIZARD_STEP_2)
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
        entityLookup.setOptionsMap(getEntitiesLookupFieldOptions())
    }





    protected boolean readPermitted(MetaClass metaClass) {
        return entityOpPermitted(metaClass, EntityOp.READ)
    }

    protected boolean entityOpPermitted(MetaClass metaClass, EntityOp entityOp) {
        Security security = AppBeans.get(Security.NAME)
        return security.isEntityOpPermitted(metaClass, entityOp)
    }


    protected Map<String, Object> getEntitiesLookupFieldOptions() {
        Map<String, Object> options = new TreeMap<>()

        for (MetaClass metaClass : metadata.getTools().getAllPersistentMetaClasses()) {
            if (readPermitted(metaClass)) {
                Class javaClass = metaClass.getJavaClass()
                if (Entity.class.isAssignableFrom(javaClass)) {
                    options.put(messages.getTools().getEntityCaption(metaClass) + " (" + metaClass.getName() + ")", metaClass)
                }
            }
        }

        return options
    }

    void toStep3 ( ) {

        switchTabs(WIZARD_STEP_2, WIZARD_STEP_3)
        parseFileAndDisplay(uploadedFileDescriptor, uploadedFile)
    }

    void parseFileAndDisplay(FileDescriptor fileDescriptor, File file) {

        ImportData importData = parseFile(fileDescriptor, file)

        DynamicTableCreator dynamicTableCreator = createDynamicTableCreator()
        dynamicTableCreator.createTable(importData, resultTableBox)

    }

    private ImportData parseFile(FileDescriptor fileDescriptor, File file) {
        TableDataConverter converter = createTableDataConverter(fileDescriptor)

        converter.convert(file.text)
    }

    private TableDataConverter createTableDataConverter(FileDescriptor fileDescriptor) {
        switch (fileDescriptor.extension) {
            case "xlsx": return new ExcelTableDataConverter()
            case "csv": return new CsvTableDataConverter()
            default: throw RuntimeException("File extension not supported")
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
        close(CLOSE_ACTION_ID)
    }
}