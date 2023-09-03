package smiley.javasweeper.view;

import java.util.List;
import smiley.javasweeper.view.modals.GenericModal;
import smiley.javasweeper.view.modals.PauseModal;
import smiley.javasweeper.view.screens.GameplayScreen;
import smiley.javasweeper.view.screens.GenericScreen;
import smiley.javasweeper.view.screens.StartupScreen;

public class ViewManager {
    private static ViewManager instance;

    private boolean viewMade;
    private List<GenericScreen> screens;
    private GenericScreen currentScreen;
    private List<GenericModal> modals;

    private ViewManager() {
        this.viewMade = false;
    }
    // :)

    public static synchronized ViewManager getInstance() {
        if (instance == null) {
            instance = new ViewManager();
        }
        return instance;
    }

    public GenericScreen getCurrentScreen() {
        return currentScreen;
    }

    public <M extends GenericModal> M getmodal(Class<M> modalClass) {
        return modalClass.cast(modals
                .stream()
                .filter(modal -> modal.getClass() == modalClass)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No initialized modal of specified class"))
        );
    }

    /**
     * Makes any screens used in the application.
     *
     * @param app The main application
     * @throws IllegalStateException If the app has already made its screens
     */
    public void makeScreens(GamePanel app) {
        if (viewMade) {
            throw new IllegalStateException("Screens are already made");
        }

        screens = List.of(
                new StartupScreen(app),
                new GameplayScreen(app)
        );
        modals = List.of(
                new PauseModal(app)
        );
        this.viewMade = true;
    }

    public void changeScreen(Class<? extends GenericScreen> screenClass) {
        this.currentScreen = screens
                .stream()
                .filter(screen -> screen.getClass() == screenClass)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No initialized screen of specified class"));
    }
}
