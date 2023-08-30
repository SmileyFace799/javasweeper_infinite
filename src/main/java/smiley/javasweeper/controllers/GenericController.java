package smiley.javasweeper.controllers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import smiley.javasweeper.controllers.keyboard.Keyboard;
import smiley.javasweeper.controllers.mouse.Mouse;
import smiley.javasweeper.controllers.mouse.MouseInteractionListener;
import smiley.javasweeper.controllers.mouse.PressEvent;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.screens.GenericScreen;
import smiley.javasweeper.view.screens.ScreenHandler;

public abstract class GenericController implements MouseInteractionListener, KeyListener {
    private final GamePanel app;

    protected GenericController(GamePanel app) {
        this.app = app;
        Mouse.getInstance().addListener(this);
        Keyboard.getInstance().addListener(this);
    }

    public abstract GenericScreen getScreen();

    public void changeScreen(Class<? extends GenericScreen> screenClass) {
        ScreenHandler.getInstance().changeScreen(screenClass);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //Do nothing by default
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //Do nothing by default
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //Do nothing by default
    }

    @Override
    public void mouseClicked(PressEvent pe) {
        //Do nothing by default
    }

    @Override
    public void mousePressed(PressEvent pe) {
        //Do nothing by default
    }

    @Override
    public void mouseReleased(PressEvent pe) {
        //Do nothing by default
    }

    @Override
    public void mouseMoved(int x, int y) {
        //Do nothing by default
    }
}
