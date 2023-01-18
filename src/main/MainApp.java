package main;

import javax.swing.*;
import java.awt.*;

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
    window.setSize(new Dimension(game.settings.getDisplayWidth(), game.settings.getDisplayHeight()));

    window.setLocationRelativeTo(null);
    window.setUndecorated(false);
    window.add(game.getJPanel());
    window.setVisible(true);

    game.startGameThread();
  }
}
