package smiley.javasweeper.view.modals;

import java.awt.*;
import smiley.javasweeper.controllers.modal.PauseController;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.GenericView;
import smiley.javasweeper.view.GraphicManager;
import smiley.javasweeper.view.DrawUtil;
import smiley.javasweeper.view.components.Button;

public class PauseModal extends GenericModal {
    private final PauseController controller;
    private static final int INNER_BASE_WIDTH = 150;

    public PauseModal(GamePanel app) {
        super(
                INNER_BASE_WIDTH,
                GraphicManager.getInstance().getTitleFontSize() + 2 * GraphicManager.getInstance().getMargin(),
                100
        );
        this.controller = new PauseController(this, app);

        int left = GraphicManager.getInstance().getMargin();
        int buttonWidth = INNER_BASE_WIDTH - left * 2;
        int buttonHeight = GraphicManager.getInstance().getTextFontSize();

        int top = left;
        Button resumeButton = new Button(buttonWidth, buttonHeight);
        resumeButton.setOnDraw((g2, scale) -> {
            g2.setFont(GraphicManager.getInstance().getTextFont());
            g2.setColor(GraphicManager.getNumberColor(1));
            DrawUtil.drawStringCenteredX(g2, "RESUME", (int) (buttonHeight * scale), (int) (buttonWidth * scale));
        });
        resumeButton.setOnClick(ie -> ((GenericView) getParent()).removeModal());
        //placeComponentLower(resumeButton, left, top);

        top += left + buttonHeight;
        Button quitGameButton = new Button(buttonWidth, buttonHeight);
        quitGameButton.setOnDraw((g2, scale) -> {
            g2.setFont(GraphicManager.getInstance().getTextFont());
            g2.setColor(GraphicManager.getNumberColor(3));
            DrawUtil.drawStringCenteredX(g2, "QUIT GAME", (int) (buttonHeight * scale), (int) (buttonWidth * scale));
        });
        quitGameButton.setOnClick(ie -> System.exit(0));
        //placeComponentLower(quitGameButton, left, top);
    }

    @Override
    public PauseController getController() {
        return controller;
    }

    @Override
    protected void paintComponent(Graphics2D upperG2, Graphics2D lowerG2) {
        upperG2.setColor(Color.BLACK);
        upperG2.setFont(GraphicManager.getInstance().getTitleFont());
        DrawUtil.drawStringCentered(upperG2, "Game Paused", getInnerWidth(), getUpperHeight());
    }
}
