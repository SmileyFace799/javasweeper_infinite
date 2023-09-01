package smiley.javasweeper;

import javax.swing.*;
import java.awt.*;
import smiley.javasweeper.view.GamePanel;
import smiley.javasweeper.view.GraphicManager;

public class MainApp {
  public static void main(String[] args) {
    JFrame window = new JFrame();
    window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    window.setResizable(false);
    window.setTitle("Javasweeper Infinite");

    GamePanel game = new GamePanel(window);

    window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    window.setResizable(false);
    window.setTitle("Javasweeper Infinite");
    window.setSize(new Dimension(GraphicManager.DEFAULT_WINDOW_WIDTH, GraphicManager.DEFAULT_WINDOW_HEIGHT));

    window.setLocationRelativeTo(null);
    window.setUndecorated(false);
    window.getContentPane().add(game.getJPanel());
    window.setVisible(true);

    game.startGameThread();
  }
}
