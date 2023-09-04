package smiley.javasweeper.model.squares;

public class NumberSquare extends Square {
    private int number;

    public NumberSquare(int x, int y) {
        super(x, y);
        this.number = -1;
    }

    @Override
    public boolean isMassRevealCountable() {
        return isFlagged();
    }

    public int getNumber() {
        if (number < 0) {
            throw new IllegalStateException("This square does not have an assigned number yet");
        }
        return number;
    }

    public void setRevealedTrue(int num) {
        super.setRevealedTrue();
        number = num;
    }
}
