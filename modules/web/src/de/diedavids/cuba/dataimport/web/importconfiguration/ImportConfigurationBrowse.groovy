package de.diedavids.cuba.dataimport.web.importconfiguration

import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.gui.WindowManager
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.components.actions.CreateAction
import de.balvi.cuba.declarativecontrollers.web.browse.AnnotatableAbstractLookup
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.web.WithImport

import javax.inject.Inject
import javax.inject.Named

@WithImport(listComponent = 'importConfigurationsTable')
class ImportConfigurationBrowse extends AnnotatableAbstractLookup {

    @Named('importConfigurationsTable.create')
    CreateAction createAction

    @Inject
    Table<ImportConfiguration> importConfigurationsTable

    @Override
    void init(Map<String, Object> params) {
        super.init(params)
        createAction.setWindowId('ddcdi$ImportConfiguration.create')
        createAction.afterCommitHandler = new CreateAction.AfterCommitHandler() {
            @Override
            void handle(Entity entity) {
                showNotification('Import Configuration created successfully', Frame.NotificationType.TRAY)
                openEditor(entity, WindowManager.OpenType.THIS_TAB)
            }
        }
    }

    void executeImport() {
        openEditor('ddcdi$import-with-import-configuration-wizard', importConfigurationsTable.singleSelected, WindowManager.OpenType.DIALOG)
    }
}