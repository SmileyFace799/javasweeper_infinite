package smiley.javasweeper.model.squares;

import smiley.javasweeper.textures.TxMap;
import smiley.javasweeper.view.screens.GameplayScreen;

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
    setTx(TxMap.getScaled(GameplayScreen.getTileSize(), String.format("squares/%s.bmp", num)));
    super.setRevealedTrue();
  }
}
