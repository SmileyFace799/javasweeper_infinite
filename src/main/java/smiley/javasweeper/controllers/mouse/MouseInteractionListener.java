package smiley.javasweeper.controllers.mouse;

public interface MouseInteractionListener {
    void mouseClicked(PressEvent pe);

    void mousePressed(PressEvent pe);

    void mouseReleased(PressEvent pe);

    void mouseMoved(int x, int y);
}
