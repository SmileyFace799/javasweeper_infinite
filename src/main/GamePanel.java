package main;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import squares.Square;
import squares.BombSquare;
import squares.NumberSquare;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class GamePanel extends JPanel implements Runnable{

    //SETTINGS (configurable)
    final double scale = 2.5;
    final double mineChance = 27.5 / 100;

    //SETTINGS
    final int originalTileSize = 16;

    final int tileSize = (int) Math.round(originalTileSize * scale);
    final int screenWidth = 1920;
    final int screenHeight = 1080;

    //FPS
    int fps = 60;

    //things lol
    private Thread gameThread;
    final MouseHandler mouseH;
    final MouseMotionHandler mouseMH;
    final KeyHandler keyH;
    final HashMap<Integer, HashMap<Integer, Square>> board = new HashMap<>();

    final HashMap<String, BufferedImage> txMap = new HashMap<>();

    //Game variables
    private boolean gamePaused = false;
    private Point cameraOffset = new Point(-(screenWidth - tileSize) / 2, -(screenHeight - tileSize) / 2);
    private Point startDragCamera;

    //Constructor
    public GamePanel() {
        DataHandler settings = new DataHandler<String, Object>("settings");
        settings.put("bruh", "69");
        String a = (String) settings.get("bruh");
        System.out.println(a);

        mouseH = new MouseHandler(this);
        mouseMH = new MouseMotionHandler(this);
        keyH = new KeyHandler(this);
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.setBackground(Color.black);
        this.addMouseListener(mouseH);
        this.addMouseMotionListener(mouseMH);
        this.addKeyListener(keyH);
        try {
            String[] fileNames = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "bombDetonated", "bombRevealed", "flag", "flagWrong", "hidden"};
            for (String name: fileNames) {
                txMap.put(name, ImageIO.read(getClass().getResourceAsStream("/imgs/" + name + ".bmp")));
            }
        } catch (IOException e) {e.printStackTrace();}
        Point startPoint = new Point(0, 0);
        this.generateSquare(startPoint, 0);
        getSquare(startPoint).reveal(0);
    }

    //accessors
    public int getScreenWidth() {return screenWidth;}
    public int getScreenHeight() {return screenHeight;}
    public int getTileSize() {return tileSize;}
    public double getMineChance() {return mineChance;}
    public Point getCameraOffset() {return cameraOffset;}
    public boolean squareExists(Point pos) {return board.containsKey(pos.x) && board.get(pos.x).containsKey(pos.y);}
    public Square getSquare(Point pos) {
        if (squareExists(pos)) {
            return board.get(pos.x).get(pos.y);
        } else {
            System.out.println("getSquare: There is no square at x=" + pos.x + " & y=" + pos.y);
            return null;
        }
    }
    public Point getStartDragCamera() {return startDragCamera;}

    //mutators
    public void setCameraOffset(Point pos) {cameraOffset = pos;}
    public void setStartDragCamera(Point pos) {startDragCamera = pos;}
    public boolean generateSquare(@NotNull Point pos) {return generateSquare(pos, this.mineChance);}
    public boolean generateSquare(@NotNull Point pos, double mineChance) { //Returns true if it generated a bomb, false if not. If no square was generated, this returns false
        if (!board.containsKey(pos.x)) {
            board.put(pos.x, new HashMap<>());
        }
        if (!board.get(pos.x).containsKey(pos.y)) {
            Square square;
            boolean mineGenerated = Math.random() < mineChance;
            if (mineGenerated) {
                square = new BombSquare(pos, this, mouseH, keyH, txMap);
            } else {
                square = new NumberSquare(pos, this, mouseH, keyH, txMap);
            }
            board.get(pos.x).put(pos.y, square);
            return mineGenerated;
        } else {
            System.out.println("generateSquare: A square already exists at x=" + pos.x + " & y=" + pos.y);
            return false;
        }
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
            }

            if (timer >= 1e9) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        HashMap<String, Point> boardPoint = new HashMap<>();
        for (String button: mouseH.mouseButtons) {
            if (mouseH.clicked.get(button)) {
                Point pressPos = mouseH.pressPos.get(button);
                boardPoint.put(button, new Point(
                        Math.floorDiv((pressPos.x + cameraOffset.x), tileSize),
                        Math.floorDiv((pressPos.y + cameraOffset.y), tileSize)
                ));
            }
        }

        if (mouseH.clicked.get("lmb")) {
            if (!this.squareExists(boardPoint.get("lmb"))) {
                this.generateSquare(boardPoint.get("lmb"));
            }
            getSquare(boardPoint.get("lmb")).reveal();
        }
        if (mouseH.clicked.get("rmb") && this.squareExists(boardPoint.get("rmb"))) {
            Square clickedSquare = getSquare(boardPoint.get("rmb"));
            if (!clickedSquare.isRevealed()) {
                clickedSquare.flag();
            }
        } else if (mouseH.clicked.get("wheel")) {
            Point pos = boardPoint.get("wheel"); //pos: Location clicked by the mouse-wheel
            Square clickedSquare = getSquare(pos);
            if (this.squareExists(pos) && clickedSquare instanceof NumberSquare clickedNum && clickedNum.isRevealed()) {
                int flagCounter = 0; //Flags surrounding the number
                for (int x = pos.x - 1; x <= pos.x + 1; x++) {
                    for (int y = pos.y - 1; y <= pos.y + 1; y++) {
                        if (getSquare(new Point(x, y)).isFlagged()) {
                            flagCounter++;
                        }
                    }
                }
                if (clickedNum.getNumber() == flagCounter) {
                    for (int x = pos.x - 1; x <= pos.x + 1; x++) {
                        for (int y = pos.y - 1; y <= pos.y + 1; y++) {
                            Square squareToReveal = getSquare(new Point(x, y));
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

        for (int x: board.keySet()) {
            for (int y: board.get(x).keySet()) {
                getSquare(new Point(x, y)).draw(g2, cameraOffset);
            }
        }

        g2.dispose();
    }
}
