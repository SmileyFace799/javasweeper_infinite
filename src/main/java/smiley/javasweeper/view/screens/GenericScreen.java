package smiley.javasweeper.view.screens;

import smiley.javasweeper.controllers.screen.GenericScreenController;
import smiley.javasweeper.view.GenericView;

public abstract class GenericScreen extends GenericView {

    protected GenericScreen() {

    }

    public abstract GenericScreenController getController();
}
