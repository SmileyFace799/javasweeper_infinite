package squares;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Square {
    final Point pos;
    final int size;

    final GamePanel gp;
    final MouseHandler mouseH;
    final KeyHandler keyH;
    final HashMap<String, BufferedImage> txMap;

    private BufferedImage tx;
    private boolean revealed = false;
    private boolean flagged = false;

    public Square(Point pos, GamePanel gp, MouseHandler mouseH, KeyHandler keyH, HashMap<String, BufferedImage> txMap) {
        this.pos = pos;
        this.size = gp.getTileSize();
        this.gp = gp;
        this.mouseH = mouseH;
        this.keyH = keyH;
        this.txMap = txMap;
        this.tx = txMap.get("hidden");
    }

    //Accessors
    public boolean isRevealed() {return revealed;}
    public boolean isFlagged() {return flagged;}

    //Mutators
    public void setTx(BufferedImage tx) {this.tx = tx;}

    //Other
    public void draw(Graphics2D g2, Point cameraOffset) {
        Point drawPos = new Point(pos.x * size - cameraOffset.x, pos.y * size - cameraOffset.y);
        if (g2.hitClip(drawPos.x, drawPos.y, size, size)) {
            g2.drawImage(tx, pos.x * size - cameraOffset.x, pos.y * size - cameraOffset.y, size, size, null);
        }
    }

    public void reveal() {reveal(gp.getMineChance());}
    public void reveal(double mineChance) {
        if (!revealed && !flagged) {revealed = true;}
    }

    public void flag() {
        flagged = !flagged;
        if (flagged) {
            setTx(txMap.get("flag"));
        } else {
            setTx(txMap.get("hidden"));
        }
    }
}
