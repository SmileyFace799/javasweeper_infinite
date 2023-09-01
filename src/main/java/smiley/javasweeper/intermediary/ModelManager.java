package smiley.javasweeper.intermediary;

import java.util.ArrayList;
import java.util.List;
import smiley.javasweeper.intermediary.events.AppStartedEvent;
import smiley.javasweeper.intermediary.events.BoardLoadedEvent;
import smiley.javasweeper.intermediary.events.ModelEvent;
import smiley.javasweeper.intermediary.events.SettingsLoadedEvent;
import smiley.javasweeper.intermediary.events.SquaresUpdatedEvent;
import smiley.javasweeper.model.Board;
import smiley.javasweeper.model.squares.NumberSquare;
import smiley.javasweeper.model.squares.Square;

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
        List<Square> updatedSquares = new ArrayList<>();
        if (!board.exists(x, y)) {
            board.generate(x, y);
        }
        if (!board.get(x, y).isRevealed()) {
            updatedSquares.addAll(board.reveal(x, y));
        }
        notifyListeners(new SquaresUpdatedEvent(updatedSquares));
    }

    public void flagBoardSquare(int x, int y) {
        Square square = board.get(x, y);
        List<Square> updatedSquares = new ArrayList<>();
        if (square != null && !square.isRevealed()) {
            square.toggleFlagged();
            updatedSquares.add(square);
        }
        notifyListeners(new SquaresUpdatedEvent(updatedSquares));
    }

    public void smartRevealSurrounding(int x, int y) {
        List<Square> updatedSquares = new ArrayList<>();
        if (board.exists(x, y)
                && board.get(x, y) instanceof NumberSquare numberSquare
                && numberSquare.isRevealed()
        ) {
            updatedSquares.addAll(board.massReveal(x, y));
        }
        notifyListeners(new SquaresUpdatedEvent(updatedSquares));
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
