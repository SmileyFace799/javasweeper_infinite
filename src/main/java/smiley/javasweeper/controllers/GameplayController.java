package smiley.javasweeper.controllers;

import java.awt.event.MouseEvent;
import smiley.javasweeper.intermediary.ModelManager;
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

    private int getClickedColumn(int cursorX) {
        return Math.floorDiv(cursorX + getScreen().getCameraOffsetX(), GameplayScreen.getTileSize());
    }

    private int getClickedRow(int cursorY) {
        return Math.floorDiv(cursorY + getScreen().getCameraOffsetY(), GameplayScreen.getTileSize());
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        int squareX = getClickedColumn(me.getX());
        int squareY = getClickedRow(me.getY());
        switch (me.getButton()) {
            case (MouseEvent.BUTTON1) ->
                    ModelManager.getInstance().revealBoardSquare(squareX, squareY);
            case (MouseEvent.BUTTON3) ->
                    ModelManager.getInstance().flagBoardSquare(squareX, squareY);
            case (MouseEvent.BUTTON2) ->
                    ModelManager.getInstance().smartRevealSurrounding(squareX, squareY);
            default -> {
                //Do nothing
            }
        }
    }
}
