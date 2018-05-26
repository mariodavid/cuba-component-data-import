package de.diedavids.cuba.dataimport.web.action

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.gui.WindowManager
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.components.ListComponent
import com.haulmont.cuba.gui.components.Window
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.service.ImportWizardService
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.inject.Inject

@CompileStatic
@Component('ddcdi$WithImportBean')
class WithImportBean {


    private static final String IMPORT_CAPTION_MSG_KEY = 'actions.Import'
    private static final String IMPORT_ICON_KEY = 'icons/wf-design-import.png'

    @Inject
    Messages messages

    @Inject
    ImportWizardService importWizardService


    void setCaption(Action action) {
        action.setCaption(messages.getMainMessage(IMPORT_CAPTION_MSG_KEY))
    }

    void setIcon(Action action) {
        action.setIcon(IMPORT_ICON_KEY)
    }


    void openImportWizard(ListComponent target, MetaClass entityMetaClass) {
        def frame = target.frame
        def importConfigurations = importWizardService.getImportConfigurations(entityMetaClass)

        if (!importConfigurations) {
            showNoImportConfigurationsError(frame)
        } else if (importConfigurations.size() == 1) {
            openImportWizardForImportConfiguration(target, importConfigurations[0] as ImportConfiguration)
        } else {
            selectImportConfigurationAndOpen(target, entityMetaClass)
        }
    }

    private void showNoImportConfigurationsError(Frame frame) {
        frame.showNotification(messages.getMainMessage('noImportConfigurationsForEntityFound'), Frame.NotificationType.ERROR)
    }

    private void selectImportConfigurationAndOpen(ListComponent target, MetaClass entityMetaClass) {
        target.frame.openLookup(
                ImportConfiguration,
                new Window.Lookup.Handler() {
                    @Override
                    void handleLookup(Collection items) {
                        openImportWizardForImportConfiguration(target, items[0] as ImportConfiguration)
                    }

                },
                WindowManager.OpenType.DIALOG,
                getLookupParams(entityMetaClass)
        )
    }

    @SuppressWarnings('UnnecessaryCast')
    private Map<String, Object> getLookupParams(MetaClass entityMetaClass) {
        [entityClass: entityMetaClass.name] as Map<String, Object>
    }

    private void openImportWizardForImportConfiguration(ListComponent target, ImportConfiguration importConfiguration) {
        Window.Editor importScreen = target.frame.openEditor('ddcdi$import-with-import-configuration-wizard', importConfiguration, WindowManager.OpenType.DIALOG)

        importScreen.addCloseWithCommitListener {
            target.datasource.refresh()
        }

    }
}
