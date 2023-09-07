package smiley.javasweeper.view.components;

import java.awt.Graphics2D;
import org.jetbrains.annotations.NotNull;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.view.GraphicManager;

public class DrawUtil {
    private DrawUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static int getCenteredX(int outerWidth, int innerWidth) {
        return (outerWidth - innerWidth) / 2;
    }

    public static int getCenteredY(int outerHeight, int innerHeight) {
        return (outerHeight - innerHeight) / 2;
    }

    /**
     * Takes a graphics object and draws a horizontally centered string at a specified y-position
     *
     * @param g2  The graphics object to draw the string on
     * @param str The string to draw
     * @param y   The y-coordinate for the string's baseline
     */
    public static void drawStringCenteredX(@NotNull Graphics2D g2, String str, int y) {
        g2.drawString(str, getCenteredX(
                GraphicManager.getInstance().getWindowWidth(),
                g2.getFontMetrics().stringWidth(str)
        ), y);
    }

    public static void drawStringCentered(@NotNull Graphics2D g2, String str) {
        drawStringCenteredX(g2, str, getCenteredY(
                GraphicManager.getInstance().getWindowHeight(),
                g2.getFontMetrics().getHeight()
        ));
    }

    public static void drawStringRightAligned(@NotNull Graphics2D g2, String str, int rightX, int y) {
        g2.drawString(str, rightX - g2.getFontMetrics().stringWidth(str), y);
    }
}
