package smiley.javasweeper.view.screens;

import java.util.List;
import smiley.javasweeper.view.GamePanel;

public class ScreenManager {
    private static ScreenManager instance;

    private boolean screensMade;
    private List<GenericScreen> screens;
    private GenericScreen currentScreen;

    private ScreenManager() {
        this.screensMade = false;
    }
    // :)

    public static synchronized ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    public GenericScreen getCurrentScreen() {
        return currentScreen;
    }

    /**
     * Makes any screens used in the application.
     *
     * @param app The main application
     * @throws IllegalStateException If the app has already made its screens
     */
    public void makeScreens(GamePanel app) {
        if (screensMade) {
            throw new IllegalStateException("Screens are already made");
        }

        screens = List.of(
                new GameplayScreen(app)
        );
        this.screensMade = true;
    }

    public void changeScreen(Class<? extends GenericScreen> screenClass) {
        this.currentScreen = screens
                .stream()
                .filter(screen -> screen.getClass() == screenClass)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No initialized screen of specified class"));
    }
}
