package smiley.javasweeper.view.modals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import smiley.javasweeper.controllers.modal.GenericModalController;
import smiley.javasweeper.filestorage.Settings;
import smiley.javasweeper.intermediary.FileEventListener;
import smiley.javasweeper.intermediary.FileManager;
import smiley.javasweeper.intermediary.events.file.FileEvent;
import smiley.javasweeper.intermediary.events.file.SettingUpdatedEvent;
import smiley.javasweeper.intermediary.events.file.SettingsLoadedEvent;
import smiley.javasweeper.textures.TxLoader;
import smiley.javasweeper.view.Child;
import smiley.javasweeper.view.GraphicManager;
import smiley.javasweeper.view.Parent;
import smiley.javasweeper.view.DrawUtil;
import smiley.javasweeper.view.components.GenericComponent;

/**
 * Represents a menu window with two areas, one upper and lower area, and border
 */
public abstract class GenericModal implements Child, Parent, FileEventListener {
    private BufferedImage borderImage;
    private BufferedImage upperImageBase;
    private BufferedImage lowerImageBase;
    private int imageOffsetLeft;
    private int upperImageOffsetTop;
    private int lowerImageOffsetTop;
    private int innerWidth;
    private int upperHeight;
    private int lowerHeight;
    private int width;
    private int height;

    private double scale;
    private Parent parent;

    private GenericModal modal;
    private int modalX;
    private int modalY;

    private List<GenericComponent> components;
    private Map<GenericComponent, Integer> componentsX;
    private Map<GenericComponent, Integer> componentsY;

    /**
     * <b>TxMap should contain the following keys & textures:</b>
     * <ul>
     *   <li>menuTopLeftCorner</li>
     *   <li>menuTopRightCorner</li>
     *   <li>menuMidLeftCorner</li>
     *   <li>menuMidRightCorner</li>
     *   <li>menuBottomLeftCorner</li>
     *   <li>menuBottomRightCorner</li>
     *   <li>menuTopEdge</li>
     *   <li>menuMidEdge</li>
     *   <li>menuBottomEdge</li>
     *   <li>menuTopLeftEdge</li>
     *   <li>menuTopRightEdge</li>
     *   <li>menuBottomLeftEdge</li>
     *   <li>menuBottomRightEdge</li>
     * </ul>
     * The following textures should have the same height:
     * <ul>
     *   <li>menuTopLeftCorner, menuTopEdge, menuTopRightCorner</li>
     *   <li>menuMidLeftCorner, menuMidEdge, menuMidRightCorner</li>
     *   <li>menuBottomLeftCorner, menuBottomEdge, menuBottomRightCorner</li>
     * </ul>
     * The following textures should have the same width:
     * <ul>
     *   <li>
     *     menuTopLeftCorner, menuTopLeftEdge, menuMidLeftCorner,
     *     menuBottomLeftEdge, menuBottomLeftCorner
     *   </li><li>
     *     menuTopRightCorner, menuTopRightEdge, menuMidRightCorner,
     *     menuBottomRightEdge, menuBottomRightCorner
     *   </li>
     * </ul>
     * The following textures should have a width of 1 pixel:
     * <ul><li>menuTopEdge, menuMidEdge, menuBottomEdge</li></ul>
     * The following textures should have a height of 1 pixel:
     * <ul><li>
     *   menuTopLeftEdge, menuTopRightEdge,
     *   menuBottomLeftEdge, menuBottomRightEdge
     * </li></ul>
     *
     * @param innerWidth  The <b>inner</b> width of the window. Must be 0 or greater
     * @param upperHeight The <b>inner</b> height of the top window. Must be 0 or greater
     * @param lowerHeight The <b>inner</b> height of the bottom window. Must be 0 or greater
     * @see TxLoader
     */
    protected GenericModal(int innerWidth, int upperHeight, int lowerHeight) {
        if (innerWidth < 0) {
            throw new IllegalArgumentException(
                    "MenuWindow: Parameter \"width\" must be 0 or greater (received \"" + innerWidth + "\")"
            );
        } else if (upperHeight < 0) {
            throw new IllegalArgumentException(
                    "MenuWindow: Parameter \"upperHeight\" must be 0 or greater (received \"" + upperHeight + "\")"
            );
        } else if (lowerHeight < 0) {
            throw new IllegalArgumentException(
                    "MenuWindow: Parameter \"lowerHeight\" must be 0 or greater (received \"" + lowerHeight + "\")"
            );
        }
        this.innerWidth = innerWidth;
        this.upperHeight = upperHeight;
        this.lowerHeight = lowerHeight;

        FileManager.getInstance().addListener(this);

        //Getting every frame piece, scaled
        BufferedImage topLeftCorner = Textures.TOP_LEFT_CORNER.get(1);
        BufferedImage topRightCorner = Textures.TOP_RIGHT_CORNER.get(1);
        BufferedImage midLeftCorner = Textures.MID_LEFT_CORNER.get(1);
        BufferedImage midRightCorner = Textures.MID_RIGHT_CORNER.get(1);
        BufferedImage bottomLeftCorner = Textures.BOTTOM_LEFT_CORNER.get(1);
        BufferedImage bottomRightCorner = Textures.BOTTOM_RIGHT_CORNER.get(1);
        BufferedImage topEdge = Textures.TOP_EDGE.get(1);
        BufferedImage midEdge = Textures.MID_EDGE.get(1);
        BufferedImage bottomEdge = Textures.BOTTOM_EDGE.get(1);
        BufferedImage topLeftEdge = Textures.TOP_LEFT_EDGE.get(1);
        BufferedImage topRightEdge = Textures.TOP_RIGHT_EDGE.get(1);
        BufferedImage bottomLeftEdge = Textures.BOTTOM_LEFT_EDGE.get(1);
        BufferedImage bottomRightEdge = Textures.BOTTOM_RIGHT_EDGE.get(1);

        this.width = topLeftCorner.getWidth()
                + innerWidth
                + topRightCorner.getWidth();
        this.height = topLeftCorner.getHeight()
                + upperHeight
                + midLeftCorner.getHeight()
                + lowerHeight
                + bottomLeftCorner.getHeight();
        this.imageOffsetLeft = topLeftCorner.getWidth();
        this.upperImageOffsetTop = topLeftCorner.getHeight();
        this.lowerImageOffsetTop = topLeftCorner.getHeight() + upperHeight + midLeftCorner.getHeight();

        this.borderImage = GraphicManager.makeFormattedImage(width, height);
        this.upperImageBase = GraphicManager.makeFormattedImage(innerWidth, upperHeight);
        this.lowerImageBase = GraphicManager.makeFormattedImage(innerWidth, lowerHeight);

        //Drawing upper & lower image base
        Graphics2D upperG2 = upperImageBase.createGraphics();
        upperG2.setPaint(new Color(189, 189, 189));
        upperG2.fillRect(0, 0, innerWidth, upperHeight);
        upperG2.dispose();

        Graphics2D lowerG2 = lowerImageBase.createGraphics();
        lowerG2.setPaint(new Color(189, 189, 189));
        lowerG2.fillRect(0, 0, innerWidth, lowerHeight);
        lowerG2.dispose();

        //Drawing the border's corners
        Graphics2D borderG2 = borderImage.createGraphics();

        borderG2.drawImage(topLeftCorner, 0, 0, null);
        borderG2.drawImage(topRightCorner, topLeftCorner.getWidth() + innerWidth, 0, null);
        borderG2.drawImage(midLeftCorner, 0, topLeftCorner.getHeight() + upperHeight, null);
        borderG2.drawImage(midRightCorner, midLeftCorner.getWidth() + innerWidth,
                topRightCorner.getHeight() + upperHeight, null);
        borderG2.drawImage(bottomLeftCorner, 0,
                topLeftCorner.getHeight() + upperHeight + midLeftCorner.getHeight() + lowerHeight, null);
        borderG2.drawImage(bottomRightCorner, bottomLeftCorner.getWidth() + innerWidth,
                topRightCorner.getHeight() + upperHeight + midRightCorner.getHeight() + lowerHeight, null);

        //Drawing the border's  edges
        for (int i = 0; i < innerWidth; i++) {
            borderG2.drawImage(topEdge, topLeftCorner.getWidth() + i, 0, null);
            borderG2.drawImage(midEdge, midLeftCorner.getWidth() + i,
                    topLeftCorner.getHeight() + upperHeight, null);
            borderG2.drawImage(bottomEdge, bottomLeftCorner.getWidth() + i,
                    topLeftCorner.getHeight() + upperHeight + midLeftCorner.getHeight() + lowerHeight, null);
        }
        for (int i = 0; i < upperHeight; i++) {
            borderG2.drawImage(topLeftEdge, 0, topLeftCorner.getHeight() + i, null);
            borderG2.drawImage(topRightEdge, topLeftCorner.getWidth() + innerWidth,
                    topRightCorner.getHeight() + i, null);
        }
        for (int i = 0; i < lowerHeight; i++) {
            borderG2.drawImage(bottomLeftEdge, 0,
                    topLeftCorner.getHeight() + upperHeight + midLeftCorner.getHeight() + i, null);
            borderG2.drawImage(bottomRightEdge, topLeftCorner.getWidth() + innerWidth,
                    topRightCorner.getHeight() + upperHeight + midRightCorner.getHeight() + i, null);
        }

        borderG2.dispose();
        this.parent = null;

        this.components = new ArrayList<>();
        this.componentsX = new HashMap<>();
        this.componentsY = new HashMap<>();

        this.scale = 1;
        setScale(Settings.getDefault(Settings.Keys.UI_SCALE, Double.class));
    }

    @Override
    public GenericModalController getController() {
        return null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getInnerWidth() {
        return innerWidth;
    }

    public int getUpperHeight() {
        return upperHeight;
    }

    public int getLowerHeight() {
        return lowerHeight;
    }

    protected abstract void draw(Graphics2D upperG2, Graphics2D lowerG2);

    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(borderImage, getParentX(), getParentY(), null);
        BufferedImage upperImage = GraphicManager.getFormattedImage(upperImageBase);
        BufferedImage lowerImage = GraphicManager.getFormattedImage(lowerImageBase);
        Graphics2D upperG2 = upperImage.createGraphics();
        Graphics2D lowerG2 = lowerImage.createGraphics();
        upperG2.setColor(Color.BLACK);
        lowerG2.setColor(Color.BLACK);
        draw(upperG2, lowerG2);
        upperG2.dispose();
        lowerG2.dispose();
        g2.drawImage(upperImage,
                getParentX() + imageOffsetLeft,
                getParentY() + upperImageOffsetTop,
                null
        );
        g2.drawImage(lowerImage,
                getParentX() + imageOffsetLeft,
                getParentY() + lowerImageOffsetTop,
                null
        );
    }

    @Override
    public Parent getParent() {
        return parent;
    }

    @Override
    public void setParent(Parent parent) {
        this.parent = parent;
    }

    @Override
    public int getParentX() {
        return getParent().getModalX();
    }

    @Override
    public int getParentY() {
        return getParent().getModalY();
    }

    @Override
    public GenericModal getModal() {
        return modal;
    }

    @Override
    public int getModalX() {
        return getParentX() + modalX;
    }

    @Override
    public int getModalY() {
        return getParentY() + modalY;
    }

    @Override
    public void placeModal(GenericModal modal, int x, int y) {
        if (modal.getParent() != null) {
            throw new IllegalArgumentException("modal already has a parent");
        }
        this.modal = modal;
        this.modalX = x;
        this.modalY = y;
        modal.setParent(this);
    }

    @Override
    public void closeModal() {
        modal.setParent(null);
        this.modal = null;
        this.modalX = 0;
        this.modalY = 0;
    }

    @Override
    public List<GenericComponent> getComponents() {
        return components;
    }

    @Override
    public int getComponentX(GenericComponent component) {
        return getParentX() + componentsX.get(component);
    }

    @Override
    public int getComponentY(GenericComponent component) {
        return getParentY() + componentsY.get(component);
    }

    @Override
    public void placeComponent(GenericComponent component, int x, int y) {
        if (component.getParent() != null) {
            throw new IllegalArgumentException("component already has a parent");
        }
        components.add(component);
        componentsX.put(component, x);
        componentsY.put(component, y);
        component.setParent(this);
    }

    protected void setScale(double scale) {
        double scaleMultiplier = scale / this.scale;
        this.borderImage = DrawUtil.getScaledImage(borderImage, scaleMultiplier);
        this.upperImageBase = DrawUtil.getScaledImage(upperImageBase, scaleMultiplier);
        this.lowerImageBase = DrawUtil.getScaledImage(lowerImageBase, scaleMultiplier);
        this.imageOffsetLeft *= scaleMultiplier;
        this.upperImageOffsetTop *= scaleMultiplier;
        this.lowerImageOffsetTop *= scaleMultiplier;
        this.innerWidth *= scaleMultiplier;
        this.upperHeight *= scaleMultiplier;
        this.lowerHeight *= scaleMultiplier;
        this.width *= scaleMultiplier;
        this.height *= scaleMultiplier;

        this.modalX *= scaleMultiplier;
        this.modalY *= scaleMultiplier;
        componentsX.replaceAll((c, x) -> (int) (x * scaleMultiplier));
        componentsY.replaceAll((c, y) -> (int) (y * scaleMultiplier));

        this.scale = scale;
    }

    @Override
    public void onEvent(FileEvent fe) {
        if (fe instanceof SettingsLoadedEvent sle) {
            setScale(sle.settings().getUiScale());
        } else if (fe instanceof SettingUpdatedEvent sue && Settings.Keys.UI_SCALE.equals(sue.setting())) {
            setScale(sue.value(Double.class));
        }
    }

    private enum Textures {
        TOP_LEFT_CORNER("menuTopLeftCorner"),
        TOP_RIGHT_CORNER("menuTopRightCorner"),
        MID_LEFT_CORNER("menuMidLeftCorner"),
        MID_RIGHT_CORNER("menuMidRightCorner"),
        BOTTOM_LEFT_CORNER("menuBottomLeftCorner"),
        BOTTOM_RIGHT_CORNER("menuBottomRightCorner"),
        TOP_EDGE("menuTopEdge"),
        MID_EDGE("menuMidEdge"),
        BOTTOM_EDGE("menuBottomEdge"),
        TOP_LEFT_EDGE("menuTopLeftEdge"),
        TOP_RIGHT_EDGE("menuTopRightEdge"),
        BOTTOM_LEFT_EDGE("menuBottomLeftEdge"),
        BOTTOM_RIGHT_EDGE("menuBottomRightEdge");

        private static final String FRAME_PIECES_PATH = "framepieces";
        private static final String FRAME_PIECES_EXTENSION = ".bmp";

        private final String framePieceName;

        Textures(String framePieceName) {
            this.framePieceName = framePieceName;
        }

        public BufferedImage get(double scale) {
            return TxLoader.getRelScaled(scale,
                    String.format("%s/%s%s", FRAME_PIECES_PATH, framePieceName, FRAME_PIECES_EXTENSION)
            );
        }
    }
}
