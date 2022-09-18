package main;

import javax.swing.JFrame;

public class MainApp {
  public static void main(String[] args) {
    JFrame window = new JFrame();
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setResizable(false);
    window.setTitle("Javasweeper Infinite");

    GamePanel game = new GamePanel(window);
    window.add(game);

    window.setLocationRelativeTo(null);
    window.setVisible(true);

    game.startGameThread();
  }
}
