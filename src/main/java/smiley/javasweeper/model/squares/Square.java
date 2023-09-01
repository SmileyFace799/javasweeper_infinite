package smiley.javasweeper.model.squares;

import java.awt.image.BufferedImage;
import smiley.javasweeper.model.Board;
import smiley.javasweeper.textures.TxMap;
import smiley.javasweeper.view.screens.GameplayScreen;

public abstract class Square {
  protected final int xval;
  protected final int yval;

  protected BufferedImage tx;
  protected boolean revealed = false;
  protected boolean flagged = false;

  public Square(int x, int y) {
    this.xval = x;
    this.yval = y;
    this.tx = TxMap.getScaled(GameplayScreen.getTileSize(), "squares/hidden.bmp");
  }

  //Accessors
  public BufferedImage getTx() {
    return tx;
  }

  public boolean isRevealed() {
    return revealed;
  }

  public boolean isFlagged() {
    return flagged;
  }

  public int getX() {
    return xval;
  }

  public int getY() {
    return yval;
  }

  //Mutators
  public void setTx(BufferedImage tx) {
    this.tx = tx;
  }

  public void setRevealedTrue() {
    this.revealed = true;
  }

  public void toggleFlagged() {
    if (!revealed) {
      flagged = !flagged;
      if (flagged) {
        setTx(TxMap.getScaled(GameplayScreen.getTileSize(), "squares/flag.bmp"));
      } else {
        setTx(TxMap.getScaled(GameplayScreen.getTileSize(), "squares/hidden.bmp"));
      }
    }
  }
}
