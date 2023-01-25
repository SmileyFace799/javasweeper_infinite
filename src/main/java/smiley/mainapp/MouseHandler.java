package smiley.mainapp;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

public class MouseHandler implements MouseListener {
  public static final int LMB = MouseEvent.BUTTON1;
  public static final int WHEEL = MouseEvent.BUTTON2;
  public static final int RMB = MouseEvent.BUTTON3;

  final int[] mouseButtons = {LMB, WHEEL, RMB};
  public final Map<Integer, Boolean> clicked = new HashMap<>();
  public final Map<Integer, Boolean> pressed = new HashMap<>();
  public final Map<Integer, Long> pressTime = new HashMap<>();
  public final Map<Integer, Point> pressPos = new HashMap<>();
  public final GamePanel gp;

  //Constructor
  public MouseHandler(GamePanel gp) {
    this.gp = gp;
    for (int button : mouseButtons) {
      clicked.put(button, false);
      pressed.put(button, false);
    }
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
