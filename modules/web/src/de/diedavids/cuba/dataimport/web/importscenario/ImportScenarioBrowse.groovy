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
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.components.Button
import com.haulmont.cuba.gui.components.Component
import com.haulmont.cuba.gui.components.DialogAction
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.components.Window
import com.haulmont.cuba.gui.config.WindowConfig
import com.haulmont.cuba.gui.data.CollectionDatasource
import com.haulmont.cuba.gui.data.Datasource
import com.haulmont.cuba.gui.upload.FileUploadingAPI
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory
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
    private DataImportService importerService;

    @Inject
    private CollectionDatasource<ImportScenario, UUID> importScenariosDs;

    @Inject
    private Button btnImport;

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Inject
    private ComponentsFactory componentsFactory;


    @Override
    void init(Map<String, Object> params) {
        btnImport.setEnabled(importScenariosDs.getItem() != null);
        importScenariosDs.addItemChangeListener({
            e -> btnImport.setEnabled(e.getItem() != null)
        } as Datasource.ItemChangeListener);
    }

    public void onBtnImportClick() {
        WindowConfig windowConfig = AppBeans.get(WindowConfig.NAME);

        final FileUploadDialog dialog = (FileUploadDialog) App.getInstance().getWindowManager().
                openWindow(windowConfig.getWindowInfo("fileUploadDialog"), WindowManager.OpenType.DIALOG);

        final ImportScenario scenario = importScenariosDs.getItem();

        dialog.addCloseListener({ actionId ->
            if (COMMIT_ACTION_ID.equals(actionId)) {
                FileUploadingAPI fileUploading = AppBeans.get(FileUploadingAPI.NAME);
                FileDescriptor descriptor = fileUploading.getFileDescriptor(dialog.getFileId(), dialog.getFileName());
                try {
                    fileUploading.putFileIntoStorage(dialog.getFileId(), descriptor);
                    descriptor = dataManager.commit(descriptor);

                    ImportLog log = metadata.create(ImportLog.class);
                    log.setScenario(scenario);
                    log.setFile(descriptor);
                    log = dataManager.commit(log);
                    log = importerService.doImport(log, null, true);

                    showCompletionMessage(log);

                } catch (FileStorageException e) {
                    Log log = LogFactory.getLog(this.getClass());
                    log.error("File upload has failed", e);
                    showNotification("File upload has failed", Frame.NotificationType.ERROR);
                }
            }
        } as Window.CloseListener);


    }

    private void showCompletionMessage(ImportLog log) {
        long errorCount = log.records.count { r -> r.level == LogRecordLevel.ERROR }
        long warnCount = log.records.count { r -> r.level == LogRecordLevel.WARN }

        if ((errorCount + warnCount) == 0) {
            showOptionDialog("Import Result"
                    ,"<font color=\"green\">Import has been successfully finished with no warnings/errors</font>"
                    ,Frame.MessageType.CONFIRMATION_HTML
                    ,[
                new DialogAction(DialogAction.Type.OK, true)
            ]);
        } else {
            showOptionDialog("Import Result"
                    ,String.format("<font color=\"red\">Import has been finished with %s ERRORS and %s WARNINGS</br>" +
                    "Click OK to see import log</font>", errorCount, warnCount)
                    ,Frame.MessageType.WARNING_HTML
                    ,[
                new DialogAction(DialogAction.Type.OK, true) {
                    @Override
                    void actionPerform(Component component) {
                        super.actionPerform(component);
                        openWindow('importdata$ImportLog.browse', WindowManager.OpenType.NEW_TAB,
                                ParamsMap.of("selectLogItem", log));
                    }
                }
                ,new DialogAction(DialogAction.Type.CANCEL)
            ])
        }
    }
}