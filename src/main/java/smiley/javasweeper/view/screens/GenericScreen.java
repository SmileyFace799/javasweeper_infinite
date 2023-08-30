package smiley.javasweeper.view.screens;

import java.awt.*;
import smiley.javasweeper.controllers.GenericController;

public abstract class GenericScreen {

    public abstract GenericController getController();

    /**
     * Draws this screen.
     *
     * @param g2 The {@link Graphics2D} object to draw to
     */
    public abstract void drawScreen(Graphics2D g2);
}
