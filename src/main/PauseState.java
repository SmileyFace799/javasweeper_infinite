package main;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class PauseState implements State {
  private final GamePanel gp;
  private final Settings settings;

  private final MenuWindow menuWindow;
  private boolean hoveringLowerMenu;
  private int hoveredIndex;

  public PauseState(GamePanel gp) {
    this.gp = gp;
    this.settings = gp.settings;

    double uiScale = settings.getUiScale();
    this.menuWindow = gp.uiH.makeSubWindow(
        (int) Math.round(150 * uiScale),
        gp.uiH.titleFontSize + 2 * gp.uiH.margin,
        (int) Math.round(100 * uiScale)
    );
    gp.uiH.centerSubWindow(menuWindow);
  }

  @Override
  public void onLoad() {

  }

  @Override
  public void update() {
    if (gp.keyH.getEscTapped()) {
      gp.stateH.setActive(gp.STATE_GAME);
    }
    if (gp.mouseH.clicked.get(MouseHandler.LMB) && hoveringLowerMenu) {
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

    g2.setFont(gp.uiH.titleFont);
    gp.uiH.drawStringCentered(g2, "Game Paused", menuWindow.upperRect.y + gp.uiH.margin + gp.uiH.titleFontSize);
    g2.setFont(gp.uiH.font);

    String[] drawStrArr = {"Resume", "Load Game", "Save Game", "Settings", "Quit Game"};
    for (int i = 0; i < drawStrArr.length; i++) {
      int textY = menuWindow.lowerRect.y + (i + 1) * (gp.uiH.margin + gp.uiH.fontSize);
      if (i == hoveredIndex && menuWindow.lowerRect.contains(gp.mouseMotionH.mousePos)) {
        g2.setColor(gp.uiH.overlayColor);
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
      hoveredIndex = Math.floorDiv((e.getY() - menuWindow.lowerRect.y - gp.uiH.margin / 2),
          (gp.uiH.margin + gp.uiH.fontSize));
    }
  }
}
