package smiley.javasweeper.view.interfaces;

public interface Child {
    Parent getParent();

    int getParentX();

    int getParentY();

    void setParent(Parent parent);
}
