package smiley.javasweeper.model.squares;

public class BombSquare extends Square {

    public BombSquare(int x, int y) {
        super(x, y);
    }

    @Override
    public void setRevealedTrue() {
        if (!this.isRevealed() && !this.isFlagged()) {
            super.setRevealedTrue();
        }
    }

    @Override
    public boolean isMassRevealCountable() {
        return isRevealed() || isFlagged();
    }
}
