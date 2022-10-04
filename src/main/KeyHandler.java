package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;

public class KeyHandler implements KeyListener, Serializable {
  private boolean escPressed = false;
  private boolean escTapped = false;
  final GamePanel gp;

  //Constructor
  public KeyHandler(GamePanel gp) {
    this.gp = gp;
  }

  //accessors
  public boolean getEscPressed() {
    return escPressed;
  }

  public boolean getEscTapped() {
    return escTapped;
  }

  //mutators
  public void frameUpdated() {
    if (escTapped) {
      escTapped = false;
    }
  }

  //other
  @Override
  public void keyTyped(KeyEvent e) {
    //Typing is never used
  }

  @Override
  public void keyPressed(KeyEvent e) {
    int code = e.getKeyCode();
    switch (code) {
      case KeyEvent.VK_ESCAPE -> {
        escPressed = true;
        escTapped = true;
      }
      case KeyEvent.VK_F3 -> gp.toggleDebug();
      default -> {
        //If nothing is pressed, don't do anything
      }
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
