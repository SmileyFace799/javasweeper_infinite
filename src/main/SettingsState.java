package main;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class SettingsState implements State {
  final GamePanel gp;
  final MenuWindow menuWindow;
  final Slider mineChanceSlider;
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
        menuWindow.lowerRect.y + gp.ui.margin,
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

    String[] drawStrArr = {"Mine Chance"};
    for (int i = 0; i < drawStrArr.length; i++) {
      g2.setColor(gp.ui.numColors.get(i + 1));
      g2.drawString(drawStrArr[i], menuWindow.lowerRect.x + gp.ui.margin,
          menuWindow.lowerRect.y + (i + 1) * (gp.ui.margin + gp.ui.fontSize)
      );
    }
    g2.setColor(gp.ui.numColors.get(1));
    mineChanceSlider.draw(g2);
    gp.ui.drawStringRightAligned(g2, Math.round(mineChanceSlider.getValue() * 100) + "%",
        menuWindow.lowerRect.x + menuWindow.lowerRect.width - gp.ui.margin,
        menuWindow.lowerRect.y + gp.ui.margin + gp.ui.fontSize
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

  }
}
