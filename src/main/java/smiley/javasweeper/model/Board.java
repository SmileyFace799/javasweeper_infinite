package smiley.javasweeper.model;

import java.util.ArrayList;
import java.util.List;
import smiley.javasweeper.misc.Map2D;
import smiley.javasweeper.model.squares.BombSquare;
import smiley.javasweeper.model.squares.NumberSquare;
import smiley.javasweeper.model.squares.Square;

public class Board extends Map2D<Integer, Integer, Square> {

    private final double mineChance;
    private final String filename;

    private boolean exploded;

    private Board() {
        this(0, "");
    }

    public Board(double mineChance, String filename) {
        this.mineChance = mineChance;
        this.filename = filename;
        this.exploded = false;
    }

    public double getMineChance() {
        return mineChance;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isBomb(int x, int y) {
        return get(x, y) instanceof BombSquare;
    }

    public List<int[]> getAdjacentPoints(int x, int y) {
        List<int[]> adjacentPoints = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (i != 4) {
                adjacentPoints.add(new int[]{x - 1 + i % 3, y - 1 + i / 3});
            }
        }
        return adjacentPoints;
    }

    public Square generate(int x, int y) {
        return generate(x, y, mineChance);
    }

    public Square generate(int x, int y, double mineChance) {
        Square square;
        if (Math.random() < mineChance) {
            square = new BombSquare(x, y);
        } else {
            square = new NumberSquare(x, y);
        }
        put(x, y, square);
        return square;
    }

    /**
     * Reveals a square & generates surrounding squares.
     *
     * @param x The x-coordinate of the square to reveal
     * @param y The y-coordinate fo the square to reveal
     * @return A list of every square that was updated in the revealing process
     */
    public List<Square> reveal(int x, int y) {
        return reveal(x, y, mineChance, true);
    }

    /**
     * Reveals a square & generates surrounding squares.
     *
     * @param x                  The x-coordinate of the square to reveal
     * @param y                  The y-coordinate fo the square to reveal
     * @param generateMineChance The chance of generating a surrounding mine
     * @return A list of every square that was updated in the revealing process
     */
    public List<Square> reveal(int x, int y, double generateMineChance) {
        return reveal(x, y, generateMineChance, true);
    }

    /**
     * Reveals a square & generates surrounding squares.
     *
     * @param x            The x-coordinate of the square to reveal
     * @param y            The y-coordinate fo the square to reveal
     * @param doMassReveal If the square should auto-reveal surrounding squares if it's a 0
     * @return A list of every square that was updated in the revealing process
     */
    public List<Square> reveal(int x, int y, boolean doMassReveal) {
        return reveal(x, y, mineChance, doMassReveal);
    }

    /**
     * Reveals a square & generates surrounding squares.
     *
     * @param x                  The x-coordinate of the square to reveal
     * @param y                  The y-coordinate fo the square to reveal
     * @param generateMineChance The chance of generating a surrounding mine
     * @param doMassReveal       If the square should auto-reveal surrounding squares if it's a 0
     * @return A list of every square that was updated in the revealing process
     */
    private List<Square> reveal(int x, int y, double generateMineChance, boolean doMassReveal) {
        List<Square> updatedSquares = new ArrayList<>();
        Square square = get(x, y);
        if (!square.isFlagged() && !square.isRevealed()) {
            updatedSquares.add(square);
            if (square instanceof NumberSquare numSquare) {
                int squareNumber = 0;
                for (int[] xy : getAdjacentPoints(x, y)) {
                    if (!containsKey(xy[0], xy[1])) {
                        updatedSquares.add(generate(xy[0], xy[1], generateMineChance));
                    }
                    if (isBomb(xy[0], xy[1])) {
                        squareNumber++;
                    }
                }

                numSquare.setRevealedTrue(squareNumber);
                if (squareNumber == 0 && doMassReveal) {
                    updatedSquares.addAll(massReveal(x, y, true));
                }
            } else if (square instanceof BombSquare) {
                square.setRevealedTrue();
                exploded = true;
            }
        }
        return updatedSquares.stream().distinct().toList();
    }

    /**
     * Reveals every revealable square around a specified point,
     * and repeats the process up to 50 times for every revealed 0.
     *
     * @param x The x-coordinate to reveal squares around
     * @param y The y-coordinate to reveal squares around
     * @return A list of every square that was updated in the revealing process
     */
    public List<Square> massReveal(int x, int y, boolean onlyZeros) {
        Board revealBoard = new Board();
        revealBoard.put(x, y, get(x, y));
        return massReveal(revealBoard, 100, onlyZeros).stream().distinct().toList();
    }

    private List<Square> massReveal(Board revealBoard, int recursionCount, boolean onlyZeros) {
        List<Square> updatedSquares = new ArrayList<>();
        Board nextRevealBoard = new Board();
        for (Square square : revealBoard) {
            if (!square.isFlagged() && !square.isRevealed()) {
                updatedSquares.addAll(reveal(
                        square.getX(), square.getY(), false
                ));
            }
            if (square instanceof NumberSquare revealSquare) {
                List<Square> surroundingSquares =
                        getAdjacentPoints(revealSquare.getX(), revealSquare.getY())
                                .stream()
                                .map(xy -> get(xy[0], xy[1]))
                                .toList();
                if (recursionCount > 0 && (revealSquare.getNumber() == 0 || (
                        !onlyZeros && revealSquare.getNumber() == surroundingSquares
                                .stream()
                                .filter(Square::isMassRevealCountable)
                                .count()
                ))) {
                    for (Square surroundingSquare : surroundingSquares) {
                        if (!nextRevealBoard.containsKey(surroundingSquare.getX(), surroundingSquare.getY())
                                && !surroundingSquare.isRevealed()
                                && !surroundingSquare.isFlagged()
                        ) {
                            nextRevealBoard.put(
                                    surroundingSquare.getX(), surroundingSquare.getY(),
                                    surroundingSquare
                            );
                        }
                    }
                }
            } else {
                recursionCount = 0;
            }
        }
        if (recursionCount > 0 && !nextRevealBoard.isEmpty()) {
            updatedSquares.addAll(massReveal(nextRevealBoard, recursionCount - 1, true));
        }
        return updatedSquares.stream().distinct().toList();
    }
}
