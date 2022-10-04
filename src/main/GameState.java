package main;


import squares.NumberSquare;
import squares.Square;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

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
    for (int button : gp.mouseH.mouseButtons) {
      if (Boolean.TRUE.equals(gp.mouseH.clicked.get(button))) {
        Point pressPos = gp.mouseH.pressPos.get(button);
        Point camOffset = gp.getCameraOffset();
        gp.clickedBoardPoints.get(button).setLocation(
            Math.floorDiv((pressPos.x + camOffset.x), gp.tileSize),
            Math.floorDiv((pressPos.y + camOffset.y), gp.tileSize)
        );
      }
    }

    if (Boolean.TRUE.equals(gp.mouseH.clicked.get(MouseHandler.LMB))) {
      if (!gp.board.exists(gp.clickedBoardPoints.get(MouseHandler.LMB))) {
        gp.board.generate(gp.clickedBoardPoints.get(MouseHandler.LMB));
      }
      gp.board.get(gp.clickedBoardPoints.get(MouseHandler.LMB)).reveal();
    }
    if (Boolean.TRUE.equals(gp.mouseH.clicked.get(MouseHandler.RMB)) && gp.board.exists(gp.clickedBoardPoints.get(MouseHandler.RMB))) {
      Square clickedSquare = gp.board.get(gp.clickedBoardPoints.get(MouseHandler.RMB));
      if (!clickedSquare.isRevealed()) {
        clickedSquare.flag();
      }
    } else if (Boolean.TRUE.equals(gp.mouseH.clicked.get(MouseHandler.WHEEL))) {
      Point pos = gp.clickedBoardPoints.get(MouseHandler.WHEEL); //pos: Location clicked by the mouse-wheel
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
    for (int button : gp.mouseH.mouseButtons) {
      if (Boolean.TRUE.equals(gp.mouseH.clicked.get(button))) {
        gp.board.save();
      }
    }

    if (gp.keyH.getEscTapped()) {
      gp.stateH.setActive(GamePanel.STATE_PAUSED);
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
    if (Boolean.TRUE.equals(gp.mouseH.pressed.get(MouseHandler.WHEEL))
        && System.nanoTime() > gp.mouseH.pressTime.get(MouseHandler.WHEEL) + (long) (0.15 * 1e9)
    ) {
      Point cameraOffset = new Point(gp.getStartDragCamera());
      Point startDragPos = gp.mouseH.pressPos.get(MouseHandler.WHEEL);
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
