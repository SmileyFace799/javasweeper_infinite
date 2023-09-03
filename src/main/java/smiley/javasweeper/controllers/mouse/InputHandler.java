package smiley.javasweeper.controllers.mouse;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import smiley.javasweeper.view.GenericView;
import smiley.javasweeper.view.Parent;
import smiley.javasweeper.view.modals.GenericModal;
import smiley.javasweeper.view.ViewManager;
import smiley.javasweeper.view.screens.GenericScreen;

public class InputHandler implements MouseListener, MouseMotionListener, KeyListener {
    private static InputHandler instance;

    private static final Map<Integer, Double> CLICK_MERCY_TIME = Map.of(
            MouseEvent.BUTTON1, 0.2,
            MouseEvent.BUTTON2, 0.25,
            MouseEvent.BUTTON3, 0.2
    );

    private final Map<Integer, Long> pressTimes;

    private InputHandler() {
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
    public static synchronized InputHandler getInstance() {
        if (instance == null) {
            instance = new InputHandler();
        }
        return instance;
    }

    private void notifyControllers(Consumer<InputListener> notifier) {
        Parent view = ViewManager.getInstance().getCurrentScreen();
        while (view.getModal() != null) {
            view = view.getModal();
        }
        if (view.getController() != null) {
            notifier.accept(view.getController());
        }
        view.getComponents().forEach(notifier);
    }

    public static double getClickMercyTime(int button) {
        return CLICK_MERCY_TIME.get(button);
    }

    public double clickTimePassed(int button) {
        return (System.nanoTime() - pressTimes.get(button)) * 1e-9;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        //Do nothing
    }

    @Override
    public void mousePressed(MouseEvent me) {
        pressTimes.put(me.getButton(), System.nanoTime());
        notifyControllers(listener -> listener.mousePressed(me));
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (clickTimePassed(me.getButton()) < getClickMercyTime(MouseEvent.BUTTON2)) {
            notifyControllers(listener -> listener.mouseClicked(me));
        }
        notifyControllers(listener -> listener.mouseReleased(me));
        pressTimes.put(me.getButton(), -1L);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        notifyControllers(listener -> listener.mouseEntered(me));
    }

    @Override
    public void mouseExited(MouseEvent me) {
        notifyControllers(listener -> listener.mouseExited(me));
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        notifyControllers(listeners -> listeners.mouseDragged(me));
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        notifyControllers(listener -> listener.mouseMoved(me));
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        notifyControllers(listener -> listener.keyTyped(ke));
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        notifyControllers(listener -> listener.keyPressed(ke));
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        notifyControllers(listener -> listener.keyReleased(ke));
    }
}