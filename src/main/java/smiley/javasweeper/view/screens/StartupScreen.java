package smiley.javasweeper.view.screens;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import smiley.javasweeper.controllers.screen.GenericScreenController;
import smiley.javasweeper.filestorage.BoardLoader;
import smiley.javasweeper.intermediary.FileEventListener;
import smiley.javasweeper.intermediary.FileManager;
import smiley.javasweeper.intermediary.ModelManager;
import smiley.javasweeper.intermediary.events.file.AppLaunchedEvent;
import smiley.javasweeper.intermediary.events.file.FileEvent;
import smiley.javasweeper.intermediary.events.file.SettingsLoadedEvent;
import smiley.javasweeper.intermediary.events.file.StartupFinishedEvent;
import smiley.javasweeper.model.Board;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.GraphicManager;
import smiley.javasweeper.view.ViewManager;
import smiley.javasweeper.view.DrawUtil;

public class StartupScreen extends GenericScreen implements FileEventListener {
    private String statusString;

    public StartupScreen(GamePanel app) {
        super();
        FileManager.getInstance().addListener(this);
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
    public void onEvent(FileEvent fe) {
        if (fe instanceof AppLaunchedEvent) {
            this.statusString = "loading settings...";
            FileManager.getInstance().loadSettings();
        } else if (fe instanceof SettingsLoadedEvent sle) {
            this.statusString = "Loading board...";
            try {
                if (BoardLoader.boardExists("testBoard.board")) {
                    ModelManager.getInstance().setBoard(BoardLoader.loadBoard("testboard.board"));
                } else {
                    Board board = new Board(sle.settings().getMineChance(), "testBoard.board");
                    board.generate(0, 0, 0);
                    board.reveal(0, 0, 0);
                    ModelManager.getInstance().setBoard(board);
                }
            } catch (IOException ioe) {
                this.statusString = "Could not load board: " + ioe.getLocalizedMessage();
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, statusString, ioe);
            }
        } else if (fe instanceof StartupFinishedEvent) {
            this.statusString = "Drawing board...";
            ViewManager.getInstance().changeScreen(GameplayScreen.class);
        }
    }
}
