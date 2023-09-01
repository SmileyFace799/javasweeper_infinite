package smiley.javasweeper.view;

import java.awt.*;
import javax.swing.*;
import smiley.javasweeper.controllers.keyboard.Keyboard;
import smiley.javasweeper.controllers.mouse.Mouse;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.intermediary.ModelEventListener;
import smiley.javasweeper.intermediary.ModelManager;
import smiley.javasweeper.intermediary.events.ModelEvent;
import smiley.javasweeper.intermediary.events.SettingsLoadedEvent;
import smiley.javasweeper.view.screens.GameplayScreen;
import smiley.javasweeper.view.screens.ScreenManager;
import smiley.javasweeper.view.screens.StartupScreen;

public class GamePanel implements ModelEventListener {

    //CONSTANTS
    public static final int FPS = 60;
    private final JFrame window;
    private Thread gameThread;
    private Point startDragCamera;
    private boolean debugEnabled = false;
    private final JPanel jPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            GraphicManager.getInstance().drawScreen(g2, debugEnabled);
            g2.dispose();
        }
    };

    //Constructor
    public GamePanel(JFrame window) {
        this.window = window;

        ModelManager.getInstance().addListener(this);

        jPanel.setDoubleBuffered(true);
        jPanel.setFocusable(true);
        jPanel.setBackground(Color.black);
        jPanel.addMouseListener(Mouse.getInstance());
        jPanel.addMouseMotionListener(Mouse.getInstance());
        jPanel.addKeyListener(Keyboard.getInstance());

        ScreenManager.getInstance().makeScreens(this);
        ScreenManager.getInstance().changeScreen(StartupScreen.class);
    }

    //accessors
    public JFrame getWindow() {
        return window;
    }

    public JPanel getJPanel() {
        return jPanel;
    }


    public Point getStartDragCamera() {
        return startDragCamera;
    }

    public void setStartDragCamera(Point pos) {
        startDragCamera = pos;
    }

    //mutators
    public void setWindowUndecorated(boolean undecorated) {
        window.dispose();
        window.setUndecorated(undecorated);
        window.setVisible(true);
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
                    jPanel.repaint();
                    delta--;
                }
            }
        });
        gameThread.start();
        ModelManager.getInstance().appStarted();
    }

    private void enableFullscreen() {
        Rectangle screenBounds = GraphicManager.GRAPHICS_CONFIG.getBounds();
        GraphicManager.getInstance().setWindowSize(screenBounds.width, screenBounds.height);
        if (GraphicManager.GRAPHICS_DEVICE.getFullScreenWindow() == null) {
            setWindowUndecorated(true);
            GraphicManager.GRAPHICS_DEVICE.setFullScreenWindow(window);
        }
    }

    private void disableFullscreen() {
        GraphicManager.getInstance().setWindowSize(
                Settings.getInstance().getDisplayWidth(),
                Settings.getInstance().getDisplayHeight()
        );
        if (GraphicManager.GRAPHICS_DEVICE.getFullScreenWindow() != null) {
            setWindowUndecorated(false);
            GraphicManager.GRAPHICS_DEVICE.setFullScreenWindow(null);
        }
    }

    public void toggleFullscreen() {
        Settings.getInstance().toggleFullscreen();
        if (Settings.getInstance().isFullscreen()) {
            disableFullscreen();
        } else {
            enableFullscreen();
        }
    }

    @Override
    public void onEvent(ModelEvent me) {
        if (me instanceof SettingsLoadedEvent) {
            jPanel.setPreferredSize(new Dimension(
                    Settings.getInstance().getDisplayWidth(),
                    Settings.getInstance().getDisplayHeight()
            ));
        }
    }
}
