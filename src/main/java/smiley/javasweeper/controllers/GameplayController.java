package smiley.javasweeper.controllers;

import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.screens.GameplayScreen;

public class GameplayController extends GenericController {
    private final GameplayScreen screen;
    public GameplayController(GameplayScreen screen, GamePanel app) {
        super(app);
        this.screen = screen;
    }

    @Override
    public GameplayScreen getScreen() {
        return screen;
    }
}
