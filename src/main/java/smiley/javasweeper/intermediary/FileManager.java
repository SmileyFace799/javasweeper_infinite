package smiley.javasweeper.intermediary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.intermediary.events.file.AppLaunchedEvent;
import smiley.javasweeper.intermediary.events.file.FileEvent;
import smiley.javasweeper.intermediary.events.file.SettingUpdatedEvent;
import smiley.javasweeper.intermediary.events.file.SettingsLoadedEvent;
import smiley.javasweeper.intermediary.events.file.StartupFinishedEvent;

public class FileManager {
    private static final String SETTINGS_PATH = "settings.json";

    private static FileManager instance;

    private final List<FileEventListener> listeners;

    private Settings settings;

    private FileManager() {
        this.settings = null;
        this.listeners = new ArrayList<>();
    }

    public static synchronized FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    public void addListener(FileEventListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(FileEvent fe) {
        listeners.forEach(listener -> listener.onEvent(fe));
    }

    public void appStarted() {
        notifyListeners(new AppLaunchedEvent());
    }

    public void startupFinished() {
        notifyListeners(new StartupFinishedEvent());
    }

    public void loadSettings() {
        try {
            this.settings = new Settings(SETTINGS_PATH);
            notifyListeners(new SettingsLoadedEvent(settings));
        } catch (IOException ioe) {
            Logger.getLogger(getClass().getName()).log(
                    Level.WARNING,
                    String.format("Unable to load settings from file \"%s\"", SETTINGS_PATH),
                    ioe
            );
        }
    }

    public void updateSetting(String setting, Object newValue) {
        if (newValue instanceof Boolean) {
            throw new IllegalArgumentException("For boolean settings, please use \"#toggleSetting(String)\"");
        }
        switch (setting) {
            case (Settings.Keys.DISPLAY_WIDTH) -> settings.setDisplayWidth((int) newValue);
            case (Settings.Keys.DISPLAY_HEIGHT) -> settings.setDisplayHeight((int) newValue);
            case (Settings.Keys.MINE_CHANCE) -> settings.setMineChance((double) newValue);
            case (Settings.Keys.BOARD_SCALE) -> settings.setBoardScale((double) newValue);
            case (Settings.Keys.UI_SCALE) -> settings.setUiScale((double) newValue);
            default -> throw new IllegalArgumentException(String.format("\"%s\" is not a valid setting", setting));
        }
        notifyListeners(new SettingUpdatedEvent(setting, newValue));
    }

    public void toggleSetting(String setting) {
        boolean newValue;
        if (Settings.Keys.FULLSCREEN.equals(setting)) {
            newValue = settings.toggleFullscreen();
        } else {
            throw new IllegalArgumentException(String.format("\"%s\" is not a valid boolean setting", setting));
        }
        notifyListeners(new SettingUpdatedEvent(setting, newValue));
    }
}
