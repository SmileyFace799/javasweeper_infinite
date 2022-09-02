package main;

import squares.Bomb;
import squares.Square;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class GamePanel extends JPanel implements Runnable{

    //SETTINGS
    final int originalTileSize = 8;
    final int scale = 5;

    final int tileSize = originalTileSize * scale;
    final int maxScreenCol = 48;
    final int maxScreenRow = 27;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;

    //FPS
    int fps = 60;

    MouseHandler mouseH = new MouseHandler();
    KeyHandler keyH = new KeyHandler();

    private Thread gameThread;
    Bomb testbomb = new Bomb(this, keyH);

    //Game variables
    private HashMap<Integer, HashMap<Integer, Square>> board = new HashMap<>();
    private boolean gamePaused = false;
    private int testX = 0;

    //Constructor
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.setBackground(Color.black);
        this.addMouseListener(mouseH);
        this.addKeyListener(keyH);
    }

    //accessors
    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
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
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
                testX++;
            }

            if (timer >= 1e9) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        if (keyH.getEscTapped()) {
            gamePaused = !gamePaused;
        }

        keyH.frameUpdated();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.white);
        if (gamePaused) {
            g2.fillRect(testX, 100, tileSize, tileSize);
        }

        g2.dispose();
    }
}
