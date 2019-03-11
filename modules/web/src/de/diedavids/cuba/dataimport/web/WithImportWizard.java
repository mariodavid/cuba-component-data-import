package de.diedavids.cuba.dataimport.web;

import com.haulmont.cuba.core.global.BeanLocator;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.ScreenData;
import com.haulmont.cuba.gui.screen.*;
import de.balvi.cuba.declarativecontrollers.web.helper.ButtonsPanelHelper;
import de.diedavids.cuba.dataimport.web.action.TableWithImportWizardAction;

import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public interface WithImportWizard {

    CollectionContainer getCollectionContainer();
    ListComponent getListComponent();

    default String getButtonId() {
        return "buttonId";
    }

    default ButtonsPanel getButtonsPanel() {
        return null;
    }

    default Map<String, Object> getDefaultValues() {
        return Collections.emptyMap();
    }


    @Subscribe
    default void initImportWizard(Screen.InitEvent event) {


        Screen screen = event.getSource();

        ScreenData screenData = UiControllerUtils.getScreenData(screen);

        BeanLocator beanLocator = Extensions.getBeanLocator(screen);
        ButtonsPanelHelper buttonsPanelHelper = beanLocator.get(ButtonsPanelHelper.NAME);

        Button button = buttonsPanelHelper.createButton(getButtonId(), getButtonsPanel(), Collections.emptyList());

        Supplier<Map<String, Object>> defaultValuesSupplier = this::getDefaultValues;

        Action action = new TableWithImportWizardAction(getListComponent(), getCollectionContainer(), screenData, defaultValuesSupplier);

        button.setAction(action);

    }
}