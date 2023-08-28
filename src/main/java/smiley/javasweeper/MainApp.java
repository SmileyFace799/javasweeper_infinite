package smiley.javasweeper;

import javax.swing.*;
import java.awt.*;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.view.screens.GamePanel;

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
    window.setSize(new Dimension(Settings.getDisplayWidth(), Settings.getDisplayHeight()));

    window.setLocationRelativeTo(null);
    window.setUndecorated(false);
    window.getContentPane().add(game.getJPanel());
    window.setVisible(true);

    game.startGameThread();
  }
}
