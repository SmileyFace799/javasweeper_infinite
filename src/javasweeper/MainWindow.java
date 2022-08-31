package javasweeper;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainWindow {
    private JFrame window;

    public MainWindow() {
        try {
            init();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private void init() throws IOException {
        window = new JFrame();
        window.setLayout(new GridLayout(2, 2, 10, 10));
        window.setTitle("gayming B)");
        window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        window.setSize(800, 500);
        window.setLocationRelativeTo(null);

        for (int i = 1; i <= 4; i++) {
            Image img = ImageIO.read(getClass().getResource("/imgs/img.png"));
            JButton b = new JButton(new ImageIcon(img.getScaledInstance(200, 200, 0)));
            window.add(b);
        }
    }

    public void show() {
        window.setVisible(true);
    }
}
