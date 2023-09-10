package smiley.javasweeper.view;

import java.awt.*;
import javax.swing.*;
import smiley.javasweeper.controllers.mouse.InputHandler;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.intermediary.FileEventListener;
import smiley.javasweeper.intermediary.FileManager;
import smiley.javasweeper.intermediary.events.file.FileEvent;
import smiley.javasweeper.intermediary.events.file.SettingUpdatedEvent;
import smiley.javasweeper.intermediary.events.file.SettingsLoadedEvent;
import smiley.javasweeper.view.screens.StartupScreen;

public class GamePanel implements FileEventListener {

    //CONSTANTS
    public static final int FPS = 60;
    private final JFrame window;
    private Thread gameThread;
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

        FileManager.getInstance().addListener(this);

        jPanel.setDoubleBuffered(true);
        jPanel.setFocusable(true);
        jPanel.setBackground(Color.black);
        jPanel.addMouseListener(InputHandler.getInstance());
        jPanel.addMouseMotionListener(InputHandler.getInstance());
        jPanel.addKeyListener(InputHandler.getInstance());
        jPanel.setPreferredSize(new Dimension(
                Settings.getDefault(Settings.Keys.DISPLAY_WIDTH, Integer.class),
                Settings.getDefault(Settings.Keys.DISPLAY_HEIGHT, Integer.class)
        ));

        ViewManager.getInstance().makeScreens(this);
        ViewManager.getInstance().changeScreen(StartupScreen.class);
    }

    public JPanel getJPanel() {
        return jPanel;
    }

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
        gameThread.setPriority(Thread.MAX_PRIORITY);
        gameThread.start();
        FileManager.getInstance().appStarted();
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
                GraphicManager.getInstance().getPreferredWindowWidth(),
                GraphicManager.getInstance().getPreferredWindowHeight()
        );
        if (GraphicManager.GRAPHICS_DEVICE.getFullScreenWindow() != null) {
            setWindowUndecorated(false);
            GraphicManager.GRAPHICS_DEVICE.setFullScreenWindow(null);
        }
    }

    @Override
    public void onEvent(FileEvent fe) {
        if (fe instanceof SettingsLoadedEvent sle) {
            Settings settings = sle.settings();
            jPanel.setPreferredSize(new Dimension(
                    settings.getDisplayWidth(),
                    settings.getDisplayHeight()

            ));
        } else if (fe instanceof SettingUpdatedEvent sue) {
            switch (sue.setting()) {
                case (Settings.Keys.DISPLAY_WIDTH) -> jPanel.setPreferredSize(new Dimension(
                        sue.value(Integer.class),
                        GraphicManager.getInstance().getPreferredWindowHeight()
                ));
                case (Settings.Keys.DISPLAY_HEIGHT) -> jPanel.setPreferredSize(new Dimension(
                        GraphicManager.getInstance().getPreferredWindowWidth(),
                        sue.value(Integer.class)
                ));
                case (Settings.Keys.FULLSCREEN) -> {
                    if (sue.value(Boolean.class)) {
                        enableFullscreen();
                    } else {
                        disableFullscreen();
                    }
                }
                default -> {
                    //Do nothing
                }
            }
        }
    }
}
