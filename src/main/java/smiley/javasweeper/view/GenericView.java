package smiley.javasweeper.view;

import java.awt.Graphics2D;
import smiley.javasweeper.controllers.GenericController;

public interface GenericView {
    GenericController getController();

    /**
     * Draws this screen.
     *
     * @param g2 The {@link Graphics2D} object to draw to
     */
    void draw(Graphics2D g2);
}
