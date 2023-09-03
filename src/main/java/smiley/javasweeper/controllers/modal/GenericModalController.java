package smiley.javasweeper.controllers.modal;

import smiley.javasweeper.controllers.GenericController;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.modals.GenericModal;

public abstract class GenericModalController extends GenericController {
    protected GenericModalController(GamePanel app) {
        super(app);
    }

    @Override
    public abstract GenericModal getView();
}
