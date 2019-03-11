package de.diedavids.cuba.dataimport.web.example.game;

import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.dataimport.entity.example.Game;

@UiController("ddcdi_Game.edit")
@UiDescriptor("game-edit.xml")
@EditedEntityContainer("gameDc")
@LoadDataBeforeShow
public class GameEdit extends StandardEditor<Game> {
}