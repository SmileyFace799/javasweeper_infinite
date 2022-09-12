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
    if (gp.mouseH.pressed.get("wheel")
        && System.nanoTime() > gp.mouseH.pressTime.get("wheel") + (long) (0.15 * 1e9)
    ) {
      Point cameraOffset = new Point(gp.getStartDragCamera());
      Point startDragPos = gp.mouseH.pressPos.get("wheel");
      Point pos = e.getPoint();
      cameraOffset.x += startDragPos.x - pos.x;
      cameraOffset.y += startDragPos.y - pos.y;
      gp.setCameraOffset(cameraOffset);
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
  }
}
