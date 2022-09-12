package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashMap;
import javax.swing.JPanel;
import squares.NumberSquare;
import squares.Square;

public class GamePanel extends JPanel implements Runnable {

  //CONSTANTS
  final int originalTileSize = 16;

  private Thread gameThread;
  final MouseHandler mouseH;
  final MouseMotionHandler mouseMotionH;
  final KeyHandler keyH;

  final TxMap txMap = new TxMap("imgs");

  //SETTINGS (configurable)
  final int screenWidth;
  final int screenHeight;
  final double scale;
  final int tileSize;
  final double mineChance;

  //FPS
  int fps = 60;

  //Game variables
  final Board board;
  private Point cameraOffset;
  private Point startDragCamera;
  final HashMap<String, Point> clickedBoardPoints = new HashMap<>();
  private boolean gamePaused = false;
  private boolean debugEnabled = false;

  //Constructor
  public GamePanel() {
    JsonMap<Object> settings = new JsonMap<>("settings.json");
    screenWidth = (int) settings.get("screenWidth");
    screenHeight = (int) settings.get("screenHeight");
    scale = (double) settings.get("UIScale");
    tileSize = (int) Math.round(originalTileSize * scale);
    mineChance = ((double) settings.get("mineChance")) / 100;
    cameraOffset = new Point(-(screenWidth - tileSize) / 2, -(screenHeight - tileSize) / 2);

    settings.put("bruh", "69");
    String a = (String) settings.get("bruh");
    System.out.println(a);

    mouseH = new MouseHandler(this);
    mouseMotionH = new MouseMotionHandler(this);
    keyH = new KeyHandler(this);
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setDoubleBuffered(true);
    this.setFocusable(true);
    this.setBackground(Color.black);
    this.addMouseListener(mouseH);
    this.addMouseMotionListener(mouseMotionH);
    this.addKeyListener(keyH);

    board = new Board(mineChance, this);
    board.load("testboard.board");

    if (!board.exists(0, 0)) {
      board.generate(0, 0, 0);
      ((NumberSquare) board.get(0, 0)).reveal(0);
    }

    for (String button : mouseH.mouseButtons) {
      clickedBoardPoints.put(button, new Point());
    }
  }

  //accessors
  public int getScreenWidth() {
    return screenWidth;
  }

  public int getScreenHeight() {
    return screenHeight;
  }

  public Point getCameraOffset() {
    return cameraOffset;
  }

  public Point getStartDragCamera() {
    return startDragCamera;
  }

  //mutators
  public void setCameraOffset(Point pos) {
    cameraOffset = pos;
  }

  public void setStartDragCamera(Point pos) {
    startDragCamera = pos;
  }

  public void toggleDebug() {
    debugEnabled = !debugEnabled;
  }

  //other
  public void startGameThread() {
    gameThread = new Thread(this);
    gameThread.start();
  }

  @Override
  public void run() {
    double drawInterval = 1e9 / fps; //1e9: 1 second in nanoseconds
    double delta = 0;
    long lastTime = System.nanoTime();
    long currentTime;
    System.out.println("FPS cap: " + fps);

    while (gameThread != null) {
      currentTime = System.nanoTime();

      delta += (currentTime - lastTime) / drawInterval;
      lastTime = currentTime;

      if (delta >= 1) {
        update();
        repaint();
        delta--;
      }
    }
  }

  public void update() {

    for (String button : mouseH.mouseButtons) {
      if (mouseH.clicked.get(button)) {
        Point pressPos = mouseH.pressPos.get(button);
        clickedBoardPoints.get(button).setLocation(
                Math.floorDiv((pressPos.x + cameraOffset.x), tileSize),
                Math.floorDiv((pressPos.y + cameraOffset.y), tileSize)
        );
      }
    }

    if (mouseH.clicked.get("lmb")) {
      if (!board.exists(clickedBoardPoints.get("lmb"))) {
        board.generate(clickedBoardPoints.get("lmb"));
      }
      board.get(clickedBoardPoints.get("lmb")).reveal();
    }
    if (mouseH.clicked.get("rmb") && board.exists(clickedBoardPoints.get("rmb"))) {
      Square clickedSquare = board.get(clickedBoardPoints.get("rmb"));
      if (!clickedSquare.isRevealed()) {
        clickedSquare.flag();
      }
    } else if (mouseH.clicked.get("wheel")) {
      Point pos = clickedBoardPoints.get("wheel"); //pos: Location clicked by the mouse-wheel
      Square clickedSquare = board.get(pos);
      if (
              this.board.exists(pos)
              && clickedSquare instanceof NumberSquare clickedNum
              && clickedNum.isRevealed()
      ) {
        int flagCounter = 0; //Number of flags surrounding the number
        for (int x = pos.x - 1; x <= pos.x + 1; x++) {
          for (int y = pos.y - 1; y <= pos.y + 1; y++) {
            if (board.get(x, y).isFlagged()) {
              flagCounter++;
            }
          }
        }
        if (clickedNum.getNumber() == flagCounter) {
          for (int x = pos.x - 1; x <= pos.x + 1; x++) {
            for (int y = pos.y - 1; y <= pos.y + 1; y++) {
              Square squareToReveal = board.get(x, y);
              if (!squareToReveal.isFlagged()) {
                squareToReveal.reveal();
              }
            }
          }
        }
      }
    }

    if (keyH.getEscTapped()) {
      gamePaused = !gamePaused;
    }

    mouseH.frameUpdated();
    keyH.frameUpdated();
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;
    g2.setColor(Color.white);
    long drawStart = System.nanoTime();
    /*
    for (int x: board.keySet()) {
      for (int y: board.get(x).keySet()) {
        board.get(x, y).draw(g2, cameraOffset);
      }
    }
     */

    g2.drawImage(board.getImage(),
            board.getMinX() * tileSize - cameraOffset.x,
            board.getMinY() * tileSize - cameraOffset.y,
            null
    );

    if (debugEnabled) {
      long drawTime = (System.nanoTime() - drawStart);
      g2.drawString("Draw time: " + (drawTime / 1e6) + "ms", 10, 20);
      System.out.println("Effective FPS: " + (1e9 / drawTime));
    }

    g2.dispose();
  }
}
