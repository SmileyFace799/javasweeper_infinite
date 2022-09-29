package main;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class SettingsState implements State {
  final GamePanel gp;
  final MenuWindow menuWindow;
  final Slider mineChanceSlider;

  private int hoveredIndex;
  private boolean mcSliderDragged = false;

  public SettingsState(GamePanel gp) {
    this.gp = gp;
    this.menuWindow = gp.ui.makeSubWindow(
        (int) Math.round(250 * gp.uiScale),
        gp.ui.titleFontSize + 2 * gp.ui.margin,
        (int) Math.round(100 * gp.uiScale)
    );
    gp.ui.centerSubWindow(menuWindow);
    int sliderWidth = (int) Math.round(75 * gp.uiScale);
    this.mineChanceSlider = gp.ui.makeSlider(
        menuWindow.lowerRect.x + menuWindow.lowerRect.width
            - (int) Math.round(45 * gp.uiScale) - sliderWidth,
        menuWindow.lowerRect.y + gp.ui.margin * 2 + gp.ui.fontSize,
        sliderWidth, gp.ui.fontSize,
        0.0
    );
  }

  @Override
  public void onLoad() {

  }

  @Override
  public void update() {
    if (gp.keyH.getEscTapped()) {
      gp.stateH.setActive(gp.STATE_GAME);
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
    gp.ui.drawStringCentered(g2, "Settings", menuWindow.upperRect.y + gp.ui.margin + gp.ui.titleFontSize);
    g2.setFont(gp.ui.font);

    String[] drawStrArr = {"Fullscreen", "Mine Chance", "", "Go back", "Save", "Save & go back"};
    for (int i = 0; i < drawStrArr.length; i++) {
      int textY = menuWindow.lowerRect.y + (i + 1) * (gp.ui.margin + gp.ui.fontSize);
      if (!drawStrArr[i].equals("") && i == hoveredIndex && menuWindow.lowerRect.contains(gp.mouseMotionH.mousePos)) {
        g2.setColor(gp.ui.overlayColor);
        g2.fillRect(
            menuWindow.lowerRect.x, textY - gp.ui.fontSize - gp.ui.margin / 2,
            menuWindow.lowerRect.width, gp.ui.fontSize + gp.ui.margin
        );
      }
      g2.setColor(gp.ui.numColors.get(i + 1));
      g2.drawString(drawStrArr[i], menuWindow.lowerRect.x + gp.ui.margin, textY);
    }
    g2.setColor(gp.ui.numColors.get(2));
    mineChanceSlider.draw(g2);
    gp.ui.drawStringRightAligned(g2, Math.round(mineChanceSlider.getValue() * 100) + "%",
        menuWindow.lowerRect.x + menuWindow.lowerRect.width - gp.ui.margin,
        menuWindow.lowerRect.y + (gp.ui.margin + gp.ui.fontSize) * 2
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
    if (e.getButton() == MouseEvent.BUTTON1 && mineChanceSlider.boundingRect.contains(e.getPoint())) {
      mcSliderDragged = true;
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1 && mcSliderDragged) {
      mcSliderDragged = false;
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {

  }

  @Override
  public void mouseExited(MouseEvent e) {

  }

  @Override
  public void mouseDragged(MouseEvent e) {
    int posX = e.getX();
    if (
        mcSliderDragged && posX > mineChanceSlider.boundingRect.x
        && posX < mineChanceSlider.boundingRect.x + mineChanceSlider.boundingRect.width
    ) {
      mineChanceSlider.setValue(
          (posX - mineChanceSlider.boundingRect.x) / (double) (mineChanceSlider.boundingRect.width)
      );
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    boolean hoveringLowerMenu = menuWindow.lowerRect.contains(e.getPoint());
    if (hoveringLowerMenu) {
      hoveredIndex = Math.floorDiv((e.getY() - menuWindow.lowerRect.y - gp.ui.margin / 2),
          (gp.ui.margin + gp.ui.fontSize));
    }
  }
}
