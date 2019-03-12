package de.diedavids.cuba.dataimport.web.action;

import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.ScreenData;

import java.util.Map;
import java.util.function.Supplier;

public interface WithImportWizardExecution {

    String NAME = "ddcdi_WithImportWizardExecution";

    void initCaption(Action action);
    void initIcon(Action action);

    void openImportWizard(ListComponent target, CollectionContainer targetContainer, ScreenData screenData, Supplier<Map<String, Object>> defaultValuesSupplier);
}
