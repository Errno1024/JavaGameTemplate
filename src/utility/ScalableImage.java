package utility;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

public class ScalableImage extends BufferedImage {
    private Dimension size;
    private BufferedImage source;

    public ScalableImage(Image image, int width, int height) {
        super(1, 1, BufferedImage.TYPE_INT_ARGB);
        source = GameUtils.toBufferedImage(image);
        this.size = new Dimension(Math.max(width, 0), Math.max(height, 0));
    }

    public ScalableImage(Image image) {
        this(image, image.getWidth(null), image.getHeight(null));
    }

    public int getWidth() { return getWidth(null); }
    public int getWidth(ImageObserver observer) {
        return size.width;
    }

    public int getHeight() { return getHeight(null); }
    public int getHeight(ImageObserver observer) {
        return size.height;
    }

    public ImageProducer getSource() { return source.getSource(); }

    public Graphics getGraphics() { return source.getGraphics(); }

    public Image getSourceImage() { return source; }

    public void setSourceImage(Image image) { source = GameUtils.toBufferedImage(image); }

    public void setSize(Dimension size) {
        this.size = new Dimension(size);
    }

    public void setWidth(int width) { this.size.width = width; }

    public void setHeight(int height) { this.size.height = height; }

    public Object getProperty(String name, ImageObserver observer) {
        return source.getProperty(name, observer);
    }

    public BufferedImage getSubimage(int x, int y, int w, int h) {
        int sx = 0;
        if (size.width != w) sx = (source.getWidth() - w) * x / (size.width - w);
        int sy = 0;
        if (size.height != h) sy = (source.getHeight() - h) * y / (size.height - h);
        BufferedImage res = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int sx1 = sx, sy1 = sy, sx2 = sx + w, sy2 = sy + h;
        int dx1 = 0, dy1 = 0, dx2 = w, dy2 = h;
        int bw = source.getWidth(), bh = source.getHeight();
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
        Graphics g = res.getGraphics();
        g.drawImage(source, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
        g.dispose();
        return res;
    }
}
