package smiley.javasweeper.view.modals;

import java.awt.*;
import smiley.javasweeper.controllers.modal.PauseController;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.GraphicManager;
import smiley.javasweeper.view.components.DrawUtil;

public class PauseModal extends GenericModal {
    private final PauseController controller;

    public PauseModal(GamePanel app) {
        super(
                150,
                GraphicManager.getInstance().getTitleFontSize() + 2 * GraphicManager.getInstance().getMargin(),
                100
        );
        this.controller = new PauseController(this, app);
    }

    @Override
    public PauseController getController() {
        return controller;
    }

    @Override
    protected void draw(Graphics2D upperG2, Graphics2D lowerG2) {
        upperG2.setColor(Color.BLACK);
        upperG2.setFont(GraphicManager.getInstance().getTitleFont());
        DrawUtil.drawStringCentered(upperG2, "Game Paused", getInnerWidth(), getUpperHeight());
    }
}
