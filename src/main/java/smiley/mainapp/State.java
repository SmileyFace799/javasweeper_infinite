package smiley.mainapp;

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface State extends MouseListener, MouseMotionListener, KeyListener {
  void onLoad();
  void update();
  void onUnload();
  void drawScreen(Graphics2D g2);
}
