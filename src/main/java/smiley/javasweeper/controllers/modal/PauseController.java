package smiley.javasweeper.controllers.modal;

import java.awt.event.KeyEvent;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.modals.PauseModal;

public class PauseController extends GenericModalController{
    private final PauseModal modal;

    public PauseController(PauseModal modal, GamePanel app) {
        super(app);
        this.modal = modal;
    }

    @Override
    public PauseModal getView() {
        return modal;
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
            getView().getParent().closeModal();
        }
    }
}
