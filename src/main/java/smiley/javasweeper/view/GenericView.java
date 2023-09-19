package smiley.javasweeper.view;

import javax.swing.JPanel;
import smiley.javasweeper.controllers.GenericController;
import smiley.javasweeper.view.modals.GenericModal;

public abstract class GenericView extends JPanel {
    private GenericModal modal;

    public abstract GenericController getController();

    protected void setControllerAsInputListener() {
        GenericController controller = getController();
        addKeyListener(controller);
        addMouseListener(controller);
        addMouseMotionListener(controller);
    }

    public GenericModal getModal() {
        return modal;
    }

    public void setModal(GenericModal modal) {
        this.modal = modal;
    }

    public void removeModal() {
        setModal(null);
    }
}
