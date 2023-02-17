package smiley.mainapp;

import smiley.squares.NumberSquare;
import java.awt.*;
import javax.swing.*;

public class GamePanel {

  //CONSTANTS
  public static final int FPS = 60;

  private final JFrame window;
  private final JPanel jPanel = new JPanel() {
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      Graphics2D g2 = (Graphics2D) g;
      uiH.drawScreen(g2, debugEnabled);
      g2.dispose();
    }
  };
  private Thread gameThread;
  public final MouseHandler mouseH;
  public final MouseMotionHandler mouseMotionH;
  public final KeyHandler keyH;
  public final StateHandler stateH;
  public final UIHandler uiH;

  //VARIABLES
  private final Board board;
  private Point cameraOffset;
  private Point startDragCamera;
  private boolean debugEnabled = false;

  //Constructor
  public GamePanel(JFrame window) {
    this.window = window;

    mouseH = new MouseHandler(this);
    mouseMotionH = new MouseMotionHandler(this);
    keyH = new KeyHandler(this);
    stateH = new StateHandler();
    uiH = new UIHandler(this);


    jPanel.setPreferredSize(new Dimension(Settings.getDisplayWidth(), Settings.getDisplayHeight()));
    jPanel.setDoubleBuffered(true);
    jPanel.setFocusable(true);
    jPanel.setBackground(Color.black);
    jPanel.addMouseListener(mouseH);
    jPanel.addMouseMotionListener(mouseMotionH);
    jPanel.addKeyListener(keyH);

    stateH.addState(GameState.STATE_GAME, new PlayState(this));
    stateH.addState(GameState.STATE_PAUSED, new PauseState(this));
    stateH.addState(GameState.STATE_SETTINGS, new SettingsState(this));
    stateH.setActive(GameState.STATE_GAME);

    board = new Board(Settings.getMineChance(), this);
    board.load("src/main/resources/boards/testboard.board");

    if (!board.exists(0, 0)) {
      board.generate(0, 0, 0);
      board.reveal(0, 0, 0);
    }
    cameraOffset = new Point(-(Settings.getDisplayWidth() - Board.getTileSize()) / 2, -(Settings.getDisplayHeight() - Board.getTileSize()) / 2);
  }

  //accessors
  public JFrame getWindow() {
    return window;
  }

  public JPanel getJPanel() {
    return jPanel;
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
    gameThread = new Thread(() -> {
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
    });
    gameThread.start();
    uiH.setupScreen();
  }

  public void update() {
    stateH.getActive().update();


    mouseH.frameUpdated();
    keyH.frameUpdated();
  }
}
