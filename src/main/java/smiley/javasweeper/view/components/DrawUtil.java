package smiley.javasweeper.view.components;

import java.awt.*;
import java.awt.image.BufferedImage;
import org.jetbrains.annotations.NotNull;
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

    public static BufferedImage getScaledImage(BufferedImage image, double scaleMultiplier) {
        BufferedImage newBoardImage = GraphicManager.makeFormattedImage(
                (int) (image.getWidth() * scaleMultiplier),
                (int) (image.getHeight() * scaleMultiplier)
        );
        Graphics2D g2 = newBoardImage.createGraphics();
        g2.drawImage(image, 0, 0, newBoardImage.getWidth(), newBoardImage.getHeight(), null);
        g2.dispose();
        return newBoardImage;
    }

    /**
     * Takes a graphics object and draws a horizontally centered string at a specified y-position
     *
     * @param g2  The graphics object to draw the string on
     * @param str The string to draw
     * @param y   The y-coordinate for the string's baseline
     */
    public static void drawStringCenteredX(@NotNull Graphics2D g2, String str, int y, int width) {
        g2.drawString(str, getCenteredX(width, g2.getFontMetrics().stringWidth(str)), y);
    }

    public static void drawStringCenteredX(@NotNull Graphics2D g2, String str, int y) {
        drawStringCenteredX(g2, str, y, GraphicManager.getInstance().getWindowWidth());
    }

    public static void drawStringCentered(@NotNull Graphics2D g2, String str, int width, int height) {
        int fontHeight = g2.getFont().getSize();
        drawStringCenteredX(g2, str, getCenteredY(height, fontHeight) + fontHeight, width);
    }

    public static void drawStringCentered(@NotNull Graphics2D g2, String str) {
        drawStringCentered(
                g2, str,
                GraphicManager.getInstance().getWindowWidth(),
                GraphicManager.getInstance().getWindowHeight()
        );
    }

    public static void drawStringRightAligned(@NotNull Graphics2D g2, String str, int rightX, int y) {
        g2.drawString(str, rightX - g2.getFontMetrics().stringWidth(str), y);
    }
}
