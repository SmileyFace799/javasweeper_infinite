package smiley.javasweeper.view.screens;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import smiley.javasweeper.controllers.screen.GenericScreenController;
import smiley.javasweeper.filestorage.BoardLoader;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.intermediary.ModelEventListener;
import smiley.javasweeper.intermediary.ModelManager;
import smiley.javasweeper.intermediary.events.AppLaunchedEvent;
import smiley.javasweeper.intermediary.events.ModelEvent;
import smiley.javasweeper.intermediary.events.SettingsLoadedEvent;
import smiley.javasweeper.intermediary.events.StartupFinishedEvent;
import smiley.javasweeper.model.Board;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.GraphicManager;
import smiley.javasweeper.view.ViewManager;
import smiley.javasweeper.view.components.DrawUtil;

public class StartupScreen extends GenericScreen implements ModelEventListener {
    private String statusString;

    public StartupScreen(GamePanel app) {
        super();
        ModelManager.getInstance().addListener(this);
        this.statusString = "Starting game...";
    }

    @Override
    public GenericScreenController getController() {
        return null;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setFont(GraphicManager.getInstance().getTitleFont());
        g2.setColor(Color.WHITE);
        DrawUtil.drawStringCentered(g2, statusString);
    }

    @Override
    public void onEvent(ModelEvent me) {
        if (me instanceof AppLaunchedEvent) {
            this.statusString = "loading settings...";
            Settings.getInstance().load();
            ModelManager.getInstance().loadSettings();
        } else if (me instanceof SettingsLoadedEvent) {
            this.statusString = "Loading board...";
            try {
                if (BoardLoader.boardExists("testBoard.board")) {
                    ModelManager.getInstance().setBoard(BoardLoader.loadBoard("testboard.board"));
                } else {
                    Board board = new Board("testBoard.board");
                    board.generate(0, 0, 0);
                    board.reveal(0, 0, 0);
                    ModelManager.getInstance().setBoard(board);
                }
            } catch (IOException ioe) {
                this.statusString = "Could not load board: " + ioe.getLocalizedMessage();
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, statusString, ioe);
            }
        } else if (me instanceof StartupFinishedEvent) {
            this.statusString = "Drawing board...";
            ViewManager.getInstance().changeScreen(GameplayScreen.class);
        }
    }
}
