package smiley.javasweeper.view.screens;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import smiley.javasweeper.view.components.MenuWindow;
import smiley.javasweeper.controllers.MouseHandler;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.controllers.UIHandler;

public class PauseState implements State {
  private final GamePanel gp;

  private final MenuWindow menuWindow;
  private boolean hoveringLowerMenu;
  private int hoveredIndex;

  public PauseState(GamePanel gp) {
    this.gp = gp;

    double uiScale = Settings.getUiScale();
    this.menuWindow = gp.uiH.makeSubWindow(
        (int) Math.round(150 * uiScale),
        gp.uiH.titleFontSize + 2 * gp.uiH.margin,
        (int) Math.round(100 * uiScale)
    );
    gp.uiH.centerSubWindow(menuWindow);
  }

  @Override
  public void onLoad() {
    //Not added yet
  }

  @Override
  public void update() {
    if (gp.keyH.getEscTapped()) {
      gp.stateH.setActive(GameState.STATE_GAME);
    }
    if (Boolean.TRUE.equals(gp.mouseH.clicked.get(MouseHandler.LMB)) && hoveringLowerMenu) {
      switch (hoveredIndex) {
        case 0 -> gp.stateH.setActive(GameState.STATE_GAME);
        case 3 -> gp.stateH.setActive(GameState.STATE_SETTINGS);
        default -> {
          //Do nothing
        }
      }
    }
  }

  @Override
  public void onUnload() {
    //Not used
  }

  @Override
  public void drawScreen(@NotNull Graphics2D g2) {
    g2.setColor(Color.black);

    gp.stateH.getState(GameState.STATE_GAME).drawScreen(g2);
    menuWindow.draw(g2);

    g2.setFont(gp.uiH.titleFont);
    gp.uiH.drawStringCentered(g2, "Game Paused", menuWindow.upperRect.y + gp.uiH.margin + gp.uiH.titleFontSize);
    g2.setFont(gp.uiH.font);

    String[] drawStrArr = {"Resume", "Load Game", "Save Game", "Settings", "Quit Game"};
    for (int i = 0; i < drawStrArr.length; i++) {
      int textY = menuWindow.lowerRect.y + (i + 1) * (gp.uiH.margin + gp.uiH.fontSize);
      if (i == hoveredIndex && menuWindow.lowerRect.contains(gp.mouseMotionH.mousePos)) {
        g2.setColor(UIHandler.OVERLAY_COLOR);
        g2.fillRect(
            menuWindow.lowerRect.x, textY - gp.uiH.fontSize - gp.uiH.margin / 2,
            menuWindow.lowerRect.width, gp.uiH.fontSize + gp.uiH.margin
        );
      }
      g2.setColor(gp.uiH.numColors.get(i + 1));
      gp.uiH.drawStringCentered(g2, drawStrArr[i], textY);
    }
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
    //Not used
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    hoveringLowerMenu = menuWindow.lowerRect.contains(e.getPoint());
    if (hoveringLowerMenu) {
      hoveredIndex = Math.floorDiv((e.getY() - menuWindow.lowerRect.y - gp.uiH.margin / 2),
          (gp.uiH.margin + gp.uiH.fontSize));
    }
  }
}
