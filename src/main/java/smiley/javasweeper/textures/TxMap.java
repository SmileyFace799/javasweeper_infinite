package smiley.javasweeper.textures;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import smiley.javasweeper.view.GraphicManager;

public class TxMap {

    private TxMap() {
        throw new IllegalStateException("Utility class");
    }

    public static BufferedImage get(String path) {
        InputStream is = TxMap.class.getResourceAsStream(path);
        if (is == null) {
            throw new IllegalArgumentException(String.format("Resource \"%s\" not found", path));
        }
        BufferedImage image;
        try {
            image = GraphicManager.getFormattedImage(ImageIO.read(is));
        } catch (IOException ioe) {
            throw new IllegalArgumentException(String.format(
                    "Resource \"%s\" is not an image", path
            ));
        }
        return image;
    }

    public static BufferedImage getScaled(int size, String path) {
        return getScaled(size, size, path);
    }

    public static BufferedImage getScaled(int width, int height, String path) {
        return getScaled(width, height, get(path));
    }

    public static BufferedImage getScaled(int width, int height, BufferedImage image) {
        BufferedImage scaledImage = new BufferedImage(width, height, image.getType());
        Graphics2D g2 = scaledImage.createGraphics();
        g2.drawImage(image, 0, 0, width, height, null);
        g2.dispose();
        return scaledImage;
    }

    public static BufferedImage getRelScaled(double sizeMultiplier, String path) {
        return getRelScaled(sizeMultiplier, sizeMultiplier, path);
    }

    public static BufferedImage getRelScaled(double widthMultiplier, double heightMultiplier, String path) {
        BufferedImage originalImage = get(path);
        return getScaled(
                (int) Math.round(originalImage.getWidth() * widthMultiplier),
                (int) Math.round(originalImage.getHeight() * heightMultiplier),
                originalImage
        );
    }
}
