package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    private boolean escPressed = false;
    private boolean escTapped = false;
    final GamePanel gp;

    //Constructor
    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    //accessors
    public boolean getEscPressed() {return escPressed;}
    public boolean getEscTapped() {return escTapped;}

    //mutators
    public void frameUpdated() {
        if (escTapped) {
            escTapped = false;
        }
    }

    //other
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ESCAPE) {
            escPressed = true;
            escTapped = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ESCAPE) {
            escPressed = false;
        }
    }
}
