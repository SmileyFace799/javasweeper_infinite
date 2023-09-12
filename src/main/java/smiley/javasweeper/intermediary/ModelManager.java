package smiley.javasweeper.intermediary;

import java.util.ArrayList;
import java.util.List;
import smiley.javasweeper.intermediary.events.file.BoardLoadedEvent;
import smiley.javasweeper.intermediary.events.model.ModelEvent;
import smiley.javasweeper.intermediary.events.model.SquaresUpdatedEvent;
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
        if (!board.containsKey(x, y)) {
            board.generate(x, y);
        }
        if (!board.get(x, y).isRevealed()) {
            notifyListeners(new SquaresUpdatedEvent(board, board.reveal(x, y)));
        }
    }

    public void flagBoardSquare(int x, int y) {
        Square square = board.get(x, y);
        if (square != null && !square.isRevealed()) {
            square.toggleFlagged();
            notifyListeners(new SquaresUpdatedEvent(board, List.of(square)));
        }
    }

    public void smartRevealSurrounding(int x, int y) {
        if (board.containsKey(x, y)
                && board.get(x, y) instanceof NumberSquare numberSquare
                && numberSquare.isRevealed()
        ) {
            notifyListeners(new SquaresUpdatedEvent(board, board.massReveal(x, y, false)));
        }
    }

    public void setBoard(Board board) {
        this.board = board;
        notifyListeners(new BoardLoadedEvent(board));
    }
}
