package smiley.javasweeper.view.screens;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import smiley.javasweeper.controllers.screen.GameplayController;
import smiley.javasweeper.filestorage.BoardLoader;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.intermediary.FileEventListener;
import smiley.javasweeper.intermediary.FileManager;
import smiley.javasweeper.intermediary.ModelEventListener;
import smiley.javasweeper.intermediary.ModelManager;
import smiley.javasweeper.intermediary.events.file.BoardLoadedEvent;
import smiley.javasweeper.intermediary.events.file.FileEvent;
import smiley.javasweeper.intermediary.events.file.SettingUpdatedEvent;
import smiley.javasweeper.intermediary.events.file.SettingsLoadedEvent;
import smiley.javasweeper.intermediary.events.model.ModelEvent;
import smiley.javasweeper.intermediary.events.model.SquaresUpdatedEvent;
import smiley.javasweeper.misc.Map2D;
import smiley.javasweeper.model.squares.BombSquare;
import smiley.javasweeper.model.squares.NumberSquare;
import smiley.javasweeper.model.squares.Square;
import smiley.javasweeper.textures.TxLoader;
import smiley.javasweeper.view.DrawUtil;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.GraphicManager;

public class GameplayScreen extends GenericScreen implements FileEventListener, ModelEventListener {
    private static final int ORIGINAL_TILE_SIZE = 16;

    private final GameplayController controller;
    private final BoardImage boardImage;
    private final ExecutorService threadExecutor;
    private int cameraOffsetX;
    private int cameraOffsetY;

    public GameplayScreen(GamePanel app) {
        super();
        this.controller = new GameplayController(this, app);
        setControllerAsInputListener();
        this.boardImage = new BoardImage();
        this.cameraOffsetX = 0;
        this.cameraOffsetY = 0;
        this.threadExecutor = Executors.newCachedThreadPool();
        FileManager.getInstance().addListener(this);
        ModelManager.getInstance().addListener(this);
    }

    public BoardImage getBoardImage() {
        return boardImage;
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

    @Override
    public GameplayController getController() {
        return controller;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        boardImage.stream().toList().forEach(chunk -> {
            int chunkX = chunk.getX();
            int chunkY = chunk.getY();
            if (chunkX < GraphicManager.getInstance().getWindowWidth() + cameraOffsetX
                    && chunkX + chunk.getWidth() > cameraOffsetX
                    && chunkY < GraphicManager.getInstance().getWindowHeight() + cameraOffsetY
                    && chunkY + chunk.getHeight() > cameraOffsetY
            ) {
                if (!chunk.isDrawn()) {
                    chunk.drawInitial();
                }
                g2.drawImage(chunk.getImage(),
                        chunkX - cameraOffsetX,
                        chunkY - cameraOffsetY,
                        null
                );
            }
        });
    }

    @Override
    public void onEvent(ModelEvent me) {
        if (me instanceof BoardLoadedEvent ble) {
            boardImage.drawSquares(ble.board().values().stream().toList());
            FileManager.getInstance().startupFinished();
        } else if (me instanceof SquaresUpdatedEvent sue) {
            List<Square> squares = sue.squares();
            if (!squares.isEmpty()) {
                boardImage.drawSquares(sue.squares());
                threadExecutor.submit(() -> {
                    try {
                        BoardLoader.saveBoard(sue.board());
                    } catch (IOException ioe) {
                        Logger.getLogger(getClass().getName())
                                .log(Level.SEVERE, "Could not save board", ioe);
                    }
                });
            }
        }
    }

    @Override
    public void onEvent(FileEvent fe) {
        if (fe instanceof SettingsLoadedEvent sle) {
            Settings settings = sle.settings();
            cameraOffsetX = -(settings.getDisplayWidth() - boardImage.getTileSize()) / 2;
            cameraOffsetY = -(settings.getDisplayHeight() - boardImage.getTileSize()) / 2;
            boardImage.setScale(settings.getBoardScale());
        } else if (fe instanceof SettingUpdatedEvent sue
                && Settings.Keys.BOARD_SCALE.equals(sue.setting())
        ) {
            boardImage.setScale(sue.value(Double.class));
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

        public BufferedImage get(int tileSize) {
            return TxLoader.getScaled(tileSize,
                    String.format("%s/%s%s", SQUARES_PATH, squareName, SQUARES_EXTENSION)
            );
        }
    }

    public class BoardImage extends Map2D<Integer, Integer, BoardImage.Chunk> {

        private double scale;
        private int tileSize;

        public BoardImage() {
            this.scale = 1;
            setScale(Settings.getDefault(Settings.Keys.BOARD_SCALE, Double.class));
        }

        public int getTileSize() {
            return tileSize;
        }

        public void drawSquares(List<Square> squares) {
            threadExecutor.submit(() -> squares.forEach(square -> {
                int chunkX = Math.floorDiv(square.getX(), Chunk.CHUNK_WIDTH);
                int chunkY = Math.floorDiv(square.getY(), Chunk.CHUNK_HEIGHT);

                computeIfAbsent(chunkX, chunkY, Chunk::new);
                get(chunkX, chunkY).drawSquare(square);
            }));
        }

        public synchronized void setScale(double scale) {
            double scaleMultiplier = scale / this.scale;
            forEach(chunk -> chunk.scale(scaleMultiplier));
            this.scale = scale;
            this.tileSize = (int) Math.round(ORIGINAL_TILE_SIZE * scale);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BoardImage chunks = (BoardImage) o;
            return Double.compare(chunks.scale, scale) == 0 && tileSize == chunks.tileSize;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), scale, tileSize);
        }

        private class Chunk {
            private static final int CHUNK_WIDTH = 100;
            private static final int CHUNK_HEIGHT = 100;

            private int top;
            private int left;
            private int width;
            private int height;
            private BufferedImage image;
            private Queue<Square> drawBuffer;

            public Chunk(int chunkX, int chunkY) {
                width = CHUNK_WIDTH * getTileSize();
                height = CHUNK_HEIGHT * getTileSize();

                this.top = chunkY * height;
                this.left = chunkX * width;
                this.image = GraphicManager.makeFormattedImage(width, height);
                this.drawBuffer = new LinkedList<>();
            }

            public int getX() {
                return left;
            }

            public int getY() {
                return top;
            }

            public int getWidth() {
                return width;
            }

            public int getHeight() {
                return height;
            }

            public BufferedImage getImage() {
                return image;
            }

            public boolean isDrawn() {
                return drawBuffer == null;
            }

            public void drawInitial() {
                if (drawBuffer == null) {
                    throw new IllegalStateException("This chunk is already drawn");
                }
                Queue<Square> buffer = drawBuffer;
                drawBuffer = null;
                threadExecutor.submit(() -> buffer.forEach(this::drawSquare));
            }

            private BufferedImage getSquareTx(Square square) {
                BufferedImage tx;
                if (square.isFlagged()) {
                    tx = Textures.FLAG.get(getTileSize());
                } else if (!square.isRevealed()) {
                    tx = Textures.HIDDEN.get(getTileSize());
                } else if (square instanceof NumberSquare numberSquare) {
                    tx = Textures.number(numberSquare.getNumber()).get(getTileSize());
                } else if (square instanceof BombSquare) {
                    tx = Textures.BOMB_DETONATED.get(getTileSize());
                } else {
                    throw new IllegalStateException(
                            "Could not find suitable texture for square: " + square
                    );
                }
                return tx;
            }

            public synchronized void drawSquare(Square square) {
                if (drawBuffer == null) {
                    int x = square.getX() * getTileSize() - left;
                    int y = square.getY() * getTileSize() - top;
                    Graphics2D g2 = image.createGraphics();
                    g2.drawImage(getSquareTx(square), x, y, null);
                    g2.dispose();
                    GraphicManager.getInstance().incrementPerFrameCounter();
                } else {
                    drawBuffer.add(square);
                }
            }

            public synchronized void scale(double scaleMultiplier) {
                this.top *= scaleMultiplier;
                this.left *= scaleMultiplier;
                this.width *= scaleMultiplier;
                this.height *= scaleMultiplier;
                this.image = DrawUtil.getScaledImage(image, scaleMultiplier);
            }
        }
    }
}
