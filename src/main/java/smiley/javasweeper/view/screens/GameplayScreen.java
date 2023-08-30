package smiley.javasweeper.view.screens;

import java.awt.*;
import java.awt.image.BufferedImage;
import smiley.javasweeper.controllers.GameplayController;
import smiley.javasweeper.intermediary.ModelEventListener;
import smiley.javasweeper.intermediary.events.ModelEvent;
import smiley.javasweeper.view.GamePanel;

public class GameplayScreen extends GenericScreen implements ModelEventListener {
    private final GameplayController controller;

    private BufferedImage boardImage;
    protected GameplayScreen(GamePanel app) {
        super();
        this.controller = new GameplayController(this, app);
    }

    @Override
    public GameplayController getController() {
        return controller;
    }

    @Override
    public void drawScreen(Graphics2D g2) {

    }

    @Override
    public void onEvent(ModelEvent me) {

    }
}
