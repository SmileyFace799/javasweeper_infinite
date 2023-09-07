package smiley.javasweeper.filestorage;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Settings {
    private static final MultiTypeMap<String> DEFAULTS = new MultiTypeMap<>(Map.of(
            Keys.DISPLAY_WIDTH, 1280,
            Keys.DISPLAY_HEIGHT, 720,
            Keys.MINE_CHANCE, 0.25,
            Keys.BOARD_SCALE, 1D,
            Keys.UI_SCALE, 1D,
            Keys.FULLSCREEN, false
    ));

    private final MultiTypeMap<String> map;

    public Settings(String filepath) throws IOException {
        this.map = new JsonMap(filepath);
    }

    public static <V> V getDefault(String key, Class<V> valueType) {
        return DEFAULTS.get(key, valueType);
    }

    private void save() {
        if (map instanceof JsonMap json) {
            try {
                json.save();
            } catch (IOException ioe) {
                Logger.getLogger(getClass().getName()).log(
                        Level.WARNING,
                        String.format("Unable to save settings to file \"%s\"", json.getFilePath()),
                        ioe
                );
            }
        }
    }

    public int getDisplayWidth() {
        return map.get(Keys.DISPLAY_WIDTH, Integer.class);
    }

    public void setDisplayWidth(int width) {
        if (width < 400) {
            throw new IllegalArgumentException("Settings.setDisplayWidth: Value must be 400 or greater (Got \"" + width + "\")");
        }
        map.put(Keys.DISPLAY_WIDTH, width);
        save();
    }

    public int getDisplayHeight() {
        return map.get(Keys.DISPLAY_HEIGHT, Integer.class);
    }

    public void setDisplayHeight(int height) {
        if (height < 300) {
            throw new IllegalArgumentException("Settings.setDisplayHeight: Value must be 300 or greater (Got \"" + height + "\")");
        }
        map.put(Keys.DISPLAY_HEIGHT, height);
        save();
    }

    public double getMineChance() {
        return map.get(Keys.MINE_CHANCE, Double.class);
    }

    public void setMineChance(double mineChance) {
        if (mineChance < 0 || mineChance > 1) {
            throw new IllegalArgumentException("Settings.setMineChance: Expected value between 0 and 1 [inclusive] (Got \"" + mineChance + "\")");
        }
        map.put(Keys.MINE_CHANCE, mineChance);
        save();
    }

    public double getBoardScale() {
        return map.get(Keys.BOARD_SCALE, Double.class);
    }

    public void setBoardScale(double boardScale) {
        if (boardScale <= 0) {
            throw new IllegalArgumentException(("Settings.setBoardScale: Expected a value greater than 0 (Got \"" + boardScale + "\")"));
        }
        map.put(Keys.BOARD_SCALE, boardScale);
        save();
    }

    public double getUiScale() {
        return map.get(Keys.UI_SCALE, Double.class);
    }

    public void setUiScale(double uiScale) {
        if (uiScale <= 0) {
            throw new IllegalArgumentException("Settings.setUiScale: Expected a value greater than 0 (Got \"" + uiScale + "\")");
        }
        map.put(Keys.UI_SCALE, uiScale);
        save();
    }

    public boolean isFullscreen() {
        return map.get(Keys.FULLSCREEN, Boolean.class);
    }

    public boolean toggleFullscreen() {
        boolean newValue = !isFullscreen();
        map.put(Keys.FULLSCREEN, newValue);
        save();
        return newValue;
    }

    public static class Keys {
        public static final String DISPLAY_WIDTH = "displayWidth";
        public static final String DISPLAY_HEIGHT = "displayHeight";
        public static final String MINE_CHANCE = "mineChance";
        public static final String BOARD_SCALE = "boardScale";
        public static final String UI_SCALE = "uiScale";
        public static final String FULLSCREEN = "fullscreen";

        private Keys() {
            throw new IllegalStateException("Utility class");
        }
    }
}
