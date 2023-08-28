package smiley.javasweeper.squares;

import smiley.javasweeper.model.Board;
import smiley.javasweeper.textures.TxMap;

public class NumberSquare extends Square {
  private int number;

  public NumberSquare(int x, int y) {
    super(x, y);
  }

  //Accessors
  public int getNumber() {
    return number;
  }

  //Mutators
  public void setRevealedTrue(int num) {
    number = num;
    setTx(TxMap.getScaled(Board.getTileSize(), Integer.toString(num)));
    super.setRevealedTrue();
  }
}
