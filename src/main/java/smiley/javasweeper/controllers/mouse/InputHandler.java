package smiley.javasweeper.controllers.mouse;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

public class InputHandler implements MouseListener, MouseMotionListener, KeyListener {

    private static final Map<Integer, Double> CLICK_MERCY_TIME = Map.of(
            MouseEvent.BUTTON1, 0.2,
            MouseEvent.BUTTON2, 0.25,
            MouseEvent.BUTTON3, 0.2
    );

    private final Map<Integer, Long> pressTimes;

    public InputHandler() {
        pressTimes = new HashMap<>();
        pressTimes.put(MouseEvent.BUTTON1, -1L);
        pressTimes.put(MouseEvent.BUTTON2, -1L);
        pressTimes.put(MouseEvent.BUTTON3, -1L);
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
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (clickTimePassed(me.getButton()) < getClickMercyTime(MouseEvent.BUTTON2)) {
            mouseClicked(me);
        }
        pressTimes.put(me.getButton(), -1L);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        //Do nothing
    }

    @Override
    public void mouseExited(MouseEvent me) {
        //Do nothing
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        //Do nothing
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        //Do nothing
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        //Do nothing
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        //Do nothing
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        //Do nothing
    }
}