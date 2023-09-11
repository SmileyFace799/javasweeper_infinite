package smiley.javasweeper.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.intermediary.FileEventListener;
import smiley.javasweeper.intermediary.FileManager;
import smiley.javasweeper.intermediary.events.file.FileEvent;
import smiley.javasweeper.intermediary.events.file.SettingUpdatedEvent;
import smiley.javasweeper.intermediary.events.file.SettingsLoadedEvent;
import smiley.javasweeper.view.screens.GenericScreen;

public class GraphicManager implements FileEventListener {
    public static final int DEFAULT_WINDOW_WIDTH = Settings.getDefault(Settings.Keys.DISPLAY_WIDTH, Integer.class);
    public static final int DEFAULT_WINDOW_HEIGHT = Settings.getDefault(Settings.Keys.DISPLAY_HEIGHT, Integer.class);
    public static final int DEFAULT_FONT_SIZE = 16;
    public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE);
    public static final String FONT_PATH = "font/font.ttf";
    public static final Color OVERLAY_COLOR = new Color(0, 0, 0, 63);

    public static final GraphicsEnvironment GRAPHICS_ENVIRONMENT = GraphicsEnvironment.getLocalGraphicsEnvironment();
    public static final GraphicsDevice GRAPHICS_DEVICE = GRAPHICS_ENVIRONMENT.getDefaultScreenDevice();
    public static final GraphicsConfiguration GRAPHICS_CONFIG = GRAPHICS_DEVICE.getDefaultConfiguration();

    private static final Map<Integer, Color> numberColors = Map.of(
            1, new Color(0, 0, 255),
            2, new Color(0, 127, 0),
            3, new Color(255, 0, 0),
            4, new Color(0, 0, 127),
            5, new Color(127, 0, 0),
            6, new Color(0, 127, 127),
            7, new Color(0, 0, 0),
            8, new Color(127, 127, 127)
    );

    private static GraphicManager instance;

    private final Font rawFont;
    private int margin;
    private int textFontSize;
    private int titleFontSize;
    private Font textFont;
    private Font titleFont;
    private int preferredWindowWidth;
    private int preferredWindowHeight;
    private int windowWidth;
    private int windowHeight;

    private int perFrameCounter;

    private GraphicManager() {
        FileManager.getInstance().addListener(this);

        Font loadedFont = null;
        InputStream fontStream = getClass().getResourceAsStream(FONT_PATH);
        if (fontStream != null) {
            try {
                loadedFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            } catch (FontFormatException | IOException e) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, String.format(
                        "Could not load font: \"%s\", resorting to default font", FONT_PATH
                ), e);
            }
        }
        rawFont = Objects.requireNonNullElse(loadedFont, DEFAULT_FONT);

        updateUiScale(Settings.getDefault(Settings.Keys.UI_SCALE, Double.class));

        this.preferredWindowWidth = DEFAULT_WINDOW_WIDTH;
        this.preferredWindowHeight = DEFAULT_WINDOW_HEIGHT;
        resetWindowSize();

        this.perFrameCounter = 0;
    }

    private void updateUiScale(double uiScale) {
        this.margin = (int) Math.round(uiScale * 3);
        this.textFontSize = (int) Math.round(uiScale * 8);
        this.titleFontSize = (int) Math.round(uiScale * 12);

        this.textFont = rawFont.deriveFont(Font.PLAIN, textFontSize);
        this.titleFont = rawFont.deriveFont(Font.PLAIN, titleFontSize);
    }

    /**
     * Singleton.
     *
     * @return Singleton instance
     */
    public static synchronized GraphicManager getInstance() {
        if (instance == null) {
            instance = new GraphicManager();
        }
        return instance;
    }

    public static Color getNumberColor(int number) {
        if (!numberColors.containsKey(number)){
            throw new IllegalArgumentException(String.format("No color for number \"%s\"", number));
        }
        return numberColors.get(number);
    }

    public int getMargin() {
        return margin;
    }

    public int getTextFontSize() {
        return textFontSize;
    }

    public int getTitleFontSize() {
        return titleFontSize;
    }

    public Font getTextFont() {
        return textFont;
    }

    public Font getTitleFont() {
        return titleFont;
    }

    public int getPreferredWindowWidth() {
        return preferredWindowWidth;
    }

    public int getPreferredWindowHeight() {
        return preferredWindowHeight;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowSize(int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;
    }

    public void resetWindowSize() {
        setWindowSize(preferredWindowWidth, preferredWindowHeight);
    }

    public void drawScreen(Graphics2D g2, boolean debugEnabled) {
        /*
        g2.transform(AffineTransform.getScaleInstance(
                (double) windowWidth / DEFAULT_WINDOW_WIDTH,
                (double) windowHeight / DEFAULT_WINDOW_HEIGHT
        ));
         */
        g2.setFont(textFont);
        g2.setColor(Color.black);
        g2.fillRect(0, 0, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);

        long drawStart = System.nanoTime();
        GenericView view = ViewManager.getInstance().getCurrentScreen();
        while (view != null) {
            view.draw(g2);
            Parent parentView = (Parent) view;
            parentView.getComponents().forEach(component -> component.draw(g2));
            view = parentView.getModal();

        }

        if (debugEnabled) {
            g2.setFont(DEFAULT_FONT);
            g2.setColor(Color.black);
            g2.fillRect(0, 0, 275, 70);
            g2.setColor(Color.white);
            long drawTime = System.nanoTime() - drawStart;
            g2.drawString("Draw time: " + (drawTime / 1e6) + "ms", 10, 20);
            g2.drawString("Effective FPS: " + (1e9 / drawTime), 10, 40);
            g2.drawString("Per frame counter: " + perFrameCounter, 10, 60);
        }
        this.perFrameCounter = 0;
    }

    public void incrementPerFrameCounter() {
        perFrameCounter++;
    }

    public static BufferedImage getFormattedImage(BufferedImage image) {
        BufferedImage convertedImage = makeFormattedImage(image.getWidth(), image.getHeight());
        Graphics2D g2 = convertedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return convertedImage;
    }

    /**
     * Makes an image that's formatted to the same format as the screen, making the drawing process way quicker.
     *
     * @param width  The width of the image in pixels
     * @param height The height of the image in pixels
     * @return The newly made image
     */
    public static BufferedImage makeFormattedImage(int width, int height) {
        return GRAPHICS_CONFIG.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
    }

    @Override
    public void onEvent(FileEvent fe) {
        if (fe instanceof SettingsLoadedEvent sle) {
            Settings settings = sle.settings();
            updateUiScale(settings.getUiScale());
            this.preferredWindowWidth = settings.getDisplayWidth();
            this.preferredWindowHeight = settings.getDisplayHeight();
            resetWindowSize();
        } else if (fe instanceof SettingUpdatedEvent sue) {
            switch (sue.setting()) {
                case (Settings.Keys.DISPLAY_WIDTH) -> setWindowSize(sue.value(Integer.class), getWindowHeight());
                case (Settings.Keys.DISPLAY_HEIGHT) -> setWindowSize(getWindowWidth(), sue.value(Integer.class));
                case (Settings.Keys.UI_SCALE) -> updateUiScale(sue.value(Double.class));
            }
        }
    }
}
