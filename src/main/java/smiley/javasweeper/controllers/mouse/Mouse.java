package smiley.javasweeper.controllers.mouse;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Mouse implements MouseListener, MouseMotionListener {
    private static Mouse instance;

    private static final Map<Integer, Double> CLICK_MERCY_TIME = Map.of(
            MouseEvent.BUTTON1, 0.1,
            MouseEvent.BUTTON2, 0.2,
            MouseEvent.BUTTON3, 0.15
    );

    private final List<MouseInteractionListener> listeners;

    private final Map<Integer, Long> pressTimes;

    private Mouse() {
        this.listeners = new ArrayList<>();
        pressTimes = new HashMap<>();
        pressTimes.put(MouseEvent.BUTTON1, -1L);
        pressTimes.put(MouseEvent.BUTTON2, -1L);
        pressTimes.put(MouseEvent.BUTTON3, -1L);
    }

    /**
     * Singleton.
     *
     * @return Singleton instance
     */
    public static synchronized Mouse getInstance() {
        if (instance == null) {
            instance = new Mouse();
        }
        return instance;
    }

    public void addListener(MouseInteractionListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(Consumer<MouseInteractionListener> notifier) {
        listeners.forEach(notifier);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        //Do nothing
    }

    @Override
    public void mousePressed(MouseEvent me) {
        pressTimes.put(me.getButton(), System.nanoTime());
        notifyListeners(listener -> listener.mousePressed(me));
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (System.nanoTime()
                < pressTimes.get(me.getButton())
                + (CLICK_MERCY_TIME.get(me.getButton()) * 1e9)
        ) {
            notifyListeners(listener -> listener.mouseClicked(me));
        }
        notifyListeners(listener -> listener.mouseReleased(me));
        pressTimes.put(me.getButton(), -1L);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        notifyListeners(listener -> listener.mouseEntered(me));
    }

    @Override
    public void mouseExited(MouseEvent me) {
        notifyListeners(listener -> listener.mouseExited(me));
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        notifyListeners(listeners -> listeners.mouseDragged(me));
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        notifyListeners(listener -> listener.mouseMoved(me));
    }
}