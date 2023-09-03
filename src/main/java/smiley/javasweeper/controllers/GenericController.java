package smiley.javasweeper.controllers;

import java.awt.event.KeyListener;
import smiley.javasweeper.controllers.mouse.InputListener;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.GenericView;

public abstract class GenericController implements InputListener, KeyListener {
    private final GamePanel app;

    protected GenericController(GamePanel app) {
        this.app = app;
    }

    public abstract GenericView getView();
}
