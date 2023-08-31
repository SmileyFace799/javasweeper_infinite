package smiley.javasweeper.controllers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import smiley.javasweeper.controllers.keyboard.Keyboard;
import smiley.javasweeper.controllers.mouse.Mouse;
import smiley.javasweeper.controllers.mouse.MouseInteractionListener;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.screens.GenericScreen;
import smiley.javasweeper.view.screens.ScreenManager;

public abstract class GenericController implements MouseInteractionListener, KeyListener {
    private final GamePanel app;

    protected GenericController(GamePanel app) {
        this.app = app;
        Mouse.getInstance().addListener(this);
        Keyboard.getInstance().addListener(this);
    }

    public abstract GenericScreen getScreen();

    public void changeScreen(Class<? extends GenericScreen> screenClass) {
        ScreenManager.getInstance().changeScreen(screenClass);
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        //Do nothing by default
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        //Do nothing by default
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_F3) {
            //TODO: Toggle debug
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        //Do nothing by default
    }

    @Override
    public void mousePressed(MouseEvent me) {
        //Do nothing by default
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        //Do nothing by default
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //Do nothing by default
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //Do nothing by default
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //Do nothing by default
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        //Do nothing by default
    }
}
