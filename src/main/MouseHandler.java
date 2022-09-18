package main;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

public class MouseHandler implements MouseListener {
  final String[] mouseButtons = {"lmb", "rmb", "wheel"};
  final HashMap<String, Boolean> clicked = new HashMap<>();
  final HashMap<String, Boolean> pressed = new HashMap<>();
  final HashMap<String, Long> pressTime = new HashMap<>();
  final HashMap<String, Point> pressPos = new HashMap<>();
  final GamePanel gp;

  //Constructor
  public MouseHandler(GamePanel gp) {
    this.gp = gp;
    for (String button : mouseButtons) {
      clicked.put(button, false);
      pressed.put(button, false);
    }
  }

  //mutators
  public void frameUpdated() {
    for (String button : mouseButtons) {
      if (clicked.get(button)) {
        clicked.put(button, false);
      }
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    int button = e.getButton();
    switch (button) {
      case MouseEvent.BUTTON1 -> clicked.put("lmb", true);
      case MouseEvent.BUTTON2 -> clicked.put("wheel", true);
      case MouseEvent.BUTTON3 -> clicked.put("rmb", true);
      default -> {
      }
    }
    gp.stateH.getActive().mouseClicked(e);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    int button = e.getButton();
    switch (button) {
      case MouseEvent.BUTTON1 -> {
        pressed.put("lmb", true);
        pressTime.put("lmb", System.nanoTime());
        pressPos.put("lmb", e.getPoint());
      }
      case MouseEvent.BUTTON2 -> {
        pressed.put("wheel", true);
        pressTime.put("wheel", System.nanoTime());
        pressPos.put("wheel", e.getPoint());

        gp.setStartDragCamera(new Point(gp.getCameraOffset()));
      }
      case MouseEvent.BUTTON3 -> {
        pressed.put("rmb", true);
        pressTime.put("rmb", System.nanoTime());
        pressPos.put("rmb", e.getPoint());
      }
      default -> {
      }
    }
    gp.stateH.getActive().mousePressed(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    int button = e.getButton();
    switch (button) {
      case MouseEvent.BUTTON1 -> {
        pressed.put("lmb", false);
        if (System.nanoTime() < pressTime.get("lmb") + (long) (0.1 * 1e9)) {
          clicked.put("lmb", true);
        }
      }
      case MouseEvent.BUTTON2 -> {
        pressed.put("wheel", false);
        if (System.nanoTime() < pressTime.get("wheel") + (long) (0.2 * 1e9)) {
          clicked.put("wheel", true);
        }
      }
      case MouseEvent.BUTTON3 -> {
        pressed.put("rmb", false);
        if (System.nanoTime() < pressTime.get("rmb") + (long) (0.15 * 1e9)) {
          clicked.put("rmb", true);
        }
      }
      default -> {
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
