package smiley.javasweeper.controllers.screen;

import smiley.javasweeper.controllers.GenericController;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.screens.GenericScreen;

public abstract class GenericScreenController extends GenericController {
    protected GenericScreenController(GamePanel app) {
        super(app);
    }

    @Override
    public abstract GenericScreen getView();
}
