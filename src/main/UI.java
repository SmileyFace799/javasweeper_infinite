package main;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

public class UI {
  final GamePanel gp;
  final int margin;
  static final int DEFAULT_FONT_SIZE = 16;
  final Font defaultFont;
  final int fontSize;
  final int titleFontSize;
  final Font font;
  final Font titleFont;
  final HashMap<Integer, Color> numColors = new HashMap<>();
  final Color overlayColor = new Color(0, 0, 0, 63);

  private boolean isFullscreen;

  public UI(GamePanel gp) {
    this.gp = gp;
    this.margin = (int) Math.round(gp.uiScale * 3);
    this.defaultFont = new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE);
    this.fontSize = (int) Math.round(gp.uiScale * 8);
    this.titleFontSize = (int) Math.round(gp.uiScale * 12);

    Font loadedFont = null;
    InputStream fontStream = getClass().getResourceAsStream("/font/font.ttf");
    if (fontStream != null) {
      try {
        loadedFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
      } catch (FontFormatException | IOException e) {
        e.printStackTrace();
      }
    }
    loadedFont = Objects.requireNonNullElse(loadedFont, defaultFont);
    this.font = loadedFont.deriveFont(Font.PLAIN, fontSize);
    this.titleFont = loadedFont.deriveFont(Font.PLAIN, titleFontSize);

    numColors.put(1, new Color(0, 0, 255));
    numColors.put(2, new Color(0, 127, 0));
    numColors.put(3, new Color(255, 0, 0));
    numColors.put(4, new Color(0, 0, 127));
    numColors.put(5, new Color(127, 0, 0));
    numColors.put(6, new Color(0, 127, 127));
    numColors.put(7, new Color(0, 0, 0));
    numColors.put(8, new Color(127, 127, 127));

    isFullscreen = (boolean) gp.settings.get("fullscreen");
  }

  /**
   * Takes a width and returns the x-coordinate you'd need to draw
   * an object with this width at,
   * for it to be horizontally centered on the screen
   *
   * @param width The image being drawn on the screen
   * @return x-coordinate you'd need to draw this object at,
   * for it to be horizontally centered on the screen.
   * <br/>Can return negative values if the width is larger than the screen width
   * @see #getCenterY(int)
   * @see #drawStringCentered(Graphics2D, String, int)
   */
  public int getCenterX(int width) {
    return (gp.screenWidth - width) / 2;
  }

  /**
   * Takes a height and returns the y-coordinate you'd need to draw
   * an object with this height at,
   * for it to be vertically centered on the screen
   *
   * @param height The image being drawn on the screen
   * @return the y-coordinate you'd need to draw
   * an object with this height at,
   * for it to be vertically centered on the screen
   * <br/>Can return negative values if the height is larger than the screen height
   * @see #getCenterX(int)
   */
  public int getCenterY(int height) {
    return (gp.screenHeight - height) / 2;
  }

  /**
   * Returns a MenuWindow of specified dimensions
   * <br/><b>NB: Do not call every frame, this method can be very resource intensive</b>
   *
   * @param width       The <b>inner</b> width of the window
   * @param upperHeight The <b>inner</b> height of the top window
   * @param lowerHeight The <b>inner</b> height of the bottom window
   * @return MenuWindow of specified dimensions
   * @see MenuWindow
   */
  public MenuWindow makeSubWindow(int width, int upperHeight, int lowerHeight) {
    return makeSubWindow(0, 0, width, upperHeight, lowerHeight);
  }

  /**
   * Returns a MenuWindow of specified dimensions
   * <br/><b>NB: Do not call every frame, this method can be very resource intensive</b>
   *
   * @param x           The x-coordinate of the window, measured in pixels from the left. Can take negative values
   * @param y           The y-coordinate of the window, measured in pixels from the top. Can take negative values
   * @param width       The <b>inner</b> width of the window
   * @param upperHeight The <b>inner</b> height of the top window
   * @param lowerHeight The <b>inner</b> height of the bottom window
   * @return MenuWindow of specified dimensions
   * @see MenuWindow
   */
  public MenuWindow makeSubWindow(int x, int y, int width, int upperHeight, int lowerHeight) {
    return new MenuWindow(x, y, width, upperHeight, lowerHeight, gp.txMap, gp.uiScale);
  }

  /**
   * Centers a MenuWindow to the center of the screen
   *
   * @param window The MenuWindow to center
   */
  public void centerSubWindow(MenuWindow window) {
    centerSubWindow(window, true, true);
  }

  /**
   * Centers a MenuWindow to the center of the screen
   *
   * @param window  The MenuWindow to center
   * @param centerX If the window should be centered horizontally
   * @param centerY If the window should be centered vertically
   */
  public void centerSubWindow(MenuWindow window, boolean centerX, boolean centerY) {
    int newX = window.boundingRect.x;
    int newY = window.boundingRect.y;
    if (centerX) {
      newX = getCenterX(window.boundingRect.width);
    }
    if (centerY) {
      newY = getCenterY((window.boundingRect.height));
    }
    window.moveTo(newX, newY);
  }

  /**
   * Makes a slider with a default value of 0
   *
   * @param x      The x-coordinate of the slider, measured in pixels from the left. Can take negative values
   * @param y      The y-coordinate of the slider, measured in pixels from the top. Can take negative values
   * @param width  The width of the slider, in pixels. Must be a positive value
   * @param height The height of the slider, in pixels. Must be a positive value
   * @return The newly made slider
   * @see #makeSlider(int, int, int, int, double)
   * @see Slider
   */
  public Slider makeSlider(int x, int y, int width, int height) {
    return makeSlider(x, y, width, height, 0);
  }

  /**
   * Makes a slider
   *
   * @param x      The x-coordinate of the slider, measured in pixels from the left. Can take negative values
   * @param y      The y-coordinate of the slider, measured in pixels from the top. Can take negative values
   * @param width  The width of the slider, in pixels. Must be a positive value
   * @param height The height of the slider, in pixels. Must be a positive value
   * @param defaultValue The default value of the slider. Must be between 0 and 1
   * @return The newly made slider
   * @see #makeSlider(int, int, int, int)
   * @see Slider
   */
  public Slider makeSlider(int x, int y, int width, int height, double defaultValue) {
    if (width < 0) {
      throw new RuntimeException("UI.makeSlider: Expected positive width (Got \"" + width + "\")");
    } else if (height < 0) {
      throw new RuntimeException("UI.makeSlider: Expected positive height (Got \"" + height + "\")");
    } else if (defaultValue < 0 || defaultValue > 1) {
      throw new RuntimeException("UI.makeSlider: Expected defaultValue between 0 and 1 (Got \"" + defaultValue + "\")");
    }
    return new Slider(x, y, width, height, defaultValue, gp.uiScale);
  }

  /**
   * Takes a graphics object and draws a horizontally centered string at a specified y-position
   *
   * @param g2  The graphics object to draw the string on
   * @param str The string to draw
   * @param y   The y-coordinate for the string's baseline
   * @see #getCenterX(int)
   * @see Graphics2D#drawString(String, int, int)
   */
  public void drawStringCentered(@NotNull Graphics2D g2, String str, int y) {
    g2.drawString(str, (gp.screenWidth - g2.getFontMetrics().stringWidth(str)) / 2, y);
  }

  public void drawStringRightAligned(@NotNull Graphics2D g2, String str, int rightX, int y) {
    g2.drawString(str, rightX - g2.getFontMetrics().stringWidth(str), y);
  }

  public void setFullscreen(boolean makeFullscreen) {
    if (isFullscreen != makeFullscreen) {

    }
  }
}
