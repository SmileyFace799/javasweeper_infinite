package smiley.javasweeper.controllers.keyboard;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Keyboard implements KeyListener {
    private static Keyboard instance;

    private final List<KeyListener> listeners;

    private Keyboard() {
        listeners = new ArrayList<>();
    }

    public static synchronized Keyboard getInstance() {
        if (instance == null) {
            instance = new Keyboard();
        }
        return instance;
    }

    public void addListener(KeyListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(Consumer<KeyListener> notifier) {
        listeners.forEach(notifier);
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        notifyListeners(listener -> listener.keyTyped(ke));
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        notifyListeners(listener -> listener.keyPressed(ke));
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        notifyListeners(listener -> listener.keyReleased(ke));
    }
}
