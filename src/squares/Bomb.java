package squares;

import main.GamePanel;
import main.KeyHandler;

public class Bomb extends Square{
    private GamePanel gp;
    private KeyHandler keyH;

    public Bomb(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
    }
}
