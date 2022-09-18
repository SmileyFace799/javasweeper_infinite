package main;


import squares.NumberSquare;
import squares.Square;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;

/**
 * Represents a state where the game is being played
 *
 * @see State
 */
public class GameState implements State {
  final GamePanel gp;

  public GameState(GamePanel gp) {
    this.gp = gp;
  }

  @Override
  public void onLoad() {

  }

  @Override
  public void update() {
    for (String button : gp.mouseH.mouseButtons) {
      if (gp.mouseH.clicked.get(button)) {
        Point pressPos = gp.mouseH.pressPos.get(button);
        Point camOffset = gp.getCameraOffset();
        gp.clickedBoardPoints.get(button).setLocation(
            Math.floorDiv((pressPos.x + camOffset.x), gp.tileSize),
            Math.floorDiv((pressPos.y + camOffset.y), gp.tileSize)
        );
      }
    }

    if (gp.mouseH.clicked.get("lmb")) {
      if (!gp.board.exists(gp.clickedBoardPoints.get("lmb"))) {
        gp.board.generate(gp.clickedBoardPoints.get("lmb"));
      }
      gp.board.get(gp.clickedBoardPoints.get("lmb")).reveal();
    }
    if (gp.mouseH.clicked.get("rmb") && gp.board.exists(gp.clickedBoardPoints.get("rmb"))) {
      Square clickedSquare = gp.board.get(gp.clickedBoardPoints.get("rmb"));
      if (!clickedSquare.isRevealed()) {
        clickedSquare.flag();
      }
    } else if (gp.mouseH.clicked.get("wheel")) {
      Point pos = gp.clickedBoardPoints.get("wheel"); //pos: Location clicked by the mouse-wheel
      Square clickedSquare = gp.board.get(pos);
      if (
          gp.board.exists(pos)
              && clickedSquare instanceof NumberSquare clickedNum
              && clickedNum.isRevealed()
      ) {
        int flagCounter = 0; //Number of flags surrounding the number
        for (int x = pos.x - 1; x <= pos.x + 1; x++) {
          for (int y = pos.y - 1; y <= pos.y + 1; y++) {
            if (gp.board.get(x, y).isFlagged()) {
              flagCounter++;
            }
          }
        }
        if (clickedNum.getNumber() == flagCounter) {
          for (int x = pos.x - 1; x <= pos.x + 1; x++) {
            for (int y = pos.y - 1; y <= pos.y + 1; y++) {
              Square squareToReveal = gp.board.get(x, y);
              if (!squareToReveal.isFlagged()) {
                squareToReveal.reveal();
              }
            }
          }
        }
      }
    }
    for (String button : gp.mouseH.mouseButtons) {
      if (gp.mouseH.clicked.get(button)) {
        gp.board.save();
      }
    }

    if (gp.keyH.getEscTapped()) {
      gp.stateH.setActive(gp.STATE_PAUSED);
    }
  }

  @Override
  public void onUnload() {

  }

  @Override
  public void drawScreen(Graphics2D g2) {
    Point camOffset = gp.getCameraOffset();
    g2.drawImage(gp.board.getImage(),
        gp.board.getMinX() * gp.tileSize - camOffset.x,
        gp.board.getMinY() * gp.tileSize - camOffset.y,
        null
    );
  }

  @Override
  public void keyTyped(KeyEvent e) {

  }

  @Override
  public void keyPressed(KeyEvent e) {

  }

  @Override
  public void keyReleased(KeyEvent e) {

  }

  @Override
  public void mouseClicked(MouseEvent e) {

  }

  @Override
  public void mousePressed(MouseEvent e) {
    int button = e.getButton();
    switch (button) {
      case MouseEvent.BUTTON1 -> {
        gp.mouseH.pressed.put("lmb", true);
        gp.mouseH.pressTime.put("lmb", System.nanoTime());
        gp.mouseH.pressPos.put("lmb", e.getPoint());
      }
      case MouseEvent.BUTTON2 -> {
        gp.mouseH.pressed.put("wheel", true);
        gp.mouseH.pressTime.put("wheel", System.nanoTime());
        gp.mouseH.pressPos.put("wheel", e.getPoint());

        gp.setStartDragCamera(new Point(gp.getCameraOffset()));
      }
      case MouseEvent.BUTTON3 -> {
        gp.mouseH.pressed.put("rmb", true);
        gp.mouseH.pressTime.put("rmb", System.nanoTime());
        gp.mouseH.pressPos.put("rmb", e.getPoint());
      }
      default -> {
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {

  }

  @Override
  public void mouseEntered(MouseEvent e) {

  }

  @Override
  public void mouseExited(MouseEvent e) {

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
