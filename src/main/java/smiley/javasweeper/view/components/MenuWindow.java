package smiley.javasweeper.view.components;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import smiley.javasweeper.controllers.UIHandler;
import smiley.javasweeper.textures.TxMap;

/**
 * Represents a menu window with two areas, one upper and lower area, and border
 *
 * @see UIHandler#makeSubWindow(int, int, int, int, int)
 */
public class MenuWindow {
  final BufferedImage img;
  final Rectangle boundingRect;
  final Rectangle upperRect;
  final Rectangle lowerRect;

  /**
   * Constructor for MenuWindow.
   * <br/><b>TxMap should contain the following keys & textures:</b>
   * <ul>
   *   <li>menuTopLeftCorner</li>
   *   <li>menuTopRightCorner</li>
   *   <li>menuMidLeftCorner</li>
   *   <li>menuMidRightCorner</li>
   *   <li>menuBottomLeftCorner</li>
   *   <li>menuBottomRightCorner</li>
   *   <li>menuTopEdge</li>
   *   <li>menuMidEdge</li>
   *   <li>menuBottomEdge</li>
   *   <li>menuTopLeftEdge</li>
   *   <li>menuTopRightEdge</li>
   *   <li>menuBottomLeftEdge</li>
   *   <li>menuBottomRightEdge</li>
   * </ul>
   * The following textures should have the same height:
   * <ul>
   *   <li>menuTopLeftCorner, menuTopEdge, menuTopRightCorner</li>
   *   <li>menuMidLeftCorner, menuMidEdge, menuMidRightCorner</li>
   *   <li>menuBottomLeftCorner, menuBottomEdge, menuBottomRightCorner</li>
   * </ul>
   * The following textures should have the same width:
   * <ul>
   *   <li>
   *     menuTopLeftCorner, menuTopLeftEdge, menuMidLeftCorner,
   *     menuBottomLeftEdge, menuBottomLeftCorner
   *   </li><li>
   *     menuTopRightCorner, menuTopRightEdge, menuMidRightCorner,
   *     menuBottomRightEdge, menuBottomRightCorner
   *   </li>
   * </ul>
   * The following textures should have a width of 1 pixel:
   * <ul><li>menuTopEdge, menuMidEdge, menuBottomEdge</li></ul>
   * The following textures should have a height of 1 pixel:
   * <ul><li>
   *   menuTopLeftEdge, menuTopRightEdge,
   *   menuBottomLeftEdge, menuBottomRightEdge
   * </li></ul>
   *
   * @param x           The x-coordinate of the window, measured in pixels from the left. Can take negative values
   * @param y           The y-coordinate of the window, measured in pixels from the top. Can take negative values
   * @param width       The <b>inner</b> width of the window. Must be 0 or greater
   * @param upperHeight The <b>inner</b> height of the top window. Must be 0 or greater
   * @param lowerHeight The <b>inner</b> height of the bottom window. Must be 0 or greater
   * @param scale       Multiplier to scale the textures with
   * @see TxMap
   */
  public MenuWindow(int x, int y, int width, int upperHeight, int lowerHeight, double scale) {
    if (width < 0) {
      throw new RuntimeException(
          "MenuWindow: Parameter \"width\" must be 0 or greater (received \"" + width + "\")"
      );
    } else if (upperHeight < 0) {
      throw new RuntimeException(
          "MenuWindow: Parameter \"upperHeight\" must be 0 or greater (received \"" + upperHeight + "\")"
      );
    } else if (lowerHeight < 0) {
      throw new RuntimeException(
          "MenuWindow: Parameter \"lowerHeight\" must be 0 or greater (received \"" + lowerHeight + "\")"
      );
    }

    //Getting every frame piece, scaled
    BufferedImage topLeftCorner = TxMap.getRelScaled(scale, "menuTopLeftCorner");
    BufferedImage topRightCorner = TxMap.getRelScaled(scale, "menuTopRightCorner");
    BufferedImage midLeftCorner = TxMap.getRelScaled(scale, "menuMidLeftCorner");
    BufferedImage midRightCorner = TxMap.getRelScaled(scale, "menuMidRightCorner");
    BufferedImage bottomLeftCorner = TxMap.getRelScaled(scale, "menuBottomLeftCorner");
    BufferedImage bottomRightCorner = TxMap.getRelScaled(scale, "menuBottomRightCorner");
    BufferedImage topEdge = TxMap.getRelScaled(1, scale, "menuTopEdge");
    BufferedImage midEdge = TxMap.getRelScaled(1, scale, "menuMidEdge");
    BufferedImage bottomEdge = TxMap.getRelScaled(1, scale, "menuBottomEdge");
    BufferedImage topLeftEdge = TxMap.getRelScaled(scale, 1, "menuTopLeftEdge");
    BufferedImage topRightEdge = TxMap.getRelScaled(scale, 1, "menuTopRightEdge");
    BufferedImage bottomLeftEdge = TxMap.getRelScaled(scale, 1, "menuBottomLeftEdge");
    BufferedImage bottomRightEdge = TxMap.getRelScaled(scale, 1, "menuBottomRightEdge");


    int fullWidth = topLeftCorner.getWidth()
        + width
        + topRightCorner.getWidth();
    int fullHeight = topLeftCorner.getHeight()
        + upperHeight
        + midLeftCorner.getHeight()
        + lowerHeight
        + bottomLeftCorner.getHeight();

    img = new BufferedImage(fullWidth, fullHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = img.createGraphics();
    g2.setPaint(new Color(189, 189, 189));
    g2.fillRect(0, 0, fullWidth, fullHeight);

    //Drawing the corners
    g2.drawImage(topLeftCorner, 0, 0, null);
    g2.drawImage(topRightCorner, topLeftCorner.getWidth() + width, 0, null);
    g2.drawImage(midLeftCorner, 0, topLeftCorner.getHeight() + upperHeight, null);
    g2.drawImage(midRightCorner, midLeftCorner.getWidth() + width,
        topRightCorner.getHeight() + upperHeight, null);
    g2.drawImage(bottomLeftCorner, 0,
        topLeftCorner.getHeight() + upperHeight + midLeftCorner.getHeight() + lowerHeight, null);
    g2.drawImage(bottomRightCorner, bottomLeftCorner.getWidth() + width,
        topRightCorner.getHeight() + upperHeight + midRightCorner.getHeight() + lowerHeight, null);

    //Drawing the edges
    for (int i = 0; i < width; i++) {
      g2.drawImage(topEdge, topLeftCorner.getWidth() + i, 0, null);
      g2.drawImage(midEdge, midLeftCorner.getWidth() + i,
          topLeftCorner.getHeight() + upperHeight, null);
      g2.drawImage(bottomEdge, bottomLeftCorner.getWidth() + i,
          topLeftCorner.getHeight() + upperHeight + midLeftCorner.getHeight() + lowerHeight, null);
    }
    for (int i = 0; i < upperHeight; i++) {
      g2.drawImage(topLeftEdge, 0, topLeftCorner.getHeight() + i, null);
      g2.drawImage(topRightEdge, topLeftCorner.getWidth() + width,
          topRightCorner.getHeight() + i, null);
    }
    for (int i = 0; i < lowerHeight; i++) {
      g2.drawImage(bottomLeftEdge, 0,
          topLeftCorner.getHeight() + upperHeight + midLeftCorner.getHeight() + i, null);
      g2.drawImage(bottomRightEdge, topLeftCorner.getWidth() + width,
          topRightCorner.getHeight() + upperHeight + midRightCorner.getHeight() + i, null);
    }

    //Creating bounding rectangles
    boundingRect = new Rectangle(x, y, fullWidth, fullHeight);
    upperRect = new Rectangle(
        x + topLeftCorner.getWidth(), y + topLeftCorner.getHeight(),
        width, upperHeight
    );
    lowerRect = new Rectangle(x + topLeftCorner.getWidth(),
        y + topLeftCorner.getHeight() + upperHeight + midLeftCorner.getHeight(),
        width, lowerHeight
    );
  }

  /**
   * Moves the MenuWindow
   *
   * @param deltaX Distance to be moved along the x-axis in pixels
   * @param deltaY Distance to be moved along the y-axis in pixels
   * @see #moveTo(int, int)
   */
  public void move(int deltaX, int deltaY) {
    Rectangle[] rects = {boundingRect, upperRect, lowerRect};
    for (Rectangle rect : rects) {
      rect.x += deltaX;
      rect.y += deltaY;
    }
  }

  /**
   * Moves the MenuWindow to a specific point. The reference point is the top left corner of the window.
   *
   * @param newX The new x-coordinate to move the window to
   * @param newY The new x-coordinate to move the window to
   * @see #move(int, int)
   */
  public void moveTo(int newX, int newY) {
    move(newX - boundingRect.x, newY - boundingRect.y);
  }

  /**
   * Draws the MenuWindow
   *
   * @param g2 The Graphics2D object to draw the window on
   */
  public void draw(@NotNull Graphics2D g2) {
    g2.drawImage(img, boundingRect.x, boundingRect.y, null);
  }
}
