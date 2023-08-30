package smiley.javasweeper.intermediary;

import java.util.ArrayList;
import java.util.List;
import smiley.javasweeper.intermediary.events.BoardLoadedEvent;
import smiley.javasweeper.intermediary.events.ModelEvent;
import smiley.javasweeper.model.Board;

public class ModelManager {
    private static ModelManager instance;

    private final List<ModelEventListener> listeners;

    private Board board;

    private ModelManager() {
        this.board = null;
        this.listeners = new ArrayList<>();
    }

    public void addListener(ModelEventListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(ModelEvent me) {
        listeners.forEach(listener -> listener.onEvent(me));
    }

    public void setBoard(Board board) {
        this.board = board;
        notifyListeners(new BoardLoadedEvent(board));
    }

    public static synchronized ModelManager getInstance() {
        if (instance == null) {
            instance = new ModelManager();
        }
        return instance;
    }
}
