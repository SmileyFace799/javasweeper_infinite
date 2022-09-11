package squares;

import main.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Square implements JSONEndoce {
    final int x;
    final int y;
    final int size;

    final Board board;
    final TxMap txMap;

    private BufferedImage tx;
    private boolean revealed = false;
    private boolean flagged = false;

    public Square(int x, int y, Board board, TxMap txMap) {
        this.x = x;
        this.y = y;
        this.size = board.getTileSize();
        this.board = board;
        this.txMap = txMap;
        this.tx = txMap.getScaled(size, "hidden");
    }

    //Accessors
    public BufferedImage getTx() {return tx;}
    public boolean isRevealed() {return revealed;}
    public boolean isFlagged() {return flagged;}

    //Mutators
    public void setTx(BufferedImage tx) {this.tx = tx;}

    //Other
    public void draw(Graphics2D g2, Point cameraOffset) {
        int realX = x * size - cameraOffset.x;
        int realY = y * size - cameraOffset.y;
        if (g2.hitClip(realX, realY, size, size)) {
            g2.drawImage(tx, realX, realY, null);
        }
    }

    public void reveal() {if (!revealed && !flagged) {
        revealed = true;
        board.updateImage(x, y, this);
    }}

    public void flag() {
        flagged = !flagged;
        if (flagged) {
            setTx(txMap.getScaled(size, "flag"));
        } else {
            setTx(txMap.getScaled(size, "hidden"));
        }
        board.updateImage(x, y, this);
    }

    @Override
    public HashMap<String, Object> JSONEncode() {
        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("class", getClass());
        return jsonMap;
    }
}
