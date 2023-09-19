package smiley.javasweeper.view.components;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.view.DrawUtil;
import smiley.javasweeper.view.GraphicManager;

public class Button extends GenericComponent {
    private BufferedImage image;
    private BufferedImage hoverOverlay;
    private Consumer<Graphics2D> onDraw;
    private Consumer<InputEvent> onClick;
    private int keyBind;
    private double scale;

    public Button(int width, int height) {
        this(() -> GraphicManager.makeFormattedImage(width, height));
    }

    public Button(Supplier<BufferedImage> imageSupplier) {
        this.image = imageSupplier.get();
        this.hoverOverlay = GraphicManager.makeFormattedImage(image.getWidth(), image.getHeight());
        this.onDraw = g2 -> {};
        this.onClick = null;
        this.keyBind = -1;
        this.scale = 1;
        setScale(Settings.getDefault(Settings.Keys.UI_SCALE, Double.class));
    }

    public void setOnDraw(ObjDoubleConsumer<Graphics2D> onDraw) {
        if (onDraw == null) {
            throw new IllegalArgumentException("Consumer \"onDraw\" cannot be null");
        }
        this.onDraw = g2 -> onDraw.accept(g2, scale);
    }

    public void setOnClick(Consumer<InputEvent> onClick) {
        this.onClick = onClick;
    }

    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
    }

    private boolean withinArea(int x, int y, int minX, int minY, int maxX, int maxY) {
        return x > minX && y > minY && x < maxX && y < maxY;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(DrawUtil.getAlteredCopy(image, onDraw), getParentX(), getParentY(), null);
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        try {
            if (me.getButton() == MouseEvent.BUTTON1 && onClick != null && withinArea(
                    me.getX(), me.getY(),
                    getParentX(), getParentY(),
                    getParentX() + image.getWidth(), getParentY() + image.getHeight()
            )) {
                onClick.accept(me);
            }
        } catch (NullPointerException npe) {
            //Do nothing
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        if (keyBind == ke.getKeyCode()) {
            onClick.accept(ke);
        }
    }

    @Override
    public void setScale(double scale) {
        double scaleMultiplier = scale / this.scale;
        this.image = DrawUtil.getScaledImage(image, scaleMultiplier);
        this.scale = scale;
    }
}
