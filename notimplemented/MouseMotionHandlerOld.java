package smiley.notimplemented;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import smiley.javasweeper.view.GamePanel;

public class MouseMotionHandlerOld implements MouseMotionListener {
  final GamePanel gp;
  final Point mousePos = new Point(0, 0);

  //Constructor
  public MouseMotionHandlerOld(GamePanel gp) {
    this.gp = gp;
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    gp.stateH.getActive().mouseDragged(e);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    mousePos.setLocation(e.getX(), e.getY());
    gp.stateH.getActive().mouseMoved(e);
  }
}
