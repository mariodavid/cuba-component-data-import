package de.diedavids.cuba.dataimport.web.action;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.ScreenData;

import java.util.Map;
import java.util.function.Supplier;

public class TableWithImportWizardAction extends BaseAction {


    private WithImportWizardExecution withImportWizardExecution = AppBeans.get(WithImportWizardExecution.class);
    private final ListComponent target;
    private final CollectionContainer targetContainer;
    private final ScreenData screenData;
    private final Supplier<Map<String, Object>> defaultValuesSupplier;

    public TableWithImportWizardAction(ListComponent target, CollectionContainer targetContainer, ScreenData screenData, Supplier<Map<String, Object>> defaultValuesSupplier) {
        super("startImport");
        this.target = target;
        this.targetContainer = targetContainer;
        this.screenData = screenData;
        this.defaultValuesSupplier = defaultValuesSupplier;
        withImportWizardExecution.initIcon(this);
        withImportWizardExecution.initCaption(this);
    }

    @Override
    public void actionPerform(Component component) {
        withImportWizardExecution.openImportWizard(target, targetContainer, screenData, defaultValuesSupplier);
    }

}
