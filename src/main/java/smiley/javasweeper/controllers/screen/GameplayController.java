package smiley.javasweeper.controllers.screen;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import smiley.javasweeper.controllers.mouse.InputHandler;
import smiley.javasweeper.intermediary.ModelManager;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.GraphicManager;
import smiley.javasweeper.view.ViewManager;
import smiley.javasweeper.view.DrawUtil;
import smiley.javasweeper.view.modals.PauseModal;
import smiley.javasweeper.view.screens.GameplayScreen;

public class GameplayController extends GenericScreenController {
    private final GameplayScreen screen;

    private boolean isCameraBeingMoved;
    private int cameraMoveReferenceX;
    private int cameraMoveReferenceY;

    public GameplayController(GameplayScreen screen, GamePanel app) {
        super(app);
        this.screen = screen;
        this.isCameraBeingMoved = false;
        this.cameraMoveReferenceX = 0;
        this.cameraMoveReferenceY = 0;
    }

    @Override
    public GameplayScreen getView() {
        return screen;
    }

    private int getClickedColumn(int cursorX) {
        return Math.floorDiv(cursorX + getView().getCameraOffsetX(), getView().getBoardImage().getTileSize());
    }

    private int getClickedRow(int cursorY) {
        return Math.floorDiv(cursorY + getView().getCameraOffsetY(), getView().getBoardImage().getTileSize());
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        int squareX = getClickedColumn(me.getX());
        int squareY = getClickedRow(me.getY());
        if (me.getButton() == MouseEvent.BUTTON2) {
            ModelManager.getInstance().smartRevealSurrounding(squareX, squareY);
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON2) {
            this.isCameraBeingMoved = true;
            this.cameraMoveReferenceX = me.getX();
            this.cameraMoveReferenceY = me.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        int squareX = getClickedColumn(me.getX());
        int squareY = getClickedRow(me.getY());
        switch (me.getButton()) {
            case (MouseEvent.BUTTON1) ->
                    ModelManager.getInstance().revealBoardSquare(squareX, squareY);
            case (MouseEvent.BUTTON2) -> {
                this.isCameraBeingMoved = false;
                this.cameraMoveReferenceX = 0;
                this.cameraMoveReferenceY = 0;
            }
            case (MouseEvent.BUTTON3) ->
                    ModelManager.getInstance().flagBoardSquare(squareX, squareY);
            default -> {
                //Do nothing
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        if (isCameraBeingMoved
                && InputHandler.getInstance().clickTimePassed(MouseEvent.BUTTON2)
                >= InputHandler.getClickMercyTime(MouseEvent.BUTTON2) / 2
        ) {
            int cameraMoveDx = cameraMoveReferenceX - me.getX();
            int cameraMoveDy = cameraMoveReferenceY - me.getY();
            getView().setCameraOffset(
                    getView().getCameraOffsetX() + cameraMoveDx,
                    getView().getCameraOffsetY() + cameraMoveDy
            );
            this.cameraMoveReferenceX -= cameraMoveDx;
            this.cameraMoveReferenceY -= cameraMoveDy;
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        super.keyReleased(ke);
        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
            PauseModal pauseModal = ViewManager.getInstance().getmodal(PauseModal.class);
            getView().placeModal(pauseModal,
                    DrawUtil.getCenteredX(
                            GraphicManager.getInstance().getWindowWidth(),
                            pauseModal.getWidth()
                    ),
                    DrawUtil.getCenteredY(
                            GraphicManager.getInstance().getWindowHeight(),
                            pauseModal.getHeight()
                    )
            );
        }
    }
}
