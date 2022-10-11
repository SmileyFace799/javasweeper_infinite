package main;

import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;

public class KeyHandler implements KeyListener, Serializable {
  private boolean escPressed = false;
  private boolean escTapped = false;
  private final GamePanel gp;

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
  public void keyTyped(@NotNull KeyEvent e) {
    gp.stateH.getActive().keyTyped(e);
  }

  @Override
  public void keyPressed(@NotNull KeyEvent e) {
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
    gp.stateH.getActive().keyPressed(e);
  }

  @Override
  public void keyReleased(@NotNull KeyEvent e) {
    int code = e.getKeyCode();
    switch (code) {
      case (KeyEvent.VK_ESCAPE) -> escPressed = false;
      case (KeyEvent.VK_F11) -> gp.uiH.toggleFullscreen();
      default -> {
        //If nothing is released, don't do anything
      }
    }
    gp.stateH.getActive().keyReleased(e);
  }
}
