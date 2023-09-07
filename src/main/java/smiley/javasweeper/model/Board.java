package smiley.javasweeper.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.model.squares.BombSquare;
import smiley.javasweeper.model.squares.NumberSquare;
import smiley.javasweeper.model.squares.Square;

public class Board implements Iterable<Square> {

    public static final int ORIGINAL_TILE_SIZE = 16;

    private final Map<Integer, Map<Integer, Square>> boardMap;
    private final Dimensions dimensions;
    private final double mineChance;
    private final String filename;

    private boolean exploded;

    private Board() {
        this("");
    }

    public Board(String filename) {
        this.boardMap = new HashMap<>();
        this.dimensions = new Dimensions();
        this.filename = filename;
        this.mineChance = 0.3;
        this.exploded = false;
    }

    //Accessors
    public Map<Integer, Map<Integer, Square>> getBoardMap() {
        return boardMap;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public String getFilename() {
        return filename;
    }

    public boolean exists(int x, int y) {
        return boardMap.containsKey(x) && boardMap.get(x).containsKey(y);
    }

    public Square get(int x, int y) {
        Square square;
        if (exists(x, y)) {
            square = boardMap.get(x).get(y);
        } else {
            square = null;
        }
        return square;
    }

    public boolean isBomb(int x, int y) {
        return get(x, y) instanceof BombSquare;
    }

    public List<Square> getSquareList() {
        List<Square> squareList = new ArrayList<>();
        for (Map<Integer, Square> column : boardMap.values()) {
            squareList.addAll(column.values());
        }
        return squareList;
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

    public void put(int x, int y, Square square) {
        if (!boardMap.containsKey(x)) {
            boardMap.put(x, new HashMap<>());
        }

        if (!boardMap.get(x).containsKey(y)) {
            boardMap.get(x).put(y, square);
        } else {
            System.out.println("put: A square already exists at x=" + x + " & y=" + y);
        }
        dimensions.expand(square.getX(), square.getY());
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
                    if (!exists(xy[0], xy[1])) {
                        updatedSquares.add(generate(xy[0], xy[1], generateMineChance));
                    }
                    if (isBomb(xy[0], xy[1])) {
                        squareNumber++;
                    }
                }

                numSquare.setRevealedTrue(squareNumber);
                if (squareNumber == 0 && doMassReveal) {
                    updatedSquares.addAll(massReveal(x, y));
                }
            } else if (square instanceof BombSquare) {
                square.setRevealedTrue();
                exploded = true;
            }
        }
        dimensions.expand(square.getX(), square.getY());
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
    public List<Square> massReveal(int x, int y) {
        Board revealBoard = new Board();
        revealBoard.put(x, y, get(x, y));
        return massReveal(revealBoard, 50).stream().distinct().toList();
    }

    private List<Square> massReveal(Board revealBoard, int recursionCount) {
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
                if (recursionCount > 0 && revealSquare.getNumber()
                        == surroundingSquares
                        .stream()
                        .filter(Square::isMassRevealCountable)
                        .count()
                ) {
                    for (Square surroundingSquare : surroundingSquares) {
                        if (!nextRevealBoard.exists(surroundingSquare.getX(), surroundingSquare.getY())
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
        if (recursionCount > 0 && !nextRevealBoard.getBoardMap().isEmpty()) {
            updatedSquares.addAll(massReveal(nextRevealBoard, recursionCount - 1));
        }
        return updatedSquares.stream().distinct().toList();
    }

    @NotNull
    @Override
    public Iterator<Square> iterator() {
        return new BoardIterator();
    }

    public static class Dimensions {
        private int minX;
        private int minY;
        private int maxX;
        private int maxY;

        private Dimensions() {
            this(0, 0, 0, 0);
        }

        private Dimensions(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        public int getMinX() {
            return minX;
        }

        public int getMinY() {
            return minY;
        }

        public int getMaxX() {
            return maxX;
        }

        public int getMaxY() {
            return maxY;
        }

        public void expand(int newX, int newY) {
            this.minX = Math.min(minX, newX);
            this.minY = Math.min(minY, newY);
            this.maxX = Math.max(maxX, newX);
            this.maxY = Math.max(maxY, newY);
        }

        public Dimensions copy() {
            return new Dimensions(minX, minY, maxX, maxY);
        }

        @Override
        public String toString() {
            return "Dimensions{" +
                    "minX=" + minX +
                    ", minY=" + minY +
                    ", maxX=" + maxX +
                    ", maxY=" + maxY +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            ;
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ;
            Dimensions dimensions = (Dimensions) o;
            return minX == dimensions.minX
                    && minY == dimensions.minY
                    && maxX == dimensions.maxX
                    && maxY == dimensions.maxY;
        }

        @Override
        public int hashCode() {
            return Objects.hash(minX, minY, maxX, maxY);
        }
    }

    private class BoardIterator implements Iterator<Square> {
        private final List<Square> squares;

        public BoardIterator() {
            this.squares = boardMap
                    .values()
                    .stream()
                    .flatMap(column -> column.values().stream())
                    .collect(Collectors.toList());
        }

        @Override
        public boolean hasNext() {
            return !squares.isEmpty();
        }

        @Override
        public Square next() {
            return squares.remove(0);
        }
    }
}
