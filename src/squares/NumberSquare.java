package squares;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class NumberSquare extends Square{
    private int number;

    public NumberSquare(Point pos, GamePanel gp, MouseHandler mouseH, KeyHandler keyH, HashMap<String, BufferedImage> txMap) {
        super(pos, gp, mouseH, keyH, txMap);
    }
    //Accessors
    public int getNumber() {return number;}

    //Other
    @Override
    public void reveal(double generateMineChance) {
        if (!this.isRevealed() && !this.isFlagged()) {
            number = 0;
            for (int x = pos.x - 1; x <= pos.x + 1; x++) {
                for (int y = pos.y - 1; y <= pos.y + 1; y++) {
                    Point surroundingSquarePoint = new Point(x, y);
                    if (!gp.squareExists(surroundingSquarePoint)) {
                        if (gp.generateSquare(surroundingSquarePoint, generateMineChance)) {
                            number++;
                        }
                    } else if (gp.getSquare(surroundingSquarePoint) instanceof BombSquare) {
                        number++;
                    }
                }
            }
            this.setTx(txMap.get(Integer.toString(number)));
            super.reveal(generateMineChance);

            if (number == 0) {
                for (int x = pos.x - 1; x <= pos.x + 1; x++) {
                    for (int y = pos.y - 1; y <= pos.y + 1; y++) {
                        gp.getSquare(new Point(x, y)).reveal();
                    }
                }
            }
        }
    }
}
