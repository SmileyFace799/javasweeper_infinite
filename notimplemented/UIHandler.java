package smiley.notimplemented;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.components.MenuWindow;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.view.components.Slider;

public class UIHandler {
  public static final int DEFAULT_FONT_SIZE = 16;
  public static final Color OVERLAY_COLOR = new Color(0, 0, 0, 63);
  public static final GraphicsEnvironment GRAPHICS_ENVIRONMENT = GraphicsEnvironment.getLocalGraphicsEnvironment();
  public static final GraphicsDevice GRAPHICS_DEVICE = GRAPHICS_ENVIRONMENT.getDefaultScreenDevice();
  public static final GraphicsConfiguration GRAPHICS_CONFIG = GRAPHICS_DEVICE.getDefaultConfiguration();

  private final GamePanel gp;

  public final int margin;
  public final Font defaultFont;
  public final int fontSize;
  public final int titleFontSize;
  public final Font font;
  public final Font titleFont;
  public final HashMap<Integer, Color> numColors = new HashMap<>();

  private final BufferedImage display;
  private final Graphics2D displayG2;
  private int windowWidth; //Width of the actual game window
  private int windowHeight; //Height of the actual game window

  public UIHandler(GamePanel gp) {
    this.gp = gp;

    double uiScale = Settings.getUiScale();
    this.margin = (int) Math.round(uiScale * 3);
    this.defaultFont = new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE);
    this.fontSize = (int) Math.round(uiScale * 8);
    this.titleFontSize = (int) Math.round(uiScale * 12);

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

    this.display = makeFormattedImage(Settings.getDisplayWidth(), Settings.getDisplayHeight());
    this.displayG2 = display.createGraphics();

    numColors.put(1, new Color(0, 0, 255));
    numColors.put(2, new Color(0, 127, 0));
    numColors.put(3, new Color(255, 0, 0));
    numColors.put(4, new Color(0, 0, 127));
    numColors.put(5, new Color(127, 0, 0));
    numColors.put(6, new Color(0, 127, 127));
    numColors.put(7, new Color(0, 0, 0));
    numColors.put(8, new Color(127, 127, 127));
  }

  public int getWindowWidth() {
    return windowWidth;
  }

  public int getWindowHeight() {
    return windowHeight;
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
    return (Settings.getDisplayWidth() - width) / 2;
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
    return (Settings.getDisplayHeight() - height) / 2;
  }

  /**
   * Takes an x-coordinate within the game window and scales it to the equivalent x-coordinate on the display image.
   * <br/> In cases where the display is stretched to fit the window, this makes a difference
   *
   * @param windowX The x-coordinate within the game window to scale
   * @return The x-coordinate scaled to the display
   */
  public int scaleXToDisplay(int windowX) {
    return Math.round(windowX * (Settings.getDisplayWidth() / (float) windowWidth));
  }

  /**
   * Takes a y-coordinate within the game window and scales it to the equivalent y-coordinate on the display image.
   * <br/> In cases where the display is stretched to fit the window, this makes a difference
   *
   * @param windowY The y-coordinate within the game window to scale
   * @return The y-coordinate scaled to the display
   */
  public int scaleYToDisplay(int windowY) {
    return Math.round(windowY * (Settings.getDisplayHeight() / (float) windowHeight));
  }

  /**
   * Takes a point within the game window and scales it to the equivalent point on the display image.
   * <br/> In cases where the display is stretched to fit the window, this makes a difference
   *
   * @param windowPoint The point within the game window to scale
   * @return The point scaled to the display
   */
  public Point scalePointToDisplay(Point windowPoint) {
    return new Point(scaleXToDisplay(windowPoint.x), scaleYToDisplay(windowPoint.y));
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
    return new MenuWindow(x, y, width, upperHeight, lowerHeight, Settings.getUiScale());
  }

  //Mutators
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
    int newX = window.getBoundingRect().x;
    int newY = window.getBoundingRect().y;
    if (centerX) {
      newX = getCenterX(window.getBoundingRect().width);
    }
    if (centerY) {
      newY = getCenterY((window.getBoundingRect().height));
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
   * @param x            The x-coordinate of the slider, measured in pixels from the left. Can take negative values
   * @param y            The y-coordinate of the slider, measured in pixels from the top. Can take negative values
   * @param width        The width of the slider, in pixels. Must be a positive value
   * @param height       The height of the slider, in pixels. Must be a positive value
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
    return new Slider(x, y, width, height, defaultValue, Settings.getUiScale());
  }





  public void setupScreen() {
    if (Settings.isFullscreen()) {
      Rectangle screenBounds = GRAPHICS_CONFIG.getBounds();
      windowWidth = screenBounds.width;
      windowHeight = screenBounds.height;
      if (GRAPHICS_DEVICE.getFullScreenWindow() == null) {
        gp.setWindowUndecorated(true);
        GRAPHICS_DEVICE.setFullScreenWindow(gp.getWindow());
      }
    } else {
      windowWidth = Settings.getDisplayWidth();
      windowHeight = Settings.getDisplayHeight();
      if (GRAPHICS_DEVICE.getFullScreenWindow() != null) {
        gp.setWindowUndecorated(false);
        GRAPHICS_DEVICE.setFullScreenWindow(null);
      }
    }
  }

  public void toggleFullscreen() {
    Settings.toggleFullscreen();
    setupScreen();
  }
}
