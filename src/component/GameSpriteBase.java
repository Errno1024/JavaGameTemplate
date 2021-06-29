package component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import utility.GameUtils;

public abstract class GameSpriteBase extends GameComponent {
    BufferedImage image;
    BufferedImage sourceImage;
    BufferedImage outputImage;

    public boolean visible = true;
    private double rotation = 0;
    private boolean mirror = false;
    private int opacity = 255;
    private int direction = 5;
    private double scaleX = 1.0, scaleY = 1.0;

    private Color coverColor;

    private int flashCount = 0;
    private int flashTime = 0;
    private BufferedImage flashImage = null;
    private Color flashColor;

    public GameSpriteBase() {
        sourceImage = GameUtils.emptyImage;
    }

    public GameSpriteBase(String path) {
        super();
        loadImage(path);
        initialize();
    }

    public GameSpriteBase(BufferedImage image) {
        super();
        setImage(image);
        initialize();
    }

    protected void initialize() {
    }

    Point rotatePoint(Point origin, Point source, double rotation) {
        int disX = source.x - origin.x, disY = source.y - origin.y;
        Point res = new Point();
        res.x = (int) Math.round(disX * Math.cos(rotation) - disY * Math.sin(rotation)) + origin.x;
        res.y = (int) Math.round(disY * Math.cos(rotation) + disX * Math.sin(rotation)) + origin.y;
        return res;
    }

    protected Point[] getCornerPoints() {
        Point p1 = new Point(), p2 = new Point(), p3 = new Point(), p4 = new Point();
        switch (direction) {
            case 1:
                p1.x = this.location.x;
                p1.y = this.location.y - this.size.height;
                break;
            case 2:
                p1.x = this.location.x - this.size.width / 2;
                p1.y = this.location.y - this.size.height;
                break;
            case 3:
                p1.x = this.location.x - this.size.width;
                p1.y = this.location.y - this.size.height;
                break;
            case 4:
                p1.x = this.location.x;
                p1.y = this.location.y - this.size.height / 2;
                break;
            case 6:
                p1.x = this.location.x - this.size.width;
                p1.y = this.location.y - this.size.height / 2;
                break;
            case 7:
                p1.x = this.location.x;
                p1.y = this.location.y;
                break;
            case 8:
                p1.x = this.location.x - this.size.width / 2;
                p1.y = this.location.y;
                break;
            case 9:
                p1.x = this.location.x - this.size.width;
                p1.y = this.location.y;
                break;
            case 5:
            default:
                p1.x = this.location.x - this.size.width / 2;
                p1.y = this.location.y - this.size.height / 2;
                break;
        }
        p2.x = p1.x + this.size.width;
        p3.x = p1.x + this.size.width;
        p4.x = p1.x;
        p2.y = p1.y;
        p3.y = p1.y + this.size.height;
        p4.y = p1.y + this.size.height;
        return new Point[]{p1, p2, p3, p4};
    }

    public int getRealX() {
        Point[] p = getCornerPoints();
        int[] x = new int[4];
        x[0] = rotatePoint(location, p[0], rotation).x;
        x[1] = rotatePoint(location, p[1], rotation).x;
        x[2] = rotatePoint(location, p[2], rotation).x;
        x[3] = rotatePoint(location, p[3], rotation).x;
        Arrays.sort(x);
        return x[0];
    }

    public int getRealY() {
        Point[] p = getCornerPoints();
        int[] y = new int[4];
        y[0] = rotatePoint(location, p[0], rotation).y;
        y[1] = rotatePoint(location, p[1], rotation).y;
        y[2] = rotatePoint(location, p[2], rotation).y;
        y[3] = rotatePoint(location, p[3], rotation).y;
        Arrays.sort(y);
        return y[0];
    }

    public int getRealWidth() {
        Point[] p = getCornerPoints();
        int[] x = new int[4];
        x[0] = rotatePoint(location, p[0], rotation).x;
        x[1] = rotatePoint(location, p[1], rotation).x;
        x[2] = rotatePoint(location, p[2], rotation).x;
        x[3] = rotatePoint(location, p[3], rotation).x;
        Arrays.sort(x);
        return x[3] - x[0];
    }

    public int getRealHeight() {
        Point[] p = getCornerPoints();
        int[] y = new int[4];
        y[0] = rotatePoint(location, p[0], rotation).y;
        y[1] = rotatePoint(location, p[1], rotation).y;
        y[2] = rotatePoint(location, p[2], rotation).y;
        y[3] = rotatePoint(location, p[3], rotation).y;
        Arrays.sort(y);
        return y[3] - y[0];
    }

    public Rectangle getRealArea() {
        Point[] p = getCornerPoints();
        int[] x = new int[4];
        x[0] = rotatePoint(location, p[0], rotation).x;
        x[1] = rotatePoint(location, p[1], rotation).x;
        x[2] = rotatePoint(location, p[2], rotation).x;
        x[3] = rotatePoint(location, p[3], rotation).x;
        Arrays.sort(x);
        int[] y = new int[4];
        y[0] = rotatePoint(location, p[0], rotation).y;
        y[1] = rotatePoint(location, p[1], rotation).y;
        y[2] = rotatePoint(location, p[2], rotation).y;
        y[3] = rotatePoint(location, p[3], rotation).y;
        Arrays.sort(y);
        return new Rectangle(x[0], y[0], x[3] - x[0], y[3] - y[0]);
    }

    public Color getCover() {
        return coverColor;
    }

    public void setCover(Color cover) {
        coverColor = cover;
    }

    public void setImage(BufferedImage image) {
        if (disposed) return;
        this.sourceImage = image;
        imageResize();
    }

    public void loadImage(String path) {
        if (disposed) return;
        this.sourceImage = GameUtils.getCacheImage(path);
        imageResize();
    }

    void imageResize() {
        if (disposed) return;
        if (sourceImage == null) {
            this.image = GameUtils.emptyImage();
            this.size = new Dimension(this.image.getWidth(), this.image.getHeight());
            return;
        }
        int imageWidth = (int) Math.round(this.sourceImage.getWidth() * scaleX);
        int imageHeight = (int) Math.round(this.sourceImage.getHeight() * scaleY);
        if (imageWidth == 0 || imageHeight == 0) {
            this.image = GameUtils.emptyImage();
        } else {
            this.image = GameUtils.emptyImage(imageWidth, imageHeight);
            Graphics g = this.image.getGraphics();
            g.drawImage(this.sourceImage, 0, 0, this.image.getWidth(), this.image.getHeight(), 0, 0, this.sourceImage.getWidth(), this.sourceImage.getHeight(), null);
            g.dispose();
        }
        this.size = new Dimension(this.image.getWidth(), this.image.getHeight());
        if (isFlashing()) {
            flashImage = GameUtils.emptyImage(image.getWidth(), image.getHeight(), flashColor);
        }
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public void setScaleX(double scaleX) {
        if (scaleX < 0) {
            this.mirror = !this.mirror;
            scaleX = -scaleX;
        }
        this.scaleX = scaleX;
        imageResize();
    }

    public void setScaleY(double scaleY) {
        if (scaleY < 0) {
            this.mirror = !this.mirror;
            this.rotate(Math.PI);
            scaleY = -scaleY;
        }
        this.scaleY = scaleY;
        imageResize();
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity(int alpha) {
        opacity = alpha;
        if (opacity < 0) opacity = 0;
        if (opacity > 255) opacity = 255;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
        while (this.rotation > 2 * Math.PI) {
            this.rotation -= 2 * Math.PI;
        }
        while (this.rotation < 0.0) {
            this.rotation += 2 * Math.PI;
        }
    }

    public void rotate(double rotation) {
        this.setRotation(this.rotation + rotation);
    }

    public boolean isMirror() {
        return mirror;
    }

    public void setMirror(boolean mirror) {
        this.mirror = mirror;
    }

    public BufferedImage getImage() {
        if (disposed) return null;
        generateImage();
        if (isTransiting()) {
            BufferedImage img = GameUtils.cloneImage(outputImage);
            Graphics2D g = (Graphics2D) img.getGraphics();
            AlphaComposite com = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - (float) transitionCount / transitionTotal);
            g.setComposite(com);
            g.drawImage(transitionImage, 0, 0, null);
            g.dispose();
            return img;
        } else {
            return outputImage;
        }
    }

    private int transitionTotal = 0;
    private int transitionCount;
    private BufferedImage transitionImage;

    public void transition(Image source, int time) {
       if (time < 0) return;
       transitionTotal = time;
       transitionCount = 0;
       transitionImage = GameUtils.cloneImage(source);
    }

    public void paintImage(Graphics2D g) {
        if (disposed) return;
        if (!visible || opacity <= 0) return;
        generateImage();
        Composite com = g.getComposite();
        AlphaComposite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity / 255);
        g.setComposite(comp);
        Point[] p = getCornerPoints();
        g.rotate(rotation, (double) location.x, (double) location.y);
        if (mirror) {
            BufferedImage img = GameUtils.emptyImage(outputImage.getWidth(), outputImage.getHeight());
            Graphics2D gr = (Graphics2D) img.getGraphics();
            gr.drawImage(outputImage, 0, 0, outputImage.getWidth(), outputImage.getHeight(), outputImage.getWidth(), 0, 0, outputImage.getHeight(), null);
            gr.dispose();
            g.drawImage(img, p[0].x, p[0].y, null);
        } else g.drawImage(outputImage, p[0].x, p[0].y, null);
        g.setComposite(com);
    }

    public void flash(int frameCount, Color color) {
        if (!isStable()) return;
        forceFlash(frameCount, color);
    }

    protected void forceFlash(int frameCount, Color color) {
        if (disposed) return;
        flashCount = frameCount;
        flashTime = 0;
        flashColor = color;
        if (image == null) generateImage();
        flashImage = GameUtils.emptyImage(image.getWidth(), image.getHeight(), flashColor);
    }

    public boolean isStable() {
        if (isFlashing()) return false;
        if (isTransiting()) return false;
        return !disposed;
    }

    public boolean isFlashing() {
        return flashCount != 0;
    }

    void generateImage() {
        if (disposed) return;
        outputImage = GameUtils.cloneImage(image);
        if (coverColor != null) {
            Graphics2D g = (Graphics2D) outputImage.getGraphics();
            AlphaComposite comp = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f - (float) flashTime / flashCount);
            g.setComposite(comp);
            g.drawImage(GameUtils.emptyImage(image.getWidth(), image.getHeight(), coverColor), 0, 0, null);
            g.dispose();
        }
        if (flashCount != 0) {
            Graphics2D g = (Graphics2D) outputImage.getGraphics();
            AlphaComposite comp = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f - (float) flashTime / flashCount);
            g.setComposite(comp);
            g.drawImage(flashImage, 0, 0, null);
            g.dispose();
        }
    }

    public boolean isTransiting() {
        return transitionTotal > 0;
    }

    public void refresh() {
        if (disposed) return;
        action();
        if (isFlashing()) {
            if (flashCount == flashTime++) {
                flashCount = 0;
                flashTime = 0;
                flashColor = null;
                flashImage = null;
            }
        }
        if (isTransiting()) {
            if (transitionTotal == transitionCount++) {
                transitionTotal = 0;
                transitionCount = 0;
                transitionImage = null;
            }
        }
    }

    public void dispose() {
        if (disposed) return;
        super.dispose();
        sourceImage = null;
        image = null;
        outputImage = null;
    }

    protected void action() {
    }
}
