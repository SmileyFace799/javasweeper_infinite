package smiley.squares;

import smiley.mainapp.Board;
import smiley.mainapp.TxMap;

public class BombSquare extends Square {

  public BombSquare(int x, int y) {
    super(x, y);
  }

  @Override
  public void setRevealedTrue() {
    if (!this.isRevealed() && !this.isFlagged()) {
      this.setTx(TxMap.getScaled(Board.getTileSize(), "bombDetonated"));
      super.setRevealedTrue();
    }
  }
}
