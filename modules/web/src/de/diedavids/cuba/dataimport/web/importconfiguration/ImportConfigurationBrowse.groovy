package de.diedavids.cuba.dataimport.web.importconfiguration

import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.gui.WindowManager
import com.haulmont.cuba.gui.components.AbstractLookup
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.components.actions.CreateAction

import javax.inject.Named

class ImportConfigurationBrowse extends AbstractLookup {

    @Named('importConfigurationsTable.create')
    CreateAction createAction

    @Override
    void init(Map<String, Object> params) {
        createAction.setWindowId('ddcdi$ImportConfiguration.create')
        createAction.afterCommitHandler = new CreateAction.AfterCommitHandler() {
            @Override
            void handle(Entity entity) {
                showNotification('Import Configuration created successfully', Frame.NotificationType.TRAY)
                openEditor(entity, WindowManager.OpenType.THIS_TAB)
            }
        }
    }
}