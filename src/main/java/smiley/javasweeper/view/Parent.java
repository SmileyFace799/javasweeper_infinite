package smiley.javasweeper.view;

import java.util.List;
import smiley.javasweeper.view.components.GenericComponent;
import smiley.javasweeper.view.modals.GenericModal;

public interface Parent extends GenericView {
    GenericModal getModal();

    int getModalX();

    int getModalY();

    void placeModal(GenericModal modal, int x, int y);

    void closeModal();

    List<GenericComponent> getComponents();

    int getComponentX(GenericComponent component);

    int getComponentY(GenericComponent component);

    void placeComponent(GenericComponent component, int x, int y);
}
