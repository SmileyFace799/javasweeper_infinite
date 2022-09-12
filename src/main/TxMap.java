package main;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;
import javax.imageio.ImageIO;

public class TxMap extends HashMap<String, BufferedImage> {
  final String classPath;
  final String imgPath; //Path to root img directory from class path

  //Constructor
  public TxMap(String imgPath) {
    this.imgPath = imgPath;
    URL classUrl = getClass().getResource("/");
    if (classUrl != null) {
      classPath = classUrl.getPath();
    } else {
      classPath = null;
      System.out.println("Could not identify the class path");
    }
    load();
  }

  //Accessors
  public BufferedImage getScaled(int size, String name) {
    return getScaled(size, size, name);
  }

  public BufferedImage getScaled(int width, int height, String name) {
    return getScaled(width, height, this.get(name));
  }

  public BufferedImage getScaled(int width, int height, BufferedImage image) {
    BufferedImage scaledImage = new BufferedImage(width, height, image.getType());
    Graphics2D g2 = scaledImage.createGraphics();
    g2.drawImage(image, 0, 0, width, height, null);
    return scaledImage;
  }

  //Mutators
  public void load() {
    addFiles(imgPath);
  }

  private void addFiles(String path) {
    for (File f : Objects.requireNonNull(new File(classPath + path).listFiles())) {
      if (f.isDirectory()) {
        addFiles(path + "/" + f.getName());
      } else {
        InputStream img = getClass().getResourceAsStream("/" + path + "/" + f.getName());
        if (img != null) {
          try {
            String[] splitFileName = f.getName().split("\\.");
            String[] splitTxName = new String[splitFileName.length - 1];
            System.arraycopy(splitFileName, 0, splitTxName, 0, splitTxName.length);
            String txName = String.join(".", splitTxName);
            this.put(txName, ImageIO.read(img));
          } catch (IOException e) {
            e.printStackTrace();
          }
        } else {
          System.out.println(
              "Could not find image: \"" + classPath + path + "/" + f.getName() + "\""
          );
        }
      }
    }
  }
}
