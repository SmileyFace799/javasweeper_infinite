package smiley.javasweeper.view.modals;

import java.awt.Graphics2D;
import smiley.javasweeper.controllers.modal.PauseController;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.GraphicManager;
import smiley.javasweeper.view.components.DrawUtil;

public class PauseModal extends GenericModal {
    private final PauseController controller;

    public PauseModal(GamePanel app) {
        super(
                (int) (150 * Settings.getInstance().getUiScale()),
                GraphicManager.getInstance().getTextFontSize() + GraphicManager.getInstance().getMargin(),
                (int) (100 * Settings.getInstance().getUiScale()),
                1
        );
        this.controller = new PauseController(this, app);
    }

    @Override
    public PauseController getController() {
        return controller;
    }

    @Override
    protected void draw(Graphics2D upperG2, Graphics2D lowerG2) {
        DrawUtil.drawStringCentered(upperG2, "Game Paused");
    }
}
