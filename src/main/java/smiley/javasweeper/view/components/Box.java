package smiley.javasweeper.view.components;

import java.awt.Graphics2D;
import java.util.List;
import smiley.javasweeper.view.Parent;
import smiley.javasweeper.view.modals.GenericModal;

public class Box extends GenericComponent implements Parent {
    @Override
    public void draw(Graphics2D g2) {

    }

    @Override
    public GenericModal getModal() {
        return null;
    }

    @Override
    public int getModalX() {
        return 0;
    }

    @Override
    public int getModalY() {
        return 0;
    }

    @Override
    public void placeModal(GenericModal modal, int x, int y) {

    }

    @Override
    public void closeModal() {

    }

    @Override
    public List<GenericComponent> getComponents() {
        return null;
    }

    @Override
    public int getComponentX(GenericComponent component) {
        return 0;
    }

    @Override
    public int getComponentY(GenericComponent component) {
        return 0;
    }

    @Override
    public void placeComponent(GenericComponent component, int x, int y) {

    }
}
