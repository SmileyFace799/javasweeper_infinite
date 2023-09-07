package smiley.javasweeper.controllers;

import java.awt.event.KeyEvent;
import smiley.javasweeper.controllers.mouse.InputListener;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.intermediary.FileManager;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.GenericView;

public abstract class GenericController implements InputListener {
    private final GamePanel app;

    protected GenericController(GamePanel app) {
        this.app = app;
    }

    public abstract GenericView getView();

    @Override
    public void keyReleased(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_F11) {
            FileManager.getInstance().toggleSetting(Settings.Keys.FULLSCREEN);
        }
    }
}
