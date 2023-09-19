package smiley.javasweeper.controllers;

import java.awt.event.KeyEvent;
import smiley.javasweeper.controllers.mouse.InputHandler;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.GenericView;

public abstract class GenericController extends InputHandler {
    private final GamePanel app;

    protected GenericController(GamePanel app) {
        this.app = app;
    }

    public abstract GenericView getView();

    @Override
    public void keyReleased(KeyEvent ke) {

    }
}
