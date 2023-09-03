package smiley.javasweeper.view.components;

import java.awt.Graphics2D;
import smiley.javasweeper.controllers.mouse.InputListener;
import smiley.javasweeper.view.Child;
import smiley.javasweeper.view.Parent;

public abstract class GenericComponent implements InputListener, Child {
    private Parent parent;

    public abstract void draw(Graphics2D g2);

    @Override
    public Parent getParent() {
        return parent;
    }

    @Override
    public int getParentX() {
        return getParent().getComponentX(this);
    }

    @Override
    public int getParentY() {
        return getParent().getComponentY(this);
    }

    @Override
    public void setParent(Parent parent) {
        this.parent = parent;
    }
}
