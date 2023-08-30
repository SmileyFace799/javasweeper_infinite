package smiley.javasweeper.controllers.mouse;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import smiley.javasweeper.view.GamePanel;

public class MouseButtonsOld implements MouseListener {
  public static final int LMB = MouseEvent.BUTTON1;
  public static final int WHEEL = MouseEvent.BUTTON2;
  public static final int RMB = MouseEvent.BUTTON3;

  final int[] mouseButtons = {LMB, WHEEL, RMB};
  private final Map<Mouse, Boolean> clicked = new HashMap<>();
  private final Map<Mouse, Boolean> pressed = new HashMap<>();
  private final Map<Mouse, Long> pressTime = new HashMap<>();
  private final Map<Mouse, Point> pressPos = new HashMap<>();
  private final GamePanel gp;

  //Constructor
  public MouseButtonsOld(GamePanel gp) {
    this.gp = gp;
    for (Mouse.Buttons button : Mouse.Buttons.values()) {
      clicked.put(button, false);
      pressed.put(button, false);
    }
  }

  public boolean getClicked(int mouseButton) {

  }

  //mutators
  public void frameUpdated() {
    for (int button : mouseButtons) {
      if (Boolean.TRUE.equals(clicked.get(button))) {
        clicked.put(button, false);
      }
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    int button = e.getButton();
    switch (button) {
      case MouseEvent.BUTTON1 -> clicked.put(LMB, true);
      case MouseEvent.BUTTON2 -> clicked.put(WHEEL, true);
      case MouseEvent.BUTTON3 -> clicked.put(RMB, true);
      default -> {
        //If no mouse button is clicked, do nothing
      }
    }
    gp.stateH.getActive().mouseClicked(e);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    int button = e.getButton();
    switch (button) {
      case MouseEvent.BUTTON1 -> {
        pressed.put(LMB, true);
        pressTime.put(LMB, System.nanoTime());
        pressPos.put(LMB, gp.uiH.scalePointToDisplay(e.getPoint()));
      }
      case MouseEvent.BUTTON2 -> {
        pressed.put(WHEEL, true);
        pressTime.put(WHEEL, System.nanoTime());
        pressPos.put(WHEEL, gp.uiH.scalePointToDisplay(e.getPoint()));

        gp.setStartDragCamera(new Point(gp.getCameraOffset()));
      }
      case MouseEvent.BUTTON3 -> {
        pressed.put(RMB, true);
        pressTime.put(RMB, System.nanoTime());
        pressPos.put(RMB, gp.uiH.scalePointToDisplay(e.getPoint()));
      }
      default -> {
        //If no mouse button is pressed, do nothing
      }
    }
    gp.stateH.getActive().mousePressed(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    int button = e.getButton();
    switch (button) {
      case MouseEvent.BUTTON1 -> {
        pressed.put(LMB, false);
        if (System.nanoTime() < pressTime.get(LMB) + (long) (0.1 * 1e9)) {
          clicked.put(LMB, true);
        }
      }
      case MouseEvent.BUTTON2 -> {
        pressed.put(WHEEL, false);
        if (System.nanoTime() < pressTime.get(WHEEL) + (long) (0.2 * 1e9)) {
          clicked.put(WHEEL, true);
        }
      }
      case MouseEvent.BUTTON3 -> {
        pressed.put(RMB, false);
        if (System.nanoTime() < pressTime.get(RMB) + (long) (0.15 * 1e9)) {
          clicked.put(RMB, true);
        }
      }
      default -> {
        //If no mouse button is released, do nothing
      }
    }
    gp.stateH.getActive().mouseReleased(e);
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    gp.stateH.getActive().mouseEntered(e);
  }

  @Override
  public void mouseExited(MouseEvent e) {
    gp.stateH.getActive().mouseExited(e);
  }
}
