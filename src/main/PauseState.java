package main;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class PauseState implements State {
  final GamePanel gp;
  final MenuWindow menuWindow;

  private boolean hoveringLowerMenu;
  private int hoveredIndex;

  public PauseState(GamePanel gp) {
    this.gp = gp;
    this.menuWindow = gp.ui.makeSubWindow(
        (int) Math.round(150 * gp.uiScale),
        gp.ui.titleFontSize + 2 * gp.ui.margin,
        (int) Math.round(100 * gp.uiScale)
    );
    gp.ui.centerSubWindow(menuWindow);
  }

  @Override
  public void onLoad() {

  }

  @Override
  public void update() {
    if (gp.keyH.getEscTapped()) {
      gp.stateH.setActive(gp.STATE_GAME);
    }
    if (gp.mouseH.clicked.get("lmb") && hoveringLowerMenu) {
      switch (hoveredIndex) {
        case 0 -> gp.stateH.setActive(gp.STATE_GAME);
        case 3 -> gp.stateH.setActive(gp.STATE_SETTINGS);
        default -> { }
      }
    }
  }

  @Override
  public void onUnload() {

  }

  @Override
  public void drawScreen(@NotNull Graphics2D g2) {
    g2.setColor(Color.black);

    gp.stateH.getState(gp.STATE_GAME).drawScreen(g2);
    menuWindow.draw(g2);

    g2.setFont(gp.ui.titleFont);
    gp.ui.drawStringCentered(g2, "Game Paused", menuWindow.upperRect.y + gp.ui.margin + gp.ui.titleFontSize);
    g2.setFont(gp.ui.font);

    String[] drawStrArr = {"Resume", "Load Game", "Save Game", "Settings", "Quit Game"};
    for (int i = 0; i < drawStrArr.length; i++) {
      if (i == hoveredIndex) {
        g2.setColor(Color.black);
      } else {
        g2.setColor(gp.ui.numColors.get(i + 1));
      }
      gp.ui.drawStringCentered(
          g2, drawStrArr[i],
          menuWindow.lowerRect.y + (i + 1) * (gp.ui.margin + gp.ui.fontSize)
      );
    }
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

  }

  @Override
  public void mouseMoved(MouseEvent e) {
    hoveringLowerMenu = menuWindow.lowerRect.contains(e.getPoint());
    if (hoveringLowerMenu) {
      hoveredIndex = Math.floorDiv((e.getY() - menuWindow.lowerRect.y - gp.ui.margin / 2),
          (gp.ui.margin + gp.ui.fontSize));
    }
  }
}
