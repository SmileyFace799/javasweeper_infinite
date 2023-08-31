package smiley.javasweeper.view.screens;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import smiley.javasweeper.controllers.GameplayController;
import smiley.javasweeper.controllers.UIHandler;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.intermediary.ModelEventListener;
import smiley.javasweeper.intermediary.events.BoardLoadedEvent;
import smiley.javasweeper.intermediary.events.ModelEvent;
import smiley.javasweeper.model.Board;
import smiley.javasweeper.squares.Square;
import smiley.javasweeper.view.GamePanel;

public class GameplayScreen extends GenericScreen implements ModelEventListener {
    private static final int ORIGINAL_TILE_SIZE = 16;

    private final GameplayController controller;

    private BufferedImage boardImage;
    private Graphics2D boardG2;
    private int cameraOffsetX;
    private int cameraOffsetY;
    private Board.Dimensions boardDimensions;

    protected GameplayScreen(GamePanel app) {
        super();
        this.controller = new GameplayController(this, app);
    }

    private static int getTileSize() {
        return (int) Math.round(ORIGINAL_TILE_SIZE * Settings.getBoardScale());
    }

    public int getCameraOffsetX() {
        return cameraOffsetX;
    }

    public int getCameraOffsetY() {
        return cameraOffsetY;
    }

    public void setCameraOffset(int offsetX, int offsetY) {
        this.cameraOffsetX = offsetX;
        this.cameraOffsetY = offsetY;
    }

    /**
     * Draws the initial board image.
     * Squares outside minX, minY, maxX & maxY will be out of bounds & not drawn
     */
    public void drawInitialImage(Board board) {
        int tileSize = getTileSize();
        this.boardDimensions = board.getDimensions();
        int minX = boardDimensions.getMinX();
        int minY = boardDimensions.getMinY();
        int maxX = boardDimensions.getMaxX();
        int maxY = boardDimensions.getMaxY();
        this.boardImage = UIHandler.makeFormattedImage(
                (1 + (maxX - minX)) * tileSize,
                (1 + (maxY - minY)) * tileSize
        );
        this.boardG2 = boardImage.createGraphics();
        for (Square square : board) {
            int x = square.getX();
            int y = square.getY();

            if (x < minX || x > maxX || y < minY || y > maxY) {
                System.out.println("x=" + x + " & y=" + y + ": Square is out of bounds");
            } else {
                boardG2.drawImage(square.getTx(), (x - minX) * tileSize, (y - minY) * tileSize, null);
            }
        }
        boardG2.dispose();
    }

    @Override
    public GameplayController getController() {
        return controller;
    }

    @Override
    public void drawScreen(Graphics2D g2) {
        g2.drawImage(boardImage,
                boardDimensions.getMinX() * Board.getTileSize() - cameraOffsetX,
                boardDimensions.getMinY() * Board.getTileSize() - cameraOffsetY,
                null
        );
    }

    @Override
    public void onEvent(ModelEvent me) {
        if (me instanceof BoardLoadedEvent ble) {
            drawInitialImage(ble.board());
        }
    }
}
