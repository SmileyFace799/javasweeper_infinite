package smiley.javasweeper.view;

import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import smiley.javasweeper.controllers.keyboard.KeyHandler;
import smiley.javasweeper.controllers.keyboard.Keyboard;
import smiley.javasweeper.controllers.mouse.Mouse;
import smiley.javasweeper.controllers.mouse.MouseButtonsOld;
import smiley.javasweeper.controllers.mouse.MouseMotionHandlerOld;
import smiley.javasweeper.controllers.UIHandler;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.intermediary.ModelManager;
import smiley.javasweeper.model.Board;
import smiley.javasweeper.view.StateHandler;
import smiley.javasweeper.view.screens.GameState;
import smiley.javasweeper.view.screens.GameplayScreen;
import smiley.javasweeper.view.screens.PauseState;
import smiley.javasweeper.view.screens.PlayState;
import smiley.javasweeper.view.screens.ScreenHandler;
import smiley.javasweeper.view.screens.SettingsState;

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
  public final UIHandler uiH;

  private Point cameraOffset;
  private Point startDragCamera;
  private boolean debugEnabled = false;

  //Constructor
  public GamePanel(JFrame window) {
    this.window = window;

    uiH = new UIHandler(this);


    jPanel.setPreferredSize(new Dimension(Settings.getDisplayWidth(), Settings.getDisplayHeight()));
    jPanel.setDoubleBuffered(true);
    jPanel.setFocusable(true);
    jPanel.setBackground(Color.black);
    Arrays.stream(Mouse.Buttons.values()).forEach(jPanel::addMouseListener);
    jPanel.addMouseMotionListener(Mouse.getInstance());
    jPanel.addKeyListener(Keyboard.getInstance());

    ScreenHandler.getInstance().makeScreens(this);
    ScreenHandler.getInstance().changeScreen(GameplayScreen.class);

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
    ModelManager.getInstance().appStarted();
  }

  public void update() {
    stateH.getActive().update();


    mouseH.frameUpdated();
    keyH.frameUpdated();
  }
}
