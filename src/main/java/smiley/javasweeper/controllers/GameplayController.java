package smiley.javasweeper.controllers;

import java.awt.event.MouseEvent;
import smiley.javasweeper.intermediary.ModelManager;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.screens.GameplayScreen;

public class GameplayController extends GenericController {
    private final GameplayScreen screen;

    private boolean isCameraBeingMoved;
    private int cameraMoveReferenceX;
    private int cameraMoveReferenceY;

    public GameplayController(GameplayScreen screen, GamePanel app) {
        super(app);
        this.screen = screen;
        this.isCameraBeingMoved = false;
        this.cameraMoveReferenceX = 0;
        this.cameraMoveReferenceY = 0;
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

    @Override
    public void mousePressed(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON2) {
            this.isCameraBeingMoved = true;
            this.cameraMoveReferenceX = me.getX();
            this.cameraMoveReferenceY = me.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON2) {
            this.isCameraBeingMoved = false;
            this.cameraMoveReferenceX = 0;
            this.cameraMoveReferenceY = 0;
        }
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (isCameraBeingMoved) {
            int cameraMoveDx = me.getX() - cameraMoveReferenceX;
            int cameraMoveDy = me.getY() - cameraMoveReferenceY;
            getScreen().setCameraOffset(
                    getScreen().getCameraOffsetX() + cameraMoveDx,
                    getScreen().getCameraOffsetY() + cameraMoveDy
            );
            this.cameraMoveReferenceX = getScreen().getCameraOffsetX();
            this.cameraMoveReferenceY = getScreen().getCameraOffsetX();
        }
    }
}
