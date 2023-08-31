package smiley.javasweeper.intermediary;

import java.util.ArrayList;
import java.util.List;
import smiley.javasweeper.intermediary.events.AppStartedEvent;
import smiley.javasweeper.intermediary.events.BoardLoadedEvent;
import smiley.javasweeper.intermediary.events.ModelEvent;
import smiley.javasweeper.intermediary.events.SettingsLoadedEvent;
import smiley.javasweeper.model.Board;
import smiley.javasweeper.squares.NumberSquare;
import smiley.javasweeper.squares.Square;

public class ModelManager {
    private static ModelManager instance;

    private final List<ModelEventListener> listeners;

    private Board board;

    private ModelManager() {
        this.board = null;
        this.listeners = new ArrayList<>();
    }

    public static synchronized ModelManager getInstance() {
        if (instance == null) {
            instance = new ModelManager();
        }
        return instance;
    }

    public void addListener(ModelEventListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(ModelEvent me) {
        listeners.forEach(listener -> listener.onEvent(me));
    }

    public void revealBoardSquare(int x, int y) {
        if (!board.exists(x, y)) {
            board.generate(x, y);
        }
        board.reveal(x, y);
        //TODO: Notify listeners
    }

    public void flagBoardSquare(int x, int y) {
        Square square = board.get(x, y);
        if (square != null && !square.isRevealed()) {
            square.toggleFlagged();
        }
        //TODO: Notify listeners
    }

    public void smartRevealSurrounding(int x, int y) {
        if (board.exists(x, y)
                && board.get(x, y) instanceof NumberSquare numberSquare
                && numberSquare.isRevealed()
        ) {
            List<Square> surroundingSquares = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (i != 4) {
                    surroundingSquares.add(board.get(x - 1 + i % 3, y - 1 + i / 3));
                }
            }

            if (surroundingSquares
                    .stream()
                    .filter(Square::isFlagged)
                    .count() == numberSquare.getNumber()
            ) {
                surroundingSquares
                        .stream()
                        .filter(square -> !square.isFlagged())
                        .forEach(square -> board.reveal(square.getX(), square.getY()));
            }
        }
        //TODO: Notify listeners
    }

    public void appStarted() {
        notifyListeners(new AppStartedEvent());
    }

    public void loadSettings() {
        notifyListeners(new SettingsLoadedEvent());
    }

    public void setBoard(Board board) {
        this.board = board;
        notifyListeners(new BoardLoadedEvent(board));
    }
}
