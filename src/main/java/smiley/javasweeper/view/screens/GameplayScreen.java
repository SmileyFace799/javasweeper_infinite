package smiley.javasweeper.view.screens;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import smiley.javasweeper.controllers.GameplayController;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.intermediary.ModelEventListener;
import smiley.javasweeper.intermediary.ModelManager;
import smiley.javasweeper.intermediary.events.BoardLoadedEvent;
import smiley.javasweeper.intermediary.events.ModelEvent;
import smiley.javasweeper.intermediary.events.SettingsLoadedEvent;
import smiley.javasweeper.intermediary.events.SquaresUpdatedEvent;
import smiley.javasweeper.model.Board;
import smiley.javasweeper.model.squares.Square;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.GraphicManager;

public class GameplayScreen extends GenericScreen implements ModelEventListener {
    private static final int ORIGINAL_TILE_SIZE = 16;

    private final GameplayController controller;

    private BufferedImage boardImage;
    private int cameraOffsetX;
    private int cameraOffsetY;
    private Board.Dimensions boardDimensions;
    private Board.Dimensions imageDimensions;

    protected GameplayScreen(GamePanel app) {
        super();
        this.controller = new GameplayController(this, app);
        ModelManager.getInstance().addListener(this);
    }

    public static int getTileSize() {
        return (int) Math.round(ORIGINAL_TILE_SIZE * Settings.getInstance().getBoardScale());
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
    private void drawInitialImage(Board board) {
        int tileSize = getTileSize();
        this.boardDimensions = board.getDimensions();
        this.imageDimensions = boardDimensions.copy();
        int minX = boardDimensions.getMinX();
        int minY = boardDimensions.getMinY();
        int maxX = boardDimensions.getMaxX();
        int maxY = boardDimensions.getMaxY();
        this.boardImage = GraphicManager.makeFormattedImage(
                (1 + (maxX - minX)) * tileSize,
                (1 + (maxY - minY)) * tileSize
        );
        Graphics2D g2 = boardImage.createGraphics();
        for (Square square : board) {
            int x = square.getX();
            int y = square.getY();

            if (x < minX || x > maxX || y < minY || y > maxY) {
                System.out.println("x=" + x + " & y=" + y + ": Square is out of bounds");
            } else {
                g2.drawImage(square.getTx(), (x - minX) * tileSize, (y - minY) * tileSize, null);
            }
        }
        g2.dispose();
    }

    public void updateImage(List<Square> squares) {
        int tileSize = getTileSize();

        if (!boardDimensions.equals(imageDimensions)) {
            BufferedImage newImage = GraphicManager.makeFormattedImage(
                    (1 + boardDimensions.getMaxX() - boardDimensions.getMinX()) * tileSize,
                    (1 + boardDimensions.getMaxY() - boardDimensions.getMinY()) * tileSize
            );
            Graphics2D newG2 = newImage.createGraphics();

            if (boardDimensions.getMinX() < imageDimensions.getMinX()) {
                newG2.drawImage(boardImage, (imageDimensions.getMinX() - boardDimensions.getMinX()) * tileSize, 0, null);
            }
            if (boardDimensions.getMinY() < imageDimensions.getMinY()) {
                newG2.drawImage(boardImage, 0, (imageDimensions.getMinY() - boardDimensions.getMinY()) * tileSize, null);
            }
            if (boardDimensions.getMaxX() > imageDimensions.getMaxX() || boardDimensions.getMaxY() > imageDimensions.getMaxY()) {
                newG2.drawImage(boardImage, 0, 0, null);
            }
            newG2.dispose();
            boardImage = newImage;
            imageDimensions.expand(boardDimensions.getMinX(), boardDimensions.getMinY());
            imageDimensions.expand(boardDimensions.getMaxX(), boardDimensions.getMaxY());
        }
        Graphics2D g2 = boardImage.createGraphics();
        squares.forEach(square -> g2.drawImage(
                square.getTx(),
                (square.getX() - boardDimensions.getMinX()) * tileSize,
                (square.getY() - boardDimensions.getMinY()) * tileSize,
                null
        ));
        g2.dispose();
    }

    @Override
    public GameplayController getController() {
        return controller;
    }

    @Override
    public void drawScreen(Graphics2D g2) {
        if (boardDimensions != null) {
            g2.drawImage(boardImage,
                    boardDimensions.getMinX() * getTileSize() - cameraOffsetX,
                    boardDimensions.getMinY() * getTileSize() - cameraOffsetY,
                    null
            );
        }
    }

    @Override
    public void onEvent(ModelEvent me) {
        if (me instanceof SettingsLoadedEvent) {
            cameraOffsetX = -(Settings.getInstance().getDisplayWidth() - getTileSize()) / 2;
            cameraOffsetY = -(Settings.getInstance().getDisplayHeight() - getTileSize()) / 2;
        } else if (me instanceof BoardLoadedEvent ble) {
            drawInitialImage(ble.board());
        } else if (me instanceof SquaresUpdatedEvent sue) {
            updateImage(sue.squares());
        }
    }
}
