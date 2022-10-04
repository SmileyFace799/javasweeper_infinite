package main;

import java.awt.*;
import java.util.HashMap;
import javax.swing.*;

import squares.NumberSquare;

public class GamePanel extends JPanel implements Runnable {

  //CONSTANTS
  final JFrame window;
  static final int ORIGINAL_TILE_SIZE = 16;

  static final int STATE_GAME = 0;
  static final int STATE_GAME_OVER = 1;
  static final int STATE_PAUSED = 2;
  static final int STATE_SETTINGS = 3;

  private transient Thread gameThread;
  final transient MouseHandler mouseH;
  final transient MouseMotionHandler mouseMotionH;
  final transient KeyHandler keyH;
  final transient StateHandler stateH;
  final transient UI ui;

  final TxMap txMap = new TxMap("imgs");

  //SETTINGS (configurable)
  final JsonMap<Object> settings;
  final int screenWidth;
  final int screenHeight;
  final double boardScale;
  final int tileSize;
  final double mineChance;
  final double uiScale;

  //FPS
  int fps = 60;

  //Game variables
  final Board board;
  private Point cameraOffset;
  private Point startDragCamera;
  final HashMap<Integer, Point> clickedBoardPoints = new HashMap<>();
  private boolean debugEnabled = false;

  //Constructor
  public GamePanel(JFrame window) {
    this.window = window;

    settings = new JsonMap<>("res/settings.json");
    screenWidth = (int) settings.get("screenWidth");
    screenHeight = (int) settings.get("screenHeight");
    boardScale = (double) settings.get("boardScale");
    tileSize = (int) Math.round(ORIGINAL_TILE_SIZE * boardScale);
    mineChance = ((double) settings.get("mineChance")) / 100;
    uiScale = (double) settings.get("UIScale");
    cameraOffset = new Point(-(screenWidth - tileSize) / 2, -(screenHeight - tileSize) / 2);

    mouseH = new MouseHandler(this);
    mouseMotionH = new MouseMotionHandler(this);
    keyH = new KeyHandler(this);
    stateH = new StateHandler();
    ui = new UI(this);

    window.setSize(new Dimension(screenWidth, screenHeight));
    this.setPreferredSize(new Dimension(screenWidth, screenHeight));
    this.setDoubleBuffered(true);
    this.setFocusable(true);
    this.setBackground(Color.black);
    this.addMouseListener(mouseH);
    this.addMouseMotionListener(mouseMotionH);
    this.addKeyListener(keyH);

    stateH.addState(STATE_GAME, new GameState(this));
    stateH.addState(STATE_PAUSED, new PauseState(this));
    stateH.addState(STATE_SETTINGS, new SettingsState(this));

    board = new Board(mineChance, this);
    board.load("res/testboard.board");

    if (!board.exists(0, 0)) {
      board.generate(0, 0, 0);
      ((NumberSquare) board.get(0, 0)).reveal(0);
    }

    for (int button : mouseH.mouseButtons) {
      clickedBoardPoints.put(button, new Point());
    }
  }

  //accessors
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
    stateH.getActive().update();
    mouseH.frameUpdated();
    keyH.frameUpdated();
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;
    g2.setFont(ui.font);
    long drawStart = System.nanoTime();

    stateH.getActive().drawScreen(g2);

    if (debugEnabled) {
      g2.setColor(Color.white);
      g2.setFont(ui.defaultFont);
      long drawTime = (System.nanoTime() - drawStart);
      g2.drawString("Draw time: " + (drawTime / 1e6) + "ms", 10, 20);
      g2.drawString("Effective FPS: " + (1e9 / drawTime), 10, 40);
    }

    g2.dispose();
  }
}
