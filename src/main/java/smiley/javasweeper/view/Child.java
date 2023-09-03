package smiley.javasweeper.view;

public interface Child {
    Parent getParent();

    int getParentX();

    int getParentY();

    void setParent(Parent parent);
}
