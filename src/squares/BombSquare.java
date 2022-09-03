package squares;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class BombSquare extends Square{

    public BombSquare(Point pos, GamePanel gp, MouseHandler mouseH, KeyHandler keyH, HashMap<String, BufferedImage> txMap) {
        super(pos, gp, mouseH, keyH, txMap);
    }

    @Override
    public void reveal(double mineChance) {
        if (!this.isRevealed() && !this.isFlagged()) {
            this.setTx(txMap.get("bombDetonated"));
            super.reveal(mineChance);
        }
    }
}
