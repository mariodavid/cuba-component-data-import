package de.diedavids.cuba.dataimport.web.example.game;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.ListComponent;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.dataimport.entity.example.Game;
import de.diedavids.cuba.dataimport.web.WithImportWizard;

import javax.inject.Inject;
import java.util.Map;

@UiController("ddcdi_Game.browse")
@UiDescriptor("game-browse.xml")
@LookupComponent("gamesTable")
@LoadDataBeforeShow
public class GameBrowse extends StandardLookup<Game> implements WithImportWizard {

    @Inject
    protected GroupTable<Game> gamesTable;

    @Inject
    protected ButtonsPanel buttonsPanel;

    @Inject
    protected CollectionContainer<Game> gamesDc;

    @Inject
    protected CollectionLoader<Game> gamesDl;

    @Override
    public CollectionContainer getCollectionContainer() {
        return gamesDc;
    }

    @Override
    public ListComponent getListComponent() {
        return gamesTable;
    }

    @Override
    public ButtonsPanel getButtonsPanel() {
        return buttonsPanel;
    }

    @Override
    public Map<String, Object> getDefaultValues() {
        return ParamsMap.of("name", "foo", "name2", "blub");
    }
}