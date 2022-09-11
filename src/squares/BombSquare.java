package squares;

import main.Board;
import main.TxMap;

public class BombSquare extends Square{

    public BombSquare(int x, int y, Board board, TxMap txMap) {
        super(x, y, board, txMap);
    }

    @Override
    public void reveal() {
        if (!this.isRevealed() && !this.isFlagged()) {
            this.setTx(txMap.getScaled(size, "bombDetonated"));
            super.reveal();
        }
    }
}
