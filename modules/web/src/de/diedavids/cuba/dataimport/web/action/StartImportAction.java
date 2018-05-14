package de.diedavids.cuba.dataimport.web.action;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import de.diedavids.cuba.dataimport.entity.ImportConfiguration;

import java.util.Collection;

public class StartImportAction extends BaseAction {
    public StartImportAction() {
        super("startImport");
    }

    @Override
    public void actionPerform(Component component) {
        doOpenImportWizard();
    }

    private void doOpenImportWizard() {
        target.getFrame().openLookup(ImportConfiguration.class, items -> {
            doOpenImportWizard(getSelectedImportConfiguration(items));
        }, WindowManager.OpenType.DIALOG, ParamsMap.of("entityClass", getTargetClassName()));
    }

    private ImportConfiguration getSelectedImportConfiguration(Collection items) {
        return (ImportConfiguration) items.iterator().next();
    }

    private String getTargetClassName() {
        return target.getDatasource().getMetaClass().getName();
    }

    private void doOpenImportWizard(ImportConfiguration importConfiguration) {
        target.getFrame().openEditor("ddcdi$import-with-import-configuration-wizard", importConfiguration, WindowManager.OpenType.DIALOG);
    }
}
