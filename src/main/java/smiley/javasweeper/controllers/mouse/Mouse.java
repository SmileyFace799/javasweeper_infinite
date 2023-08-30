package smiley.javasweeper.controllers.mouse;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Mouse implements MouseMotionListener {
    private static Mouse instance;

    private final List<MouseInteractionListener> listeners;

    private int mouseX;
    private int mouseY;

    private Mouse() {
        this.listeners = new ArrayList<>();
        this.mouseX = 0;
        this.mouseY = 0;
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
    public void mouseDragged(MouseEvent me) {
        //Not implemented
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        this.mouseX = me.getX();
        this.mouseY = me.getY();
        notifyListeners(listener -> listener.mouseMoved(mouseX, mouseY));
    }

    public enum Buttons implements MouseListener {
        LMB(MouseEvent.BUTTON1, 0.1),
        WHEEL(MouseEvent.BUTTON2, 0.2),
        RMB(MouseEvent.BUTTON3, 0.15);

        private final int buttonInt;
        private final long clickMercyTime;
        private PressEvent pressed;

        Buttons(int buttonInt, double clickMercyTime) {
            this.buttonInt = buttonInt;
            this.clickMercyTime = (long) (clickMercyTime * 1e9);
            this.pressed = null;
        }

        @Override
        public void mouseClicked(MouseEvent me) {
            if (me.getButton() != buttonInt) {
                return;
            }
            getInstance().notifyListeners(listener -> listener.mouseClicked(pressed));
        }

        @Override
        public void mousePressed(MouseEvent me) {
            if (me.getButton() != buttonInt) {
                return;
            }
            this.pressed = new PressEvent(me.getX(), me.getY(), System.nanoTime());
            getInstance().notifyListeners(listener -> listener.mousePressed(pressed));
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            if (me.getButton() != buttonInt) {
                return;
            }
            if (System.nanoTime() < pressed.time() + clickMercyTime) {
                mouseClicked(me);
            }
            getInstance().notifyListeners(listener -> listener.mouseReleased(pressed));
            this.pressed = null;
        }

        @Override
        public void mouseEntered(MouseEvent me) {
            //NOt implemented
        }

        @Override
        public void mouseExited(MouseEvent me) {
            //Not implemented
        }
    }
}