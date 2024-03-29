package smiley.notimplemented;


import smiley.javasweeper.squares.NumberSquare;
import smiley.javasweeper.squares.Square;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.StateHandler;
import smiley.javasweeper.model.Board;
import smiley.javasweeper.view.screens.GameState;
import smiley.javasweeper.view.screens.State;

/**
 * Represents a state where the game is being played
 *
 * @see State
 */
public class PlayState implements State {
  private final GamePanel gp;
  private final MouseButtonsOld mouseH;
  private final KeyHandler keyH;
  private final StateHandler stateH;
  private final UIHandler uiH;

  private final Map<Integer, Point> clickedBoardPoints = new HashMap<>();

  public PlayState(GamePanel gp) {
    this.gp = gp;
    this.mouseH = gp.mouseH;
    this.keyH = gp.keyH;
    this.stateH = gp.stateH;
    this.uiH = gp.uiH;
    for (int button : mouseH.mouseButtons) {
      clickedBoardPoints.put(button, new Point());
    }
  }

  @Override
  public void onLoad() {
    //Not used
  }

  @Override
  public void update() {
    Board board = gp.getBoard();
    for (int button : mouseH.mouseButtons) {
      if (Boolean.TRUE.equals(mouseH.clicked.get(button))) {
        Point pressPos = mouseH.pressPos.get(button);
        Point camOffset = gp.getCameraOffset();
        clickedBoardPoints.get(button).setLocation(
            Math.floorDiv((pressPos.x + camOffset.x), Board.getTileSize()),
            Math.floorDiv((pressPos.y + camOffset.y), Board.getTileSize())
        );
      }
    }

    if (Boolean.TRUE.equals(mouseH.clicked.get(MouseButtonsOld.LMB))) {
      if (!board.exists(clickedBoardPoints.get(MouseButtonsOld.LMB))) {
        board.generate(clickedBoardPoints.get(MouseButtonsOld.LMB));
      }
      board.reveal(clickedBoardPoints.get(MouseButtonsOld.LMB));
    }
    if (Boolean.TRUE.equals(mouseH.clicked.get(MouseButtonsOld.RMB)) && board.exists(clickedBoardPoints.get(MouseButtonsOld.RMB))) {
      Square clickedSquare = board.get(clickedBoardPoints.get(MouseButtonsOld.RMB));
      if (!clickedSquare.isRevealed()) {
        board.flag(clickedSquare);
      }
    } else if (Boolean.TRUE.equals(mouseH.clicked.get(MouseButtonsOld.WHEEL))) {
      Point pos = clickedBoardPoints.get(MouseButtonsOld.WHEEL); //pos: Location clicked by the mouse-wheel
      Square clickedSquare = board.get(pos);
      if (
          board.exists(pos)
              && clickedSquare instanceof NumberSquare clickedNum
              && clickedNum.isRevealed()
      ) {
        int flagCounter = 0; //Number of flags surrounding the number
        for (int x = pos.x - 1; x <= pos.x + 1; x++) {
          for (int y = pos.y - 1; y <= pos.y + 1; y++) {
            if (board.get(x, y).isFlagged()) {
              flagCounter++;
            }
          }
        }
        if (clickedNum.getNumber() == flagCounter) {
          for (int x = pos.x - 1; x <= pos.x + 1; x++) {
            for (int y = pos.y - 1; y <= pos.y + 1; y++) {
              Square squareToReveal = board.get(x, y);
              if (!squareToReveal.isFlagged()) {
                board.reveal(x, y);
              }
            }
          }
        }
      }
    }
    for (int button : gp.mouseH.mouseButtons) {
      if (Boolean.TRUE.equals(gp.mouseH.clicked.get(button))) {
        board.save();
      }
    }

    if (keyH.getEscTapped()) {
      stateH.setActive(GameState.STATE_PAUSED);
    }
  }

  @Override
  public void onUnload() {
    //Not used
  }

  @Override
  public void drawScreen(Graphics2D g2) {
    Board board = gp.getBoard();
    Point camOffset = gp.getCameraOffset();
    g2.drawImage(board.getImage(),
        board.getMinX() * Board.getTileSize() - camOffset.x,
        board.getMinY() * Board.getTileSize() - camOffset.y,
        null
    );
  }

  @Override
  public void keyTyped(KeyEvent e) {
    //Not used
  }

  @Override
  public void keyPressed(KeyEvent e) {
    //Not used
  }

  @Override
  public void keyReleased(KeyEvent e) {
    //Not used
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    //Not used
  }

  @Override
  public void mousePressed(MouseEvent e) {
    //Not used
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    //Not used
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    //Not used
  }

  @Override
  public void mouseExited(MouseEvent e) {
    //Not used
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (Boolean.TRUE.equals(mouseH.pressed.get(MouseButtonsOld.WHEEL))
        && System.nanoTime() > mouseH.pressTime.get(MouseButtonsOld.WHEEL) + (long) (0.15 * 1e9)
    ) {
      Point cameraOffset = new Point(gp.getStartDragCamera());
      Point startDragPos = mouseH.pressPos.get(MouseButtonsOld.WHEEL);
      Point pos = uiH.scalePointToDisplay(e.getPoint());
      cameraOffset.x += startDragPos.x - pos.x;
      cameraOffset.y += startDragPos.y - pos.y;
      gp.setCameraOffset(cameraOffset);
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    //Not used
  }
}
