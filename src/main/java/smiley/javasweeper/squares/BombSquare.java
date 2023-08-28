package smiley.javasweeper.squares;

import smiley.javasweeper.model.Board;
import smiley.javasweeper.textures.TxMap;

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
