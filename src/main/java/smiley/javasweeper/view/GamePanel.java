package smiley.javasweeper.view;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import smiley.javasweeper.controllers.mouse.InputHandler;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.intermediary.FileManager;
import smiley.javasweeper.view.screens.StartupScreen;

public class GamePanel {

    //CONSTANTS
    public static final int FPS = 60;

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
    public GamePanel() {

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

    public void toggleDebug() {
        debugEnabled = !debugEnabled;
    }

    //other
    public void startGameThread() {
        gameThread = new Thread(() -> {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Game thread started");
            double drawInterval = 1e9 / FPS; //1e9: 1 second in nanoseconds
            double delta = 0;
            long lastTime = System.nanoTime();
            long currentTime;

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
}
