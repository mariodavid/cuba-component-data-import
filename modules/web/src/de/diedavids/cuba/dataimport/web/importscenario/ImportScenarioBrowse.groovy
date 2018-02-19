package de.diedavids.cuba.dataimport.web.importscenario

import com.haulmont.bali.util.ParamsMap
import com.haulmont.cuba.core.entity.FileDescriptor
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.FileStorageException
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.WindowManager
import com.haulmont.cuba.gui.app.core.file.FileUploadDialog
import com.haulmont.cuba.gui.components.AbstractLookup
import com.haulmont.cuba.gui.components.Button
import com.haulmont.cuba.gui.components.Component
import com.haulmont.cuba.gui.components.DialogAction
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.components.Window
import com.haulmont.cuba.gui.config.WindowConfig
import com.haulmont.cuba.gui.data.CollectionDatasource
import com.haulmont.cuba.gui.data.Datasource
import com.haulmont.cuba.gui.upload.FileUploadingAPI
import com.haulmont.cuba.web.App
import de.diedavids.cuba.dataimport.entity.ImportLog
import de.diedavids.cuba.dataimport.entity.ImportScenario
import de.diedavids.cuba.dataimport.entity.LogRecordLevel
import de.diedavids.cuba.dataimport.service.DataImportService
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import javax.inject.Inject

class ImportScenarioBrowse extends AbstractLookup {

    @Inject
    private DataImportService importerService

    @Inject
    private CollectionDatasource<ImportScenario, UUID> importScenariosDs

    @Inject
    private Button btnImport

    @Inject
    private Metadata metadata

    @Inject
    private DataManager dataManager

    private final static String FIELD_UPLOAD_ERROR_MSG = 'File upload has failed'
    private final static String IMPORT_RESULT_TITLE = 'Import Result'


    @Override
    void init(Map<String, Object> params) {
        btnImport.setEnabled(importScenariosDs.item != null)
        importScenariosDs.addItemChangeListener({
            e -> btnImport.setEnabled(e.item != null)
        } as Datasource.ItemChangeListener)
    }

    void onBtnImportClick() {
        WindowConfig windowConfig = AppBeans.get(WindowConfig.NAME)

        final FileUploadDialog dialog = (FileUploadDialog) App.instance.windowManager.
                openWindow(windowConfig.getWindowInfo('fileUploadDialog'), WindowManager.OpenType.DIALOG)

        dialog.addCloseListener({ actionId ->
            if (COMMIT_ACTION_ID == actionId) {
                FileUploadingAPI fileUploading = AppBeans.get(FileUploadingAPI.NAME)
                FileDescriptor descriptor = fileUploading.getFileDescriptor(dialog.fileId, dialog.fileName)
                try {
                    fileUploading.putFileIntoStorage(dialog.fileId, descriptor)
                    descriptor = dataManager.commit(descriptor)

                    ImportLog log = createImportLog(descriptor)
                    showCompletionMessage(log)

                } catch (FileStorageException e) {
                    Log log = LogFactory.getLog(this.class)
                    log.error(FIELD_UPLOAD_ERROR_MSG, e)
                    showNotification(FIELD_UPLOAD_ERROR_MSG, Frame.NotificationType.ERROR)
                }
            }
        } as Window.CloseListener)
    }

    private ImportLog createImportLog(FileDescriptor descriptor) {

        ImportScenario scenario = importScenariosDs.item

        ImportLog log = metadata.create(ImportLog)
        log.setScenario(scenario)
        log.setFile(descriptor)
        log = dataManager.commit(log)
        log = importerService.doImport(log, null, true)
        log
    }

    private void showCompletionMessage(ImportLog log) {
        long errorCount = log.records.count { r -> r.level == LogRecordLevel.ERROR }
        long warnCount = log.records.count { r -> r.level == LogRecordLevel.WARN }

        if ((errorCount + warnCount) == 0) {
            showOptionDialog(IMPORT_RESULT_TITLE
                    ,'<font color="green">Import has been successfully finished with no warnings/errors</font>'
                    ,Frame.MessageType.CONFIRMATION_HTML
                    ,[
                new DialogAction(DialogAction.Type.OK, true)
            ])
        } else {
            showImportResult(errorCount, warnCount, log)
        }
    }

    private showImportResult(long errorCount, long warnCount, log) {
        showOptionDialog(IMPORT_RESULT_TITLE
                , String.format('<font color="red">Import has been finished with %s ERRORS and %s WARNINGS</br>' +
                'Click OK to see import log</font>', errorCount, warnCount)
                , Frame.MessageType.WARNING_HTML
                , [
                new DialogAction(DialogAction.Type.OK, true) {
                    @Override
                    void actionPerform(Component component) {
                        super.actionPerform(component)
                        openWindow('importdata$ImportLog.browse', WindowManager.OpenType.NEW_TAB,
                                ParamsMap.of('selectLogItem', log))
                    }
                }
                , new DialogAction(DialogAction.Type.CANCEL)
        ])
    }
}