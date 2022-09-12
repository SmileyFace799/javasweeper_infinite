package squares;

import main.Board;
import main.TxMap;

public class NumberSquare extends Square {
  private int number;

  public NumberSquare(int x, int y, Board board, TxMap txMap) {
    super(x, y, board, txMap);
  }

  //Accessors
  public int getNumber() {
    return number;
  }

  //Mutators
  public void setRevealed(int num) {
    number = num;
    this.setTx(txMap.getScaled(size, Integer.toString(num)));
    super.reveal();
  }

  //Other
  @Override
  public void reveal() {
    reveal(board.getMineChance());
  }

  public void reveal(double generateMineChance) {
    reveal(generateMineChance, true);
  }

  public void reveal(double generateMineChance, boolean doMassReveal) {
    if (!this.isFlagged() && !this.isRevealed()) {
      number = 0;
      for (int x = this.xval - 1; x <= this.xval + 1; x++) {
        for (int y = this.yval - 1; y <= this.yval + 1; y++) {
          if (!board.exists(x, y)) {
            board.generate(x, y, generateMineChance);
          }
          if (board.isBomb(x, y)) {
            number++;
          }
        }
      }
      this.setTx(txMap.getScaled(size, Integer.toString(number)));
      super.reveal();

      if (number == 0 && doMassReveal) {
        board.massReveal(xval, yval);
      }
    }
  }
}
