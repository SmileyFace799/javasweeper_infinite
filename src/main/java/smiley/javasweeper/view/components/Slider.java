package smiley.javasweeper.view.components;

import java.awt.*;

public class Slider {
  private double value;
  final Rectangle boundingRect;
  final double scale;

  public Slider(int x, int y, int width, int height, double defaultValue, double scale) {
    this.value = defaultValue;
    this.boundingRect = new Rectangle(x, y, width, height);
    this.scale = scale;
  }

  public double getValue() {
    return value;
  }

  /**
   * Sets the value of the slider
   *
   * @param value The new value
   *              <br/>Must be a value that satisfies the following:
   *              <ul><li>0 <= value <= 1</li></ul>
   */
  public void setValue(double value) {
    if (value < 0 || value > 1) {
      System.out.println("Slider.setValue: Value of of range (Got \"" + value + "\")");
    } else {
      this.value = value;
    }
  }

  public void draw(Graphics2D g2) {
    g2.setStroke(new BasicStroke((int) Math.round(2 * scale)));
    g2.drawLine(
        boundingRect.x, boundingRect.y + boundingRect.height / 2,
        boundingRect.x + boundingRect.width, boundingRect.y + boundingRect.height / 2
    );
    int markerWidth = (int) Math.round(4 * scale);
    int markerX = (int) Math.round((boundingRect.width) * value) + boundingRect.x;
    g2.setStroke(new BasicStroke(markerWidth));
    g2.drawLine(
        markerX, boundingRect.y, markerX, boundingRect.y + boundingRect.height
    );
  }
}
