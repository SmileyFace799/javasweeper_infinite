package smiley.javasweeper.view.screens;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import smiley.javasweeper.controllers.screen.GameplayController;
import smiley.javasweeper.filestorage.BoardLoader;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.intermediary.ModelEventListener;
import smiley.javasweeper.intermediary.ModelManager;
import smiley.javasweeper.intermediary.events.BoardLoadedEvent;
import smiley.javasweeper.intermediary.events.ModelEvent;
import smiley.javasweeper.intermediary.events.SettingsLoadedEvent;
import smiley.javasweeper.intermediary.events.SquaresUpdatedEvent;
import smiley.javasweeper.model.Board;
import smiley.javasweeper.model.squares.BombSquare;
import smiley.javasweeper.model.squares.NumberSquare;
import smiley.javasweeper.model.squares.Square;
import smiley.javasweeper.textures.TxLoader;
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

    public GameplayScreen(GamePanel app) {
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

    private BufferedImage getSquareTx(Square square) {
        BufferedImage tx;
        if (square.isFlagged()) {
            tx = Textures.FLAG.get();
        } else if (!square.isRevealed()) {
            tx = Textures.HIDDEN.get();
        } else if (square instanceof NumberSquare numberSquare) {
            tx = Textures.number(numberSquare.getNumber()).get();
        } else if (square instanceof BombSquare) {
            tx = Textures.BOMB_DETONATED.get();
        } else {
            throw new IllegalStateException(
                    "Could not find suitable texture for square: " + square
            );
        }
        return tx;
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
                g2.drawImage(
                        getSquareTx(square),
                        (x - minX) * tileSize,
                        (y - minY) * tileSize,
                        null
                );
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
            int drawOffsetX = 0;
            int drawOffsetY = 0;
            if (boardDimensions.getMinX() < imageDimensions.getMinX()) {
                drawOffsetX = (imageDimensions.getMinX() - boardDimensions.getMinX()) * tileSize;
            }
            if (boardDimensions.getMinY() < imageDimensions.getMinY()) {
                drawOffsetY = (imageDimensions.getMinY() - boardDimensions.getMinY()) * tileSize;
            }
            newG2.drawImage(boardImage, drawOffsetX, drawOffsetY, null);
            newG2.dispose();
            boardImage = newImage;
            imageDimensions.expand(boardDimensions.getMinX(), boardDimensions.getMinY());
            imageDimensions.expand(boardDimensions.getMaxX(), boardDimensions.getMaxY());
        }

        Graphics2D g2 = boardImage.createGraphics();
        squares.forEach(square -> g2.drawImage(
                getSquareTx(square),
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
    public void draw(Graphics2D g2) {
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
            ModelManager.getInstance().startupFinished();
        } else if (me instanceof SquaresUpdatedEvent sue) {
            updateImage(sue.squares());
            try {
                BoardLoader.saveBoard(sue.board());
            } catch (IOException ioe) {
                Logger.getLogger(getClass().getName())
                        .log(Level.SEVERE, "Could not save board", ioe);
            }
        }
    }

    private enum Textures {
        ZERO("0"),
        ONE("1"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        SEVEN("7"),
        EIGHT("8"),

        HIDDEN("hidden"),
        FLAG("flag"),
        BOMB_DETONATED("bombDetonated"),
        BOMB_REVEALED("bombRevealed"),
        FLAG_WRONG("flagWrong");

        private static final String SQUARES_PATH = "squares";
        private static final String SQUARES_EXTENSION = ".bmp";

        private final String squareName;

        Textures(String squareName) {
            this.squareName = squareName;
        }

        public static Textures number(int number) {
            Textures texture;
            switch (number) {
                case 0 -> texture = ZERO;
                case 1 -> texture = ONE;
                case 2 -> texture = TWO;
                case 3 -> texture = THREE;
                case 4 -> texture = FOUR;
                case 5 -> texture = FIVE;
                case 6 -> texture = SIX;
                case 7 -> texture = SEVEN;
                case 8 -> texture = EIGHT;
                default -> throw new IllegalArgumentException(
                        String.format("No square texture for number \"%s\"", number)
                );
            }
            return texture;
        }

        public BufferedImage get() {
            return TxLoader.getScaled(getTileSize(),
                    String.format("%s/%s%s", SQUARES_PATH, squareName, SQUARES_EXTENSION)
            );
        }
    }
}
