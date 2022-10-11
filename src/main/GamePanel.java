package main;

import java.awt.*;
import javax.swing.*;

import squares.NumberSquare;

public class GamePanel extends JPanel implements Runnable {

  //CONSTANTS
  public final JFrame window;
  public static final int FPS = 60;
  public static final int ORIGINAL_TILE_SIZE = 16;

  public static final int STATE_GAME = 0;
  public static final int STATE_GAME_OVER = 1;
  public static final int STATE_PAUSED = 2;
  public static final int STATE_SETTINGS = 3;

  private transient Thread gameThread;
  public final transient Settings settings;
  public final transient MouseHandler mouseH;
  public final transient MouseMotionHandler mouseMotionH;
  public final transient KeyHandler keyH;
  public final transient StateHandler stateH;
  public final transient UIHandler uiH;
  public final TxMap txMap = new TxMap("imgs");

  //VARIABLES
  private final Board board;
  private Point cameraOffset;
  private Point startDragCamera;
  private boolean debugEnabled = false;

  //Constructor
  public GamePanel(JFrame window) {
    this.window = window;
    settings = new Settings();

    mouseH = new MouseHandler(this);
    mouseMotionH = new MouseMotionHandler(this);
    keyH = new KeyHandler(this);
    stateH = new StateHandler();
    uiH = new UIHandler(this);

    cameraOffset = new Point(-(settings.getDisplayWidth() - getTileSize()) / 2, -(settings.getDisplayHeight() - getTileSize()) / 2);

    window.setSize(new Dimension(settings.getDisplayWidth(), settings.getDisplayHeight()));
    this.setPreferredSize(new Dimension(settings.getDisplayWidth(), settings.getDisplayHeight()));
    this.setDoubleBuffered(true);
    this.setFocusable(true);
    this.setBackground(Color.black);
    this.addMouseListener(mouseH);
    this.addMouseMotionListener(mouseMotionH);
    this.addKeyListener(keyH);

    stateH.addState(STATE_GAME, new GameState(this));
    stateH.addState(STATE_PAUSED, new PauseState(this));
    stateH.addState(STATE_SETTINGS, new SettingsState(this));

    txMap.replaceAll((k, v) -> uiH.convertFormat(v));

    board = new Board(settings.getMineChance(), this);
    board.load("res/testboard.board");

    if (!board.exists(0, 0)) {
      board.generate(0, 0, 0);
      ((NumberSquare) board.get(0, 0)).reveal(0);
    }
  }

  //accessors
  public int getTileSize() {
    return (int) Math.round(ORIGINAL_TILE_SIZE * settings.getBoardScale());
  }

  public Board getBoard() {
    return board;
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
    uiH.setupScreen();
  }

  @Override
  public void run() {
    double drawInterval = 1e9 / FPS; //1e9: 1 second in nanoseconds
    double delta = 0;
    long lastTime = System.nanoTime();
    long currentTime;
    System.out.println("FPS cap: " + FPS);

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
    uiH.drawScreen(g2, debugEnabled);
    g2.dispose();
  }
}
