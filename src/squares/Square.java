package squares;

import java.awt.image.BufferedImage;
import main.Board;
import main.TxMap;

public class Square {
  final int xval;
  final int yval;
  final int size;

  final Board board;
  final TxMap txMap;

  private BufferedImage tx;
  private boolean revealed = false;
  private boolean flagged = false;

  public Square(int x, int y, Board board, TxMap txMap) {
    this.xval = x;
    this.yval = y;
    this.size = board.getTileSize();
    this.board = board;
    this.txMap = txMap;
    this.tx = txMap.getScaled(size, "hidden");
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

  public void reveal() {
    if (!revealed && !flagged) {
      revealed = true;
      board.updateImage(this);
    }
  }

  public void flag() {
    flagged = !flagged;
    if (flagged) {
      setTx(txMap.getScaled(size, "flag"));
    } else {
      setTx(txMap.getScaled(size, "hidden"));
    }
    board.updateImage(this);
  }
}
