package smiley.javasweeper.controllers.mouse;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface InputListener extends MouseListener, MouseMotionListener, KeyListener {
    @Override
    default void keyTyped(KeyEvent ke) {

    }

    @Override
    default void keyPressed(KeyEvent ke) {

    }

    @Override
    default void keyReleased(KeyEvent ke) {

    }

    @Override
    default void mouseClicked(MouseEvent me) {

    }

    @Override
    default void mousePressed(MouseEvent me) {

    }

    @Override
    default void mouseReleased(MouseEvent me) {

    }

    @Override
    default void mouseEntered(MouseEvent me) {

    }

    @Override
    default void mouseExited(MouseEvent me) {

    }

    @Override
    default void mouseDragged(MouseEvent me) {

    }

    @Override
    default void mouseMoved(MouseEvent me) {

    }
}
