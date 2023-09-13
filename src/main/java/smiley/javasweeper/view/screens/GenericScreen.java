package smiley.javasweeper.view.screens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import smiley.javasweeper.controllers.screen.GenericScreenController;
import smiley.javasweeper.view.GenericView;
import smiley.javasweeper.view.interfaces.Parent;
import smiley.javasweeper.view.components.GenericComponent;
import smiley.javasweeper.view.modals.GenericModal;

public abstract class GenericScreen implements GenericView, Parent {
    private GenericModal modal;
    private int modalX;
    private int modalY;

    private List<GenericComponent> components;
    private Map<GenericComponent, Integer> componentsX;
    private Map<GenericComponent, Integer> componentsY;

    protected GenericScreen() {
        this.components = new ArrayList<>();
        this.componentsX = new HashMap<>();
        this.componentsY = new HashMap<>();
    }

    public abstract GenericScreenController getController();

    public GenericModal getModal() {
        return modal;
    }

    public int getModalX() {
        return modalX;
    }

    public int getModalY() {
        return modalY;
    }

    public void placeModal(GenericModal modal, int x, int y) {
        if (modal.getParent() != null) {
            throw new IllegalArgumentException("modal already has a parent");
        }
        this.modal = modal;
        this.modalX = x;
        this.modalY = y;
        modal.setParent(this);
    }

    public void closeModal() {
        modal.setParent(null);
        this.modal = null;
        this.modalX = 0;
        this.modalY = 0;
    }

    public List<GenericComponent> getComponents() {
        return components;
    }

    public int getComponentX(GenericComponent component) {
        return componentsX.get(component);
    }

    public int getComponentY(GenericComponent component) {
        return componentsY.get(component);
    }

    public void placeComponent(GenericComponent component, int x, int y) {
        if (component.getParent() != null) {
            throw new IllegalArgumentException("component already has a parent");
        }
        components.add(component);
        componentsX.put(component, x);
        componentsY.put(component, y);
    }
}
