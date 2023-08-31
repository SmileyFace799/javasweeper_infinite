package smiley.javasweeper.model;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import smiley.javasweeper.controllers.UIHandler;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.squares.BombSquare;
import smiley.javasweeper.squares.NumberSquare;
import smiley.javasweeper.squares.Square;

public class Board implements Iterable<Square> {

  public static final int ORIGINAL_TILE_SIZE = 16;

  private final Map<Integer, Map<Integer, Square>> boardMap;
  private final Dimensions dimensions;
  private final double mineChance;
  private final String filename;

  public Board() {
    this("");
  }

  public Board(String filename) {
    this.boardMap = new HashMap<>();
    this.dimensions = new Dimensions();
    this.filename = filename;
    this.mineChance = Settings.getMineChance();
  }

  //Accessors
  public Map<Integer, Map<Integer, Square>> getBoardMap() {
    return boardMap;
  }

  public static int getTileSize() {
    return (int) Math.round(ORIGINAL_TILE_SIZE * Settings.getBoardScale());
  }

  public Dimensions getDimensions() {
    return dimensions;
  }

  public String getFilename() {
    return filename;
  }

  public boolean exists(@NotNull Point pos) {
    return exists(pos.x, pos.y);
  }

  public boolean exists(int x, int y) {
    return boardMap.containsKey(x) && boardMap.get(x).containsKey(y);
  }

  public Square get(Point pos) {
    return get(pos.x, pos.y);
  }

  public Square get(int x, int y) {
    if (exists(x, y)) {
      return boardMap.get(x).get(y);
    } else {
      System.out.println("get: There is no square at x=" + x + " & y=" + y);
      return null;
    }
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
    for (int adjX = x - 1; adjX <= x + 1; adjX++) {
      for (int adjY = y - 1; adjY <= y + 1; adjY++) {
        if (x != adjX || y != adjY) {
          adjacentPoints.add(new int[]{adjX, adjY});
        }
      }
    }
    return adjacentPoints;
  }

  //Mutators
  public void generate(@NotNull Point pos) {
    generate(pos.x, pos.y);
  }

  public void generate(int x, int y) {
    generate(x, y, mineChance);
  }

  public void generate(int x, int y, double mineChance) {
    Square square;
    if (Math.random() < mineChance) {
      square = new BombSquare(x, y);
    } else {
      square = new NumberSquare(x, y);
    }
    put(x, y, square);
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
  public void flag(int x, int y) {
    flag(get(x, y));
  }
  public void flag(Square square) {
    square.toggleFlagged();
    updateImage(square);
  }

  public void reveal(Point p) {
    reveal(p.x, p.y);
  }

  public void reveal(int x, int y) {
    reveal(x, y, mineChance);
  }

  public void reveal(int x, int y, double generateMineChance) {
    reveal(x, y, generateMineChance, true);
  }

  public void reveal(int x, int y, double generateMineChance, boolean doMassReveal) {

    Square square = get(x, y);
    if (!square.isFlagged() && !square.isRevealed()) {
      if (square instanceof NumberSquare numSquare) {
        int squareNumber = 0;
        for (int xVal = x - 1; xVal <= x + 1; xVal++) {
          for (int yVal = y - 1; yVal <= y + 1; yVal++) {
            if (!exists(xVal, yVal)) {
              generate(xVal, yVal, generateMineChance);
            }
            if (isBomb(xVal, yVal)) {
              squareNumber++;
            }
          }
        }
        numSquare.setRevealedTrue(squareNumber);
        if (squareNumber == 0 && doMassReveal) {
          massReveal(x, y);
        }
      } else if (square instanceof BombSquare bombSquare) {
        bombSquare.setRevealedTrue();
      }
    }
    dimensions.expand(square.getX(), square.getY());
  }

  public void massReveal(int x, int y) {
    Board revealBoard = new Board(gp);
    revealBoard.put(x, y, get(x, y));
    massReveal(revealBoard, 50);
  }

  private void massReveal(Board revealBoard, int recursionCount) {
    Board nextRevealBoard = new Board(gp);
    for (Square square : revealBoard.getSquareList()) {
      NumberSquare revealSquare = (NumberSquare) square;
      reveal(revealSquare.getX(), revealSquare.getY(), mineChance, false);
      if (revealSquare.getNumber() == 0 && recursionCount > 0) {
        for (int[] xy : getAdjacentPoints(revealSquare.getX(), revealSquare.getY())) {
          Square surroundingSquare = get(xy[0], xy[1]);
          if (
              !nextRevealBoard.exists(xy[0], xy[1])
                  && !surroundingSquare.isRevealed()
                  && !surroundingSquare.isFlagged()
          ) {
            nextRevealBoard.put(xy[0], xy[1], surroundingSquare);
          }
        }
      }
    }
    if (recursionCount > 0 && !nextRevealBoard.getBoardMap().isEmpty()) {
      massReveal(nextRevealBoard, recursionCount - 1);
    }
  }

  /**
   * Draws the initial board image.
   * Squares outside minX, minY, maxX & maxY will be out of bounds & not drawn
   */
  public void drawInitialImage() {
    int tileSize = getTileSize();
    image = UIHandler.makeFormattedImage(
        (1 + (maxX - minX)) * tileSize,
        (1 + (maxY - minY)) * tileSize
    );
    Graphics2D g2 = image.createGraphics();
    for (Map.Entry<Integer, Map<Integer, Square>> column : boardMap.entrySet()) {
      int x = column.getKey();
      for (int y : column.getValue().keySet()) {
        if (x < minX || x > maxX || y < minY || y > maxY) {
          System.out.println(
              "x=" + x + " & y=" + y + ": Square is out of bounds"
          );
        } else {
          g2.drawImage(get(x, y).getTx(), (x - minX) * tileSize, (y - minY) * tileSize, null);
        }
      }
    }
    g2.dispose();
  }

  public void updateImage(Square square) {
    int x = square.getX();
    int y = square.getY();
    int tileSize = getTileSize();
    if (this.image == null) {
      minX = x;
      minY = y;
      image = gp.uiH.makeFormattedImage(tileSize, tileSize);
      Graphics2D g2 = image.createGraphics();
      g2.drawImage(square.getTx(), 0, 0, null);
      g2.dispose();
      return;
    }
    if (x < minX) {
      BufferedImage newImage = gp.uiH.makeFormattedImage(
          image.getWidth() + ((minX - x) * tileSize), image.getHeight()
      );
      Graphics2D newG2 = newImage.createGraphics();
      newG2.drawImage(image, (minX - x) * tileSize, 0, null);
      newG2.dispose();
      image = newImage;
      minX = x;
    } else if (x > maxX) {
      BufferedImage newImage = gp.uiH.makeFormattedImage(
          image.getWidth() + ((x - maxX) * tileSize), image.getHeight()
      );
      Graphics2D newG2 = newImage.createGraphics();
      newG2.drawImage(image, 0, 0, null);
      newG2.dispose();
      image = newImage;
      maxX = x;
    }
    if (y < minY) {
      BufferedImage newImage = gp.uiH.makeFormattedImage(
          image.getWidth(), image.getHeight() + ((minY - y) * tileSize)
      );
      Graphics2D newG2 = newImage.createGraphics();
      newG2.drawImage(image, 0, (minY - y) * tileSize, null);
      newG2.dispose();
      image = newImage;
      minY = y;
    } else if (y > maxY) {
      BufferedImage newImage = gp.uiH.makeFormattedImage(
          image.getWidth(), image.getHeight() + ((y - maxY) * tileSize)
      );
      Graphics2D newG2 = newImage.createGraphics();
      newG2.drawImage(image, 0, 0, null);
      newG2.dispose();
      image = newImage;
      maxY = y;
    }
    Graphics2D g2 = image.createGraphics();
    g2.drawImage(square.getTx(), (x - minX) * tileSize, (y - minY) * tileSize, null);
    g2.dispose();
  }

  private Square makeNumberSquare(String squareStr) {
    String[] squareArr = squareStr.split("&");
    Square square;
    int x = Integer.parseInt(squareArr[1]);
    int y = Integer.parseInt(squareArr[2]);
    if (Objects.equals(squareArr[0], "number")) {
      square = new NumberSquare(x, y);
      NumberSquare numSquare = (NumberSquare) square;
      if (Boolean.parseBoolean(squareArr[4])) {
        numSquare.setRevealedTrue(Integer.parseInt(squareArr[5]));
      }
    } else if (Objects.equals(squareArr[0], "bomb")) {
      square = new BombSquare(x, y);
    } else {
      throw new RuntimeException("Cannot recognize square: " + squareArr[0]);
    }
    if (Boolean.parseBoolean(squareArr[3])) {
      square.toggleFlagged();
    }
    return square;
  }


  public void load(String filename) {
    this.filename = filename;
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String boardStr = br.readLine();
      for (String squareStr : boardStr.split("\\|")) {
        Square square = makeNumberSquare(squareStr);
        int x = square.getX();
        int y = square.getY();
        this.put(x, y, square);


      }
      drawInitialImage();
    } catch (IOException e) {
      System.out.println(
              "Could not load data from file: \"" + filename + "\". The file might not exist"
      );
    }
  }

  public void save() {
    if (filename == null) {
      System.out.println("No filename is associated with this board");
      return;
    }
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
      StringBuilder boardStr = new StringBuilder();
      for (Map<Integer, Square> xMap : boardMap.values()) {
        for (Square square : xMap.values()) {
          if (square instanceof NumberSquare) {
            boardStr.append("number");
          } else if (square instanceof BombSquare) {
            boardStr.append("bomb");
          }
          boardStr.append("&");
          boardStr.append(square.getX());
          boardStr.append("&");
          boardStr.append(square.getY());
          boardStr.append("&");
          boardStr.append(square.isFlagged());
          if (square instanceof NumberSquare numSquare) {
            boardStr.append("&");
            boardStr.append(numSquare.isRevealed());
            boardStr.append("&");
            boardStr.append(numSquare.getNumber());
          }
          boardStr.append("|");
        }
      }
      boardStr.deleteCharAt(boardStr.length() - 1);

      bw.write(boardStr.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
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

    public Dimensions(int minX, int minY, int maxX, int maxY) {
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

    private void expand(int newX, int newY) {
      this.minX = Math.min(minX, newX);
      this.minY = Math.min(minY, newY);
      this.maxX = Math.max(maxX, newX);
      this.maxY = Math.max(maxY, newY);
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
