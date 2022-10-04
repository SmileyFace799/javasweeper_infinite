package main;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import squares.BombSquare;
import squares.NumberSquare;
import squares.Square;

public class Board extends HashMap<Integer, HashMap<Integer, Square>> {
  final double mineChance;
  final GamePanel gp;
  final int tileSize;
  private BufferedImage image;
  private String associatedFilename;

  private int minX;
  private int minY;
  private int maxX;
  private int maxY;


  public Board(GamePanel gp) { //Defaults to 0 mineChance
    this.mineChance = 0;
    this.gp = gp;
    this.tileSize = gp.tileSize;
  }

  public Board(double mineChance, GamePanel gp) {
    this.mineChance = mineChance;
    this.gp = gp;
    this.tileSize = gp.tileSize;
  }

  //Accessors
  public int getMinX() {
    return minX;
  }

  public int getMinY() {
    return minY;
  }

  public int getTileSize() {
    return tileSize;
  }

  public double getMineChance() {
    return mineChance;
  }

  public BufferedImage getImage() {
    return image;
  }

  public boolean exists(@NotNull Point pos) {
    return exists(pos.x, pos.y);
  }

  public boolean exists(int x, int y) {
    return this.containsKey(x) && this.get(x).containsKey(y);
  }

  public Square get(Point pos) {
    return get(pos.x, pos.y);
  }

  public Square get(int x, int y) {
    if (exists(x, y)) {
      return super.get(x).get(y);
    } else {
      System.out.println("get: There is no square at x=" + x + " & y=" + y);
      return null;
    }
  }

  public boolean isBomb(int x, int y) {
    return get(x, y) instanceof BombSquare;
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
      square = new BombSquare(x, y, this, gp.txMap);
    } else {
      square = new NumberSquare(x, y, this, gp.txMap);
    }
    put(x, y, square);
  }

  public void put(int x, int y, Square square) {
    put(x, y, square, true);
  }

  public void put(int x, int y, Square square, boolean updateImg) {
    if (!this.containsKey(x)) {
      this.put(x, new HashMap<>());
    }
    if (!this.get(x).containsKey(y)) {
      this.get(x).put(y, square);
    } else {
      System.out.println("put: A square already exists at x=" + x + " & y=" + y);
    }
    if (updateImg) {
      updateImage(square);
    }
  }

  public void massReveal(int x, int y) {
    Board revealBoard = new Board(gp);
    revealBoard.put(x, new HashMap<>());
    revealBoard.get(x).put(y, get(x, y));
    massReveal(revealBoard, 50);
  }

  private void massReveal(Board revealBoard, int recursionCount) {
    Board nextRevealBoard = new Board(gp);
    for (int x : revealBoard.keySet()) {
      for (int y : revealBoard.get(x).keySet()) {
        NumberSquare revealSquare = (NumberSquare) get(x, y);
        revealSquare.reveal(mineChance, false);
        if (revealSquare.getNumber() == 0 && recursionCount > 0) {
          for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
              NumberSquare surroundingSquare = (NumberSquare) get(x + dx, y + dy);
              if (
                  !nextRevealBoard.exists(x + dx, y + dy)
                      && !surroundingSquare.isRevealed()
                      &&
                      !surroundingSquare.isFlagged()
              ) {
                nextRevealBoard.put(x + dx, y + dy, surroundingSquare);
              }
            }
          }
        }
      }
    }
    if (recursionCount > 0) {
      massReveal(nextRevealBoard, recursionCount - 1);
    }
  }

  /**
   * Draws the initial board image.
   * Squares outside minX, minY, maxX & maxY will be out of bounds & not drawn
   */
  public void drawInitialImage() {
    image = new BufferedImage(
        (1 + (maxX - minX)) * tileSize,
        (1 + (maxY - minY)) * tileSize,
        BufferedImage.TYPE_INT_ARGB
    );
    Graphics2D g2 = image.createGraphics();
    for (Entry<Integer, HashMap<Integer, Square>> column : this.entrySet()) {
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
    if (this.image == null) {
      minX = x;
      minY = y;
      image = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = image.createGraphics();
      g2.drawImage(square.getTx(), 0, 0, tileSize, tileSize, null);
      return;
    }
    if (x < minX) {
      BufferedImage newImage = new BufferedImage(
          image.getWidth() + ((minX - x) * tileSize), image.getHeight(), image.getType()
      );
      Graphics2D newG2 = newImage.createGraphics();
      newG2.drawImage(image, (minX - x) * tileSize, 0, null);
      newG2.dispose();
      image = newImage;
      minX = x;
    } else if (x > maxX) {
      BufferedImage newImage = new BufferedImage(
          image.getWidth() + ((x - maxX) * tileSize), image.getHeight(), image.getType()
      );
      Graphics2D newG2 = newImage.createGraphics();
      newG2.drawImage(image, 0, 0, null);
      newG2.dispose();
      image = newImage;
      maxX = x;
    }
    if (y < minY) {
      BufferedImage newImage = new BufferedImage(
          image.getWidth(), image.getHeight() + ((minY - y) * tileSize), image.getType()
      );
      Graphics2D newG2 = newImage.createGraphics();
      newG2.drawImage(image, 0, (minY - y) * tileSize, null);
      newG2.dispose();
      image = newImage;
      minY = y;
    } else if (y > maxY) {
      BufferedImage newImage = new BufferedImage(
          image.getWidth(), image.getHeight() + ((y - maxY) * tileSize), image.getType()
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

  public void load(String filename) {
    associatedFilename = filename;
    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      String boardStr = br.readLine();
      for (String squareStr : boardStr.split("\\|")) {
        String[] squareArr = squareStr.split("&");
        Square square;
        int x = Integer.parseInt(squareArr[1]);
        int y = Integer.parseInt(squareArr[2]);
        if (Objects.equals(squareArr[0], "number")) {
          square = new NumberSquare(x, y, this, gp.txMap);
          NumberSquare numSquare = (NumberSquare) square;
          if (Boolean.parseBoolean(squareArr[4])) {
            numSquare.setRevealed(Integer.parseInt(squareArr[5]));
          }
        } else if (Objects.equals(squareArr[0], "bomb")) {
          square = new BombSquare(x, y, this, gp.txMap);
        } else {
          throw new RuntimeException("Cannot recognize square: " + squareArr[0]);
        }
        if (Boolean.parseBoolean(squareArr[3])) {
          square.flag();
        }
        if (this.isEmpty()) {
          minX = x;
          minY = y;
          maxX = x;
          maxY = y;
        } else {
          if (x < minX) {
            minX = x;
          } else if (x > maxX) {
            maxX = x;
          }
          if (y < minY) {
            minY = y;
          } else if (y > maxY) {
            maxY = y;
          }
        }
        put(x, y, square, false);
      }
      br.close();
      drawInitialImage();
    } catch (IOException e) {
      System.out.println(
          "Could not load data from file: \"" + filename + "\". The file might not exist"
      );
    }
  }

  public void save() {
    if (associatedFilename == null) {
      System.out.println("No filename is associated with this board");
      return;
    }
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(associatedFilename));
      StringBuilder boardStr = new StringBuilder();
      for (int x : this.keySet()) {
        for (int y : get(x).keySet()) {
          Square square = get(x, y);
          if (square instanceof NumberSquare) {
            boardStr.append("number");
          } else if (square instanceof BombSquare) {
            boardStr.append("bomb");
          }
          boardStr.append("&");
          boardStr.append(x);
          boardStr.append("&");
          boardStr.append(y);
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
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
