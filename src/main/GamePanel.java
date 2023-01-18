package main;

import java.awt.*;
import javax.swing.*;

import squares.NumberSquare;

public class GamePanel implements Runnable {

  //CONSTANTS
  private final JFrame window;
  public static final int FPS = 60;
  public static final int ORIGINAL_TILE_SIZE = 16;

  private final JPanel jPanel = new JPanel() {
    @Override
    public void paintComponents(Graphics g) {
      super.paintComponent(g);

      Graphics2D g2 = (Graphics2D) g;
      uiH.drawScreen(g2, debugEnabled);
      g2.dispose();
    }
  };
  private  Thread gameThread;
  public final Settings settings;
  public final  MouseHandler mouseH;
  public final  MouseMotionHandler mouseMotionH;
  public final  KeyHandler keyH;
  public final  StateHandler stateH;
  public final  UIHandler uiH;
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

    jPanel.setPreferredSize(new Dimension(settings.getDisplayWidth(), settings.getDisplayHeight()));
    jPanel.setDoubleBuffered(true);
    jPanel.setFocusable(true);
    jPanel.setBackground(Color.black);
    jPanel.addMouseListener(mouseH);
    jPanel.addMouseMotionListener(mouseMotionH);
    jPanel.addKeyListener(keyH);

    stateH.addState(GameState.STATE_GAME, new PlayState(this));
    stateH.addState(GameState.STATE_PAUSED, new PauseState(this));
    stateH.addState(GameState.STATE_SETTINGS, new SettingsState(this));

    txMap.replaceAll((k, v) -> uiH.convertFormat(v));

    board = new Board(settings.getMineChance(), this);
    board.load("res/testboard.board");

    if (!board.exists(0, 0)) {
      board.generate(0, 0, 0);
      ((NumberSquare) board.get(0, 0)).reveal(0);
    }
  }

  //accessors
  public JFrame getWindow() {
    return window;
  }

  public JPanel getJPanel() {
    return jPanel;
  }

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
  public void setWindowUndecorated(boolean undecorated) {
    window.dispose();
    window.setUndecorated(undecorated);
    window.setVisible(true);
  }

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
        jPanel.repaint();
        delta--;
      }
    }
  }

  public void update() {
    stateH.getActive().update();


    mouseH.frameUpdated();
    keyH.frameUpdated();
  }
}
