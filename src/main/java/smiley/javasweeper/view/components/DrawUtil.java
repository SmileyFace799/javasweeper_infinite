package smiley.javasweeper.view.components;

import java.awt.*;
import org.jetbrains.annotations.NotNull;
import smiley.javasweeper.filestorage.Settings;

public class DrawUtil {
    private DrawUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Takes a graphics object and draws a horizontally centered string at a specified y-position
     *
     * @param g2  The graphics object to draw the string on
     * @param str The string to draw
     * @param y   The y-coordinate for the string's baseline
     */
    public static void drawStringCenteredX(@NotNull Graphics2D g2, String str, int y) {
        g2.drawString(str, (Settings.getDisplayWidth() - g2.getFontMetrics().stringWidth(str)) / 2, y);
    }

    public static void drawStringCentered(@NotNull Graphics2D g2, String str) {
        drawStringCenteredX(g2, str,
                (Settings.getDisplayHeight() - g2.getFontMetrics().getHeight()) / 2
        );
    }

    public static void drawStringRightAligned(@NotNull Graphics2D g2, String str, int rightX, int y) {
        g2.drawString(str, rightX - g2.getFontMetrics().stringWidth(str), y);
    }
}
