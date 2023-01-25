package smiley.mainapp;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;

public class TxMap {
  private static final HashMap<String, BufferedImage> IMG_MAP = new HashMap<>();
  private static final String IMG_FOLDER = "imgs/";
  static {
    load();
  }

  //Constructor
  private TxMap() {
    throw new IllegalStateException("Utility class");
  }

  //Accessors
  public static BufferedImage getScaled(int size, String name) {
    return getScaled(size, size, name);
  }

  public static BufferedImage getScaled(int width, int height, String name) {
    return getScaled(width, height, IMG_MAP.get(name));
  }

  public static BufferedImage getScaled(int width, int height, BufferedImage image) {
    BufferedImage scaledImage = new BufferedImage(width, height, image.getType());
    Graphics2D g2 = scaledImage.createGraphics();
    g2.drawImage(image, 0, 0, width, height, null);
    g2.dispose();
    return scaledImage;
  }

  public static BufferedImage getRelScaled(double sizeMultiplier, String name) {
    return getRelScaled(sizeMultiplier, sizeMultiplier, name);
  }

  public static BufferedImage getRelScaled(double widthMultiplier, double heightMultiplier, String name) {
    BufferedImage originalImage = IMG_MAP.get(name);
    return getScaled(
        (int) Math.round(originalImage.getWidth() * widthMultiplier),
        (int) Math.round(originalImage.getHeight() * heightMultiplier),
        originalImage
    );
  }

  //Mutators
  public static void load() {
    addFiles(IMG_FOLDER);
  }

  private static void put(String name, BufferedImage image) {
    BufferedImage convertedImage = UIHandler.makeFormattedImage(image.getWidth(), image.getHeight());
    Graphics2D g2 = convertedImage.createGraphics();
    g2.drawImage(image, 0, 0, null);
    g2.dispose();
    IMG_MAP.put(name, convertedImage);
  }

  private static void addFiles(String path) {
    for (File f : Objects.requireNonNull(new File("src/main/resources/" + path).listFiles())) {
      if (f.isDirectory()) {
        addFiles(path + f.getName() + "/");
      } else {
        InputStream img = TxMap.class.getResourceAsStream("../../" + path + f.getName());
        if (img != null) {
          try {
            String[] splitFileName = f.getName().split("\\.");
            String[] splitTxName = new String[splitFileName.length - 1];
            System.arraycopy(splitFileName, 0, splitTxName, 0, splitTxName.length);
            String txName = String.join(".", splitTxName);
            put(txName, ImageIO.read(img));
          } catch (IOException e) {
            e.printStackTrace();
          }
        } else {
          System.out.println(
              "Could not find image: \"" + path + f.getName() + "\""
          );
        }
      }
    }
  }

  public static void scale(String name, int size) {
    scale(name, size, size);
  }

  public static void scale(String name, int width, int height) {
    IMG_MAP.put(name, getScaled(width, height, name));
  }

  public static void scaleAll(int size) {
    scaleAll(size, size);
  }

  public static void scaleAll(int width, int height) {
    IMG_MAP.replaceAll((k, v) -> getScaled(width, height, v));
  }
}
