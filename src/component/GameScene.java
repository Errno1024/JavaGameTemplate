package component;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.util.ArrayDeque;
import javax.swing.Timer;

import core.MainPanel;
import settings.Variables;
import utility.GameUtils;
import utility.transformer.PointTransformer;

public class GameScene extends GameObject implements ActionListener, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private Vector<GameComponent> components = new Vector<>();
    private ArrayDeque<Integer> emptySlots = new ArrayDeque<>();

    private Vector<GameComponent> sortedComponents = new Vector<>();

    protected BufferedImage image;
    private Color backgroundColor;
    private BufferedImage baseImage;
    private BufferedImage backgroundImage;

    protected MainPanel panel;
    protected Timer timer = new Timer(1, this);
    public int defaultTransitionFrameCount = Variables.defaultTransitionFrameCount;

    private BufferedImage transitionImage = null;
    private int frameCount = 0;
    private int transitionCount = 0;

    protected boolean waitUntilStable = false;
    private boolean imageFrozen = false;

    private Point viewpoint = new Point(0, 0);
    private Point viewpointDest = null;
    private PointTransformer viewpointTransformer = null;

    public boolean isViewpointTransforming() { return viewpointTransformer != null; }

    public Point getViewpoint() {
        if (viewpointTransformer != null) {
            return viewpointTransformer.getValue();
        }
        return new Point(viewpoint);
    }

    public void setViewpoint(Point viewpoint) {
        this.viewpoint = new Point(viewpoint);
        viewpointDest = null;
        viewpointTransformer = null;
    }

    private void viewpointStabilize() {
        if (viewpointTransformer != null) {
            viewpoint = viewpointTransformer.getValue();
            viewpointTransformer = null;
            viewpointDest = null;
        }
    }

    public void viewpointMove(Point dest, long total, double initSmooth, double destSmooth) {
        viewpointStabilize();
        viewpointDest = dest;
        viewpointTransformer = new PointTransformer(viewpoint, dest, total, initSmooth, destSmooth);
    }

    public void viewpointMove(Point dest, long total) {
        viewpointStabilize();
        viewpointDest = dest;
        viewpointTransformer = new PointTransformer(viewpoint, dest, total);
    }

    public void viewpointTranslate(Point dest, long total, double initSmooth, double destSmooth) {
        dest = new Point(dest);
        dest.translate(viewpoint.x, viewpoint.y);
        this.viewpointMove(dest, total, initSmooth, destSmooth);
    }

    public void viewpointTranslate(Point dest, long total) {
        dest = new Point(dest);
        dest.translate(viewpoint.x, viewpoint.y);
        this.viewpointMove(dest, total);
    }

    public GameScene(MainPanel panel) {
        super(panel.getSize());
        setPanel(panel);
    }

    public GameScene() {
        super();
    }

    protected void initialize() {
    }

    public Image getBaseImage() {
        return baseImage;
    }

    public void setBaseImage(Image image) {
        if (disposed) return;
        baseImage = GameUtils.cloneImage(image);
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Image image) {
        if (disposed) return;
        if (image instanceof BufferedImage) {
            backgroundImage = (BufferedImage) image;
        } else {
            backgroundImage = GameUtils.cloneImage(image);
        }
    }

    public void setSize(Dimension size) {
        this.size = size.getSize();
    }

    public void setSize(int width, int height) {
        this.setSize(new Dimension(width, height));
    }

    public void setPanel(MainPanel panel) {
        this.panel = panel;
        this.size = panel.getSize();
        this.backgroundColor = panel.getBackground();
    }

    private boolean initialized = false;
    private void _initialize() {
        if (!initialized) {
            initialized = true;
            initialize();
        }
    }

    public void sceneChange(GameScene scene) {
        if (disposed) return;
        panel.sceneChange(scene);
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color c) {
        backgroundColor = c;
    }

    protected void action() {}

    public void refresh() {
        if (disposed) return;
        _initialize();
        if (viewpointTransformer != null) {
            viewpointTransformer.refresh();
            if (viewpointTransformer.isEnd()) {
                viewpoint = viewpointDest;
                viewpointDest = null;
                viewpointTransformer = null;
            }
        }
        for (int i = 0; i < sortedComponents.size(); ++i) {
            sortedComponents.elementAt(i).refresh();
        }
        action();
        generateImage();

        if (frameCount != 0) {
            if (frameCount == transitionCount++) {
                frameCount = 0;
                transitionCount = 0;
                transitionImage = null;
            }
        }
    }

    public boolean isStable() {
        if (frameCount != 0) return false;
        return !disposed;
    }

    public void setTransition(Image image, int frameCount) {
        if (image == null) return;
        this.frameCount = frameCount;
        this.transitionCount = 0;
        this.transitionImage = GameUtils.cloneImage(image);
    }

    public void setTransition(Image image) {
        setTransition(image, defaultTransitionFrameCount);
    }

    public void imageFreeze() {
        if (isStable()) {
            imageFrozen = true;
        }
    }

    public void imageUnfreeze() {
        imageFrozen = false;
    }

    public boolean isImageFrozen() {
        return imageFrozen;
    }

    protected void generateImage() {
        if (disposed) return;
        if (imageFrozen && image != null) return;
        image = GameUtils.emptyImage(this.size, backgroundColor);
        Graphics g = image.getGraphics();
        if (baseImage != null) {
            g.drawImage(baseImage, 0, 0, null);
        }
        g.dispose();
        generateBackGround();
        for (int i = 0; i < sortedComponents.size(); ++i) {
            componentPaintImage(sortedComponents.elementAt(i), image);
        }
    }

    protected void componentPaintImage(GameComponent component, BufferedImage image) {
        Graphics2D g = GameUtils.imageGetGraphics(image);
        if (!component.isAbsoluteLocation()) {
            Point viewpoint = getViewpoint();
            g.translate(-viewpoint.x, -viewpoint.y);
        }
        component.paintImage(g);
    }

    protected void generateBackGround() {
        if (disposed) return;
        if (backgroundImage != null) {
            Point viewpoint = getViewpoint();
            int dx1 = 0, dy1 = 0, dx2 = panel.getWidth(), dy2 = panel.getHeight();
            int sx1 = viewpoint.x, sy1 = viewpoint.y;
            int sx2 = sx1 + dx2, sy2 = sy1 + dy2;
            int bw = backgroundImage.getWidth(), bh = backgroundImage.getHeight();
            if (sx1 < 0) {
                dx1 = -sx1;
                sx1 = 0;
            }
            if (sy1 < 0) {
                dy1 = -sy1;
                sy1 = 0;
            }
            if (sx2 >= bw) {
                dx2 -= sx2 - bw;
                sx2 = bw;
            }
            if (sy2 >= bh) {
                dy2 -= sy2 - bh;
                sy2 = bh;
            }
            Graphics g = image.getGraphics();
            g.drawImage(backgroundImage.getSubimage(sx1, sy1, sx2 - sx1, sy2 - sy1), dx1, dy1, dx2, dy2, 0, 0, sx2 - sx1, sy2 - sy1, null);
            g.dispose();
        }
    }

    public Image getImage() {
        if (disposed) return null;
        if (image == null) {
            generateImage();
        }
        if (frameCount == 0)
            return image;
        else {
            BufferedImage img = GameUtils.cloneImage(image);
            Graphics2D g = (Graphics2D) img.getGraphics();
            AlphaComposite com = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - (float) transitionCount / frameCount);
            g.setComposite(com);
            g.drawImage(transitionImage, 0, 0, null);
            g.dispose();
            return img;
        }
    }

    public int add(GameComponent sp) {
        if (disposed) return 0;
        if (emptySlots.isEmpty()) {
            sp.serial = components.size();
            components.add(sp);
        } else {
            sp.serial = emptySlots.poll();
            components.set(sp.serial, sp);
        }
        sp.setParent(this);
        componentsAdd(sp);
        return sp.serial;
    }

    public void remove(Integer serial) {
        if (disposed) return;
        if (serial >= components.size()) return;
        componentsRemoveSerial(serial);
        GameComponent sp = components.set(serial, null);
        if (sp != null) {
            sp.dispose();
            if (serial < components.size() - 1) emptySlots.add(serial);
        }
    }

    public void remove(GameComponent com) {
        remove(com.serial);
    }

    private void componentsRemoveSerial(Integer serial) {
        for (int i = 0; i < sortedComponents.size(); ++i) {
            if (serial.equals(sortedComponents.elementAt(i).serial)) {
                sortedComponents.removeElementAt(i);
                return;
            }
        }
    }

    private void componentsAdd(GameComponent source) {
        boolean bInserted = false;
        for (int i = 0; i < sortedComponents.size(); ++i) {
            if (sortedComponents.elementAt(i).getDepth() > source.getDepth()) {
                sortedComponents.insertElementAt((GameComponent) source, i);
                bInserted = true;
                break;
            }
        }
        if (!bInserted) {
            sortedComponents.add((GameComponent) source);
        }
    }

    @Override
    protected void onChildDepthChange(GameObject source) {
        if (disposed) return;
        if (source instanceof GameComponent) {
            componentsAdd((GameComponent) source);
        }
    }

    @Override
    protected void onChildDispose(GameObject source) {
        if (disposed) return;
        if (source instanceof GameComponent) {
            remove((GameComponent) source);
        }
    }

    public void dispose() {
        if (disposed) return;
        timer.stop();
        for (int i = 0; i < components.size(); ++i) {
            GameComponent com = components.elementAt(i);
            if (com != null)
                com.dispose();
        }
        components.clear();
        emptySlots.clear();
        baseImage = null;
        disposed = true;
    }

    protected void timerRefresh() {}

    public void actionPerformed(ActionEvent e) {
        if (disposed) return;
        if (e.getSource() == timer) timerRefresh();
    }

    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
    public void mouseWheelMoved(MouseWheelEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}
