package smiley.javasweeper.model.squares;

import smiley.javasweeper.textures.TxMap;
import smiley.javasweeper.view.screens.GameplayScreen;

public class BombSquare extends Square {

  public BombSquare(int x, int y) {
    super(x, y);
  }

  @Override
  public void setRevealedTrue() {
    if (!this.isRevealed() && !this.isFlagged()) {
      this.setTx(TxMap.getScaled(GameplayScreen.getTileSize(), "squares/bombDetonated.bmp"));
      super.setRevealedTrue();
    }
  }
}
