package de.diedavids.cuba.dataimport.web.action

import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.gui.WindowManager
import com.haulmont.cuba.gui.components.Component
import com.haulmont.cuba.gui.components.ListComponent
import com.haulmont.cuba.gui.components.Window
import com.haulmont.cuba.gui.components.actions.BaseAction
import de.diedavids.cuba.dataimport.entity.ImportConfiguration

class TableWithImportAction extends BaseAction {


    Messages messages = AppBeans.<Messages> get(Messages)

    TableWithImportAction(ListComponent listComponent) {
        super('startImport')
        target = listComponent
        setIcon('icons/wf-design-import.png')
        setCaption(messages.getMainMessage('actions.Import'))
    }

    @Override
    void actionPerform(Component component) {
        doOpenImportWizard();
    }

    private void doOpenImportWizard() {
        target.getFrame().openLookup(ImportConfiguration, new Window.Lookup.Handler() {
            @Override
            void handleLookup(Collection items) {
                doOpenImportWizard(items[0] as ImportConfiguration);
            }

        }, WindowManager.OpenType.DIALOG, [entityClass: getTargetClassName()])
    }

    private String getTargetClassName() {
        return target.getDatasource().getMetaClass().getName();
    }

    private void doOpenImportWizard(ImportConfiguration importConfiguration) {
        target.getFrame().openEditor('ddcdi$import-with-import-configuration-wizard', importConfiguration, WindowManager.OpenType.DIALOG);
    }
}
