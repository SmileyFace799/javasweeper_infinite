package smiley.javasweeper.model.squares;

public abstract class Square {
  protected final int xval;
  protected final int yval;

  protected boolean revealed = false;
  protected boolean flagged = false;

  protected Square(int x, int y) {
    this.xval = x;
    this.yval = y;
  }

  public boolean isRevealed() {
    return revealed;
  }

  public boolean isFlagged() {
    return flagged;
  }

  public abstract boolean isMassRevealCountable();

  public int getX() {
    return xval;
  }

  public int getY() {
    return yval;
  }

  public void setRevealedTrue() {
    this.revealed = true;
  }

  public void toggleFlagged() {
    if (!revealed) {
      flagged = !flagged;
    }
  }
}
