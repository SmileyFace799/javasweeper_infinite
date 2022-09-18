package main;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseMotionHandler implements MouseMotionListener {
  final GamePanel gp;

  //Constructor
  public MouseMotionHandler(GamePanel gp) {
    this.gp = gp;
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    gp.stateH.getActive().mouseDragged(e);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    gp.stateH.getActive().mouseMoved(e);
  }
}
