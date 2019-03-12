package de.diedavids.cuba.dataimport.web;

import com.haulmont.cuba.core.global.BeanLocator;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.screen.*;
import de.balvi.cuba.declarativecontrollers.web.helper.ButtonsPanelHelper;
import de.diedavids.cuba.dataimport.web.action.TableWithImportWizardAction;

import java.util.Collections;
import java.util.Map;

public interface WithImportWizard {

    /**
     * defines the table component that will be used as a basis for the import wizard
     * @return the table
     */
    ListComponent getListComponent();

    /**
     * defines the collection container of the table that will be used as a basis for the import wizard
     * @return the collection container of the table
     */
    CollectionContainer getCollectionContainer();


    /**
     * the button id of the destination button
     *
     * It will either picked up from existing XML definitions or created with this identifier
     * @return the button identifier
     */
    default String getButtonId() {
        return "importWizardButton";
    }


    /**
     * defines the button panel that will be used for inserting the import button
     * @return the destination buttonPanel
     */
    default ButtonsPanel getButtonsPanel() {
        return null;
    }


    /**
     * defines default values for the entity that will be imported
     * @return Map of default values (keys are the attribute names of the entity)
     */
    default Map<String, Object> getDefaultValues() {
        return Collections.emptyMap();
    }

    @Subscribe
    default void initImportWizardButton(Screen.InitEvent event) {

        Screen screen = event.getSource();

        Button button = createOrGetButton(screen);

        initButtonWithImportWizardAction(screen, button);

    }

    default Button createOrGetButton(Screen screen) {
        BeanLocator beanLocator = Extensions.getBeanLocator(screen);
        ButtonsPanelHelper buttonsPanelHelper = beanLocator.get(ButtonsPanelHelper.NAME);

        return buttonsPanelHelper.createButton(getButtonId(), getButtonsPanel(), Collections.emptyList());
    }

    default void initButtonWithImportWizardAction(Screen screen, Button button) {
        Action action = new TableWithImportWizardAction(
                getListComponent(),
                getCollectionContainer(),
                UiControllerUtils.getScreenData(screen),
                this::getDefaultValues
        );

        button.setAction(action);
    }
}