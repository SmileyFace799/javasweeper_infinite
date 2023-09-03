package smiley.javasweeper.controllers.mouse;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface InputListener extends MouseListener, MouseMotionListener, KeyListener {
    @Override
    default void keyTyped(KeyEvent e) {

    }

    @Override
    default void keyPressed(KeyEvent e) {

    }

    @Override
    default void keyReleased(KeyEvent e) {

    }

    @Override
    default void mouseClicked(MouseEvent e) {

    }

    @Override
    default void mousePressed(MouseEvent e) {

    }

    @Override
    default void mouseReleased(MouseEvent e) {

    }

    @Override
    default void mouseEntered(MouseEvent e) {

    }

    @Override
    default void mouseExited(MouseEvent e) {

    }

    @Override
    default void mouseDragged(MouseEvent e) {

    }

    @Override
    default void mouseMoved(MouseEvent e) {

    }
}
