package de.diedavids.cuba.dataimport.web.action

import com.haulmont.bali.util.ParamsMap
import com.haulmont.chile.core.model.MetaClass
import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.gui.WindowManager
import com.haulmont.cuba.gui.components.Action
import com.haulmont.cuba.gui.components.Frame
import com.haulmont.cuba.gui.components.ListComponent
import com.haulmont.cuba.gui.components.Window
import com.haulmont.cuba.gui.model.CollectionContainer
import com.haulmont.cuba.gui.model.ScreenData
import de.diedavids.cuba.dataimport.entity.ImportConfiguration
import de.diedavids.cuba.dataimport.service.ImportWizardService
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.inject.Inject
import java.util.function.Supplier

@CompileStatic
@Component(WithImportWizardExecution.NAME)
class WithImportWizardExecutionBean implements WithImportWizardExecution {


    private static final String IMPORT_CAPTION_MSG_KEY = 'actions.Import'
    private static final String IMPORT_ICON_KEY = 'icons/wf-design-import.png'

    @Inject
    Messages messages

    @Inject
    ImportWizardService importWizardService


    void initCaption(Action action) {
        action.setCaption(messages.getMainMessage(IMPORT_CAPTION_MSG_KEY))
    }

    void initIcon(Action action) {
        action.setIcon(IMPORT_ICON_KEY)
    }

    void openImportWizard(ListComponent target, CollectionContainer targetContainer, ScreenData screenData, Supplier<Map<String, Object>> defaultValuesSupplier) {
        def frame = target.frame
        def entityMetaClass = targetContainer.entityMetaClass
        def importConfigurations = importWizardService.getImportConfigurations(entityMetaClass)

        if (!importConfigurations) {
            showNoImportConfigurationsError(frame)
        } else if (importConfigurations.size() == 1) {
            openImportWizardForImportConfiguration(target, screenData, importConfigurations[0] as ImportConfiguration, defaultValuesSupplier)
        } else {
            selectImportConfigurationAndOpen(target, screenData, entityMetaClass, defaultValuesSupplier)
        }
    }

    private void showNoImportConfigurationsError(Frame frame) {
        frame.showNotification(messages.getMainMessage('noImportConfigurationsForEntityFound'), Frame.NotificationType.ERROR)
    }

    private void selectImportConfigurationAndOpen(ListComponent target, ScreenData screenData, MetaClass entityMetaClass, Supplier<Map<String, Object>> defaultValuesSupplier) {
        target.frame.openLookup(
                ImportConfiguration,
                new Window.Lookup.Handler() {
                    @Override
                    void handleLookup(Collection items) {
                        openImportWizardForImportConfiguration(target, screenData, items[0] as ImportConfiguration, defaultValuesSupplier)
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

    private void openImportWizardForImportConfiguration(ListComponent target, ScreenData screenData, ImportConfiguration importConfiguration, Supplier<Map<String, Object>> defaultValuesSupplier) {
        Window.Editor importScreen = target.frame.openEditor('ddcdi$import-with-import-configuration-wizard', importConfiguration, WindowManager.OpenType.DIALOG, ParamsMap.of('defaultValues',defaultValuesSupplier.get()))

        importScreen.addCloseWithCommitListener {
            screenData.loadAll()
        }

    }
}
