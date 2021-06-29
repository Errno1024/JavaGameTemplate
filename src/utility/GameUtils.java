package utility;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

public final class GameUtils {
    private GameUtils() {
    }

    public static String defaultFont = "SansSerif";

    private static String[] fontNames;
    private static final double DOUBLE_CRITERION = Math.pow(2, -16);

    public static double distanceReachFirstTime(double distance, double velocity, double acceleration, double invalid) {
        if (doubleEqual(acceleration, 0)) {
            if (doubleEqual(velocity, 0)) {
                if (doubleEqual(distance, 0))
                    return 0;
                return invalid;
            }
            return distance / velocity;
        }
        double delta = velocity * velocity + 2 * acceleration * distance;
        if (delta < 0) return -1;
        double s1 = (Math.sqrt(delta) - velocity) / acceleration, s2 = (-Math.sqrt(delta) - velocity) / acceleration;
        if (s1 < 0)
            if (s2 < 0) return Math.max(s1, s2);
            else return s2;
        else if (s2 < 0) return s1;
        else return Math.min(s1, s2);
    }

    /**
     * To predict move distance according to velocity and acceleration.
     *
     * @param time
     * @param velocity
     * @param acceleration
     * @return
     */
    public static double moveDistance(double time, double velocity, double acceleration) {
        return velocity * time + 0.5 * acceleration * time * time;
    }

    /**
     * To predict final velocity according to initial velocity and acceleration.
     *
     * @param time
     * @param velocity
     * @param acceleration
     * @return
     */
    public static double moveFinalVelocity(double time, double velocity, double acceleration) {
        return velocity + time * acceleration;
    }

    public static int getSection(double value, int width) {
        return (int) Math.floor(value / width);
    }

    public static int getLowerSection(double value, int width) {
        return (int) Math.ceil(value / width) - 1;
    }

    public static boolean doubleEqual(double value1, double value2) {
        return Math.abs(value1 - value2) < DOUBLE_CRITERION;
    }

    /**
     * To smoothly transit a value from a initial value to a destination.
     *
     * @param progress
     * @param init
     * @param dest
     * @param initSmooth
     * @param destSmooth
     * @return
     */
    public static double valueTransition(double progress, double init, double dest, double initSmooth, double destSmooth) {
        if (progress < 0)
            progress = 0;
        if (progress > 1)
            progress = 1;
        if (initSmooth < 0)
            initSmooth = 0;
        if (initSmooth > 1)
            initSmooth = 1;
        if (destSmooth < 0)
            destSmooth = 0;
        if (destSmooth > 1)
            destSmooth = 1;
        if (initSmooth > destSmooth)
            initSmooth = destSmooth = (initSmooth + destSmooth) / 2;
        double dis = progress * (2 + initSmooth - destSmooth);
        double rate;
        if (dis < 2 * initSmooth)
            rate = dis * dis / (4 * initSmooth);
        else if (dis <= initSmooth + destSmooth)
            rate = dis - initSmooth;
        else {
            double backdis = (1 - progress) * (2 + initSmooth - destSmooth);
            rate = 1 - backdis * backdis / (4 - 4 * destSmooth);
        }
        return rate * dest + (1 - rate) * init;
    }

    public static double valueTransition(double progress, double init, double dest) {
        return valueTransition(progress, init, dest, 0.3333333333, 0.6666666667);
    }

    /**
     * To get a font object.
     *
     * @param name
     * @param format
     * @param size
     * @return
     */
    public static Font getFont(String name, int format, int size) {
        if (fontNames == null) {
            fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            Arrays.sort(fontNames);
        }
        if (Arrays.binarySearch(fontNames, name) < 0) {
            Font f = new Font(defaultFont, format, size);
            return f;
        } else {
            Font f = new Font(name, format, size);
            return f;
        }
    }

    /**
     * To set a Graphics object with specified font.
     *
     * @param g
     * @param font
     */
    public static void graphicsSetFont(Graphics g, Font font) {
        g.setFont(font);
    }

    public static void graphicsSetFont(Graphics g, String fontName, int format, int size) {
        g.setFont(getFont(fontName, format, size));
    }

    /**
     * To draw text onto a Graphics object.
     */
    public static Rectangle graphicsDrawText(Graphics g, String text, Rectangle area, int alignment, boolean ascentOnly) {
        Graphics2D g2 = (Graphics2D) g;
        FontMetrics fm = g2.getFontMetrics();
        int width = fm.stringWidth(text);
        int x, y;
        switch (alignment) {
            case 3:
            case 6:
            case 9: {
                x = area.x + area.width - width;
                break;
            }
            case 1:
            case 4:
            case 7: {
                x = area.x;
                break;
            }
            case 2:
            case 5:
            case 8:
            default: {
                x = area.x + (area.width - width) / 2;
                break;
            }
        }
        switch (alignment) {
            case 7:
            case 8:
            case 9: {
                y = area.y + (ascentOnly ? fm.getAscent() : fm.getAscent() + fm.getLeading());
                break;
            }
            case 1:
            case 2:
            case 3: {
                y = area.y + area.height - (ascentOnly ? 0 : fm.getDescent());
                break;
            }
            case 4:
            case 5:
            case 6:
            default: {
                y = area.y + area.height / 2 + (ascentOnly ? fm.getAscent() / 2 : fm.getHeight() / 2 - fm.getDescent());
                break;
            }
        }
        g2.drawString(text, x, y - fm.getLeading());
        return new Rectangle(x, y - fm.getLeading() - fm.getAscent(), width, fm.getHeight() - fm.getLeading());
    }

    public static Rectangle graphicsDrawText(Graphics g, String text, Rectangle area, int alignment) {
        return graphicsDrawText(g, text, area, alignment, true);
    }

    public static Rectangle graphicsDrawText(Graphics g, String text, Rectangle area, boolean ascentOnly) {
        return graphicsDrawText(g, text, area, 5, ascentOnly);
    }

    public static Rectangle graphicsDrawText(Graphics g, String text, Rectangle area) {
        return graphicsDrawText(g, text, area, 5, true);
    }

    public static Rectangle graphicsDrawText(Graphics g, String text, int x, int y, int alignment, boolean ascentOnly) {
        return graphicsDrawText(g, text, new Rectangle(x, y, 0, 0), alignment, ascentOnly);
    }

    public static Rectangle graphicsDrawText(Graphics g, String text, int x, int y, int alignment) {
        return graphicsDrawText(g, text, new Rectangle(x, y, 0, 0), alignment, true);
    }

    public static Rectangle graphicsDrawText(Graphics g, String text, int x, int y, boolean ascentOnly) {
        return graphicsDrawText(g, text, new Rectangle(x, y, 0, 0), 5, ascentOnly);
    }

    public static Rectangle graphicsDrawText(Graphics g, String text, int x, int y) {
        return graphicsDrawText(g, text, new Rectangle(x, y, 0, 0), 5, true);
    }

    private static HashMap<RenderingHints.Key, Object> HINTS;

    public static void graphicsAntiAlias(Graphics2D g) {
        if (HINTS == null) {
            HINTS = new HashMap<>();
            HINTS.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setRenderingHints(HINTS);
    }

    public static Graphics2D imageGetGraphics(Image image) {
        Graphics2D g = (Graphics2D) image.getGraphics();
        graphicsAntiAlias(g);
        return g;
    }

    private static HashMap<String, BufferedImage> imageCache = new HashMap<String, BufferedImage>();

    public static BufferedImage getCacheImage(String path) {
        if (path == null || path.length() == 0) return null;
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }
        URL url = GameUtils.class.getClassLoader().getResource(path);
        BufferedImage image = null;
        try {
            if (DataManager.isInJar() && url != null) {
                image = ImageIO.read(url);
            } else {
                File f = new File(path);
                image = ImageIO.read(f);
            }
        } catch (IOException e) {
            return null;
        }
        imageCache.put(path, image);
        return image;
    }

    private static HashMap<String, Font> fontPathCache = new HashMap<>();

    public static Font getFontFromPath(String path, int format) {
        Font res = fontPathCache.getOrDefault(path, null);
        if (res != null) {
            return res;
        }
        try {
            InputStream is = DataManager.getInputStream(path);
            res = Font.createFont(format, is);
            fontPathCache.put(path, res);
            return res;
        }
        catch (IOException | FontFormatException e) { e.printStackTrace(); return null; }
    }

    public static Font getFontFromPath(String path) { return getFontFromPath(path, Font.TRUETYPE_FONT); }

    public static BufferedImage getImage(String path) {
        return cloneImage(getCacheImage(path));
    }

    public static BufferedImage cloneImage(Image image) {
        if (image == null) return null;
        BufferedImage img = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.drawImage(image, 0, 0, null);
        return img;
    }

    public static BufferedImage emptyImage(Dimension size, Color color) {
        BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setColor(color);
        g.fillRect(0, 0, size.width, size.height);
        return img;
    }

    public static BufferedImage emptyImage(int width, int height, Color color) {
        return emptyImage(new Dimension(width, height), color);
    }

    public static BufferedImage emptyImage(Dimension size) {
        return emptyImage(size, new Color(0, 0, 0, 0));
    }

    public static BufferedImage emptyImage(int width, int height) {
        return emptyImage(new Dimension(width, height), new Color(0, 0, 0, 0));
    }

    public static BufferedImage emptyImage() {
        return emptyImage(new Dimension(1, 1), new Color(0, 0, 0, 0));
    }

    public static final BufferedImage emptyImage = emptyImage();

    public static boolean isCollidingRect(Rectangle obj1, Rectangle obj2) {
        if (obj1.getX() > obj2.getX() + obj2.getWidth()) return false;
        if (obj2.getX() > obj1.getX() + obj1.getWidth()) return false;
        if (obj1.getY() > obj2.getY() + obj2.getHeight()) return false;
        if (obj2.getY() > obj1.getY() + obj1.getHeight()) return false;
        return true;
    }

    public static boolean isCollidingRound(Rectangle obj1, Rectangle obj2, double radius1, double radius2) {
        double x1 = obj1.getX() + (double) obj1.getWidth() / 2;
        double y1 = obj1.getY() + (double) obj1.getHeight() / 2;
        double x2 = obj2.getX() + (double) obj2.getWidth() / 2;
        double y2 = obj2.getY() + (double) obj2.getHeight() / 2;
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) <= radius1 + radius2;
    }

    public static final int WINDOW_LEFT = 1;
    public static final int WINDOW_RIGHT = 2;
    public static final int WINDOW_UP = 4;
    public static final int WINDOW_DOWN = 8;

    /**
     * To draw a window. The source image is in RPG Maker VX format.
     *
     * @param sourceImage
     * @param width
     * @param height
     * @param direction
     * @return
     */
    public static BufferedImage getWindowImage(Image sourceImage, int width, int height, int direction, boolean frame) {
        if (sourceImage == null) return null;
        int sourceWidth = sourceImage.getWidth(null);
        int sourceHeight = sourceImage.getHeight(null);
        if (width < sourceWidth / 2 && frame) width = sourceWidth / 2;
        if (height < sourceHeight / 2 && frame) height = sourceHeight / 2;
        BufferedImage image = emptyImage(width, height);
        Graphics2D g = imageGetGraphics(image);
        int bgx1 = sourceWidth / 64, bgy1 = sourceHeight / 64, bgx2 = width - sourceWidth / 64, bgy2 = height - sourceHeight / 64;
        if (frame) {
            bgx1 = sourceWidth / 64;
            bgy1 = sourceHeight / 64;
            bgx2 = width - sourceWidth / 64;
            bgy2 = height - sourceHeight / 64;
        } else {
            bgx1 = 0;
            bgy1 = 0;
            bgx2 = width;
            bgy2 = height;
        }
        //Draw Background
        g.drawImage(sourceImage, bgx1, bgy1, bgx2, bgy2, 0, 0, sourceWidth / 2, sourceHeight / 2, null);
        //Draw Background2
        int remainWidth = (bgx2 - bgx1) % (sourceWidth / 2);
        int remainHeight = (bgy2 - bgy1) % (sourceHeight / 2);
        for (int y = bgy1; y < bgy2; y += sourceHeight / 2)
            for (int x = bgx1; x < bgx2; x += sourceWidth / 2) {
                boolean bx = x <= bgx2 - sourceWidth / 2;
                boolean by = y <= bgy2 - sourceHeight / 2;
                g.drawImage(sourceImage, x, y, (bx ? x + sourceWidth / 2 : bgx2), (by ? y + sourceHeight / 2 : bgy2), 0, sourceHeight / 2, (bx ? sourceWidth / 2 : remainWidth), (by ? sourceHeight : sourceHeight / 2 + remainHeight), null);
            }
        if (frame) {
            //Draw Corner
            g.drawImage(sourceImage, 0, 0, sourceWidth / 8, sourceHeight / 8, sourceWidth / 2, 0, sourceWidth * 5 / 8, sourceHeight / 8, null);
            g.drawImage(sourceImage, 0, height - sourceHeight / 8, sourceWidth / 8, height, sourceWidth / 2, sourceHeight * 3 / 8, sourceWidth * 5 / 8, sourceHeight / 2, null);
            g.drawImage(sourceImage, width - sourceWidth / 8, 0, width, sourceHeight / 8, sourceWidth * 7 / 8, 0, sourceWidth, sourceHeight / 8, null);
            g.drawImage(sourceImage, width - sourceWidth / 8, height - sourceHeight / 8, width, height, sourceWidth * 7 / 8, sourceHeight * 3 / 8, sourceWidth, sourceHeight / 2, null);
            //Draw Frame
            g.drawImage(sourceImage, 0, sourceHeight / 8, sourceWidth / 8, height - sourceHeight / 8, sourceWidth / 2, sourceHeight / 8, sourceWidth * 5 / 8, sourceHeight * 3 / 8, null);
            g.drawImage(sourceImage, width - sourceWidth / 8, sourceHeight / 8, width, height - sourceHeight / 8, sourceWidth * 7 / 8, sourceHeight / 8, sourceWidth, sourceHeight * 3 / 8, null);
            g.drawImage(sourceImage, sourceWidth / 8, 0, width - sourceWidth / 8, sourceHeight / 8, sourceWidth * 5 / 8, 0, sourceWidth * 7 / 8, sourceHeight / 8, null);
            g.drawImage(sourceImage, sourceWidth / 8, height - sourceHeight / 8, width - sourceWidth / 8, height, sourceWidth * 5 / 8, sourceHeight * 3 / 8, sourceWidth * 7 / 8, sourceHeight / 2, null);
        }
        //Draw Arrows
        if ((WINDOW_LEFT & direction) != 0) {
            g.drawImage(sourceImage, bgx1, height / 2 - sourceHeight / 16, bgx1 + sourceWidth / 16, height / 2 + sourceHeight / 16, sourceWidth * 5 / 8, sourceHeight * 3 / 16, sourceWidth * 11 / 16, sourceHeight * 5 / 16, null);
        }
        if ((WINDOW_RIGHT & direction) != 0) {
            g.drawImage(sourceImage, bgx2 - sourceWidth / 16, height / 2 - sourceHeight / 16, bgx2, height / 2 + sourceHeight / 16, sourceWidth * 13 / 16, sourceHeight * 3 / 16, sourceWidth * 7 / 8, sourceHeight * 5 / 16, null);
        }
        if ((WINDOW_UP & direction) != 0) {
            g.drawImage(sourceImage, width / 2 - sourceWidth / 16, bgy1, width / 2 + sourceWidth / 16, bgy1 + sourceHeight / 16, sourceWidth * 11 / 16, sourceHeight / 8, sourceWidth * 13 / 16, sourceHeight * 3 / 16, null);
        }
        if ((WINDOW_DOWN & direction) != 0) {
            g.drawImage(sourceImage, width / 2 - sourceWidth / 16, bgy2 - sourceHeight / 16, width / 2 + sourceWidth / 16, bgy2, sourceWidth * 11 / 16, sourceHeight * 5 / 16, sourceWidth * 13 / 16, sourceHeight * 3 / 8, null);
        }
        return image;
    }

    public static BufferedImage getWindowImage(Image sourceImage, int width, int height, int direction) {
        return getWindowImage(sourceImage, width, height, direction, true);
    }

    public static BufferedImage getWindowImage(Image sourceImage, Dimension size, int direction) {
        return getWindowImage(sourceImage, size.width, size.height, direction);
    }

    public static BufferedImage getWindowImage(Image sourceImage, int width, int height) {
        return getWindowImage(sourceImage, width, height, 0);
    }

    public static BufferedImage getWindowImage(Image sourceImage, Dimension size) {
        return getWindowImage(sourceImage, size.width, size.height, 0);
    }

    public static BufferedImage getWindowImage(String path, int width, int height, int direction) {
        BufferedImage sourceImage = getImage(path);
        return getWindowImage(sourceImage, width, height, direction);
    }

    public static BufferedImage getWindowImage(String path, Dimension size, int direction) {
        return getWindowImage(path, size.width, size.height, direction);
    }

    public static BufferedImage getWindowImage(String path, int width, int height) {
        return getWindowImage(path, width, height, 0);
    }

    public static BufferedImage getWindowImage(String path, Dimension size) {
        return getWindowImage(path, size.width, size.height, 0);
    }

    public static void clearCache() {
        imageCache.clear();
    }

    private static boolean mouseInside = false;
    private static final HashMap<Integer, Boolean> mouseDown = new HashMap<Integer, Boolean>();
    private static final HashMap<Integer, Boolean> mouseToggle = new HashMap<Integer, Boolean>();
    private static final HashMap<Integer, Integer> mouseClick = new HashMap<Integer, Integer>();
    private static Point mouseLocation = new Point(0, 0);
    private static double mousePreciseRotation = 0.0;
    private static int mouseRotation = 0;

    public static boolean isMouseInside() {
        return mouseInside;
    }

    public static double getMousePreciseRotation() {
        return mousePreciseRotation;
    }

    public static int getMouseRotation() {
        return mouseRotation;
    }

    public static boolean isMousePressing(int code) {
        if (!mouseDown.containsKey(code)) return false;
        return mouseDown.get(code);
    }

    public static boolean isMouseToggled(int code) {
        if (!mouseToggle.containsKey(code)) return false;
        return mouseToggle.get(code);
    }

    public static int getMouseClick(int code) {
        if (!mouseClick.containsKey(code)) return 0;
        return mouseClick.get(code);
    }

    public static Point getMouseScreenLocation() {
        return MouseInfo.getPointerInfo().getLocation();
    }

    public static Point getMouseLocation() {
        return mouseLocation;
    }

    private static final HashMap<Integer, Boolean> keyDown = new HashMap<Integer, Boolean>();
    private static final HashMap<Integer, Boolean> keyToggle = new HashMap<Integer, Boolean>();
    private static final HashMap<Integer, Boolean> keyClick = new HashMap<Integer, Boolean>();

    public static void inputClear() {
        mouseToggle.clear();
        mouseClick.clear();
        keyToggle.clear();
        keyClick.clear();
        mouseRotation = 0;
        mousePreciseRotation = 0.0;
    }

    public static boolean isKeyHolding(int keyCode) {
        if (keyDown.containsKey(keyCode))
            return keyDown.get(keyCode);
        return false;
    }

    public static boolean isKeyToggled(int keyCode) {
        if (keyToggle.containsKey(keyCode))
            return keyToggle.get(keyCode);
        return false;
    }

    public static boolean isKeyClicked(int keyCode) {
        if (keyClick.containsKey(keyCode))
            return keyClick.get(keyCode);
        return false;
    }

    private static class Listener implements Runnable, KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            mouseClick.put(e.getButton(), e.getClickCount());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (!isMousePressing(e.getButton()))
                mouseToggle.put(e.getButton(), true);
            mouseDown.put(e.getButton(), true);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mouseDown.put(e.getButton(), false);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            mouseInside = true;
        }

        @Override
        public void mouseExited(MouseEvent e) {
            mouseInside = false;
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (!isKeyHolding(e.getKeyCode()))
                keyToggle.put(e.getKeyCode(), true);
            keyDown.put(e.getKeyCode(), true);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (isKeyHolding(e.getKeyCode()))
                keyClick.put(e.getKeyCode(), true);
            keyDown.put(e.getKeyCode(), false);
        }


        @Override
        public void mouseDragged(MouseEvent e) {
        }


        @Override
        public void mouseMoved(MouseEvent e) {
            mouseLocation = e.getPoint();
        }


        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            mouseRotation = e.getWheelRotation();
            mousePreciseRotation = e.getPreciseWheelRotation();
        }

        @Override
        public void run() {
        }
    }

    public static final Listener listener = new Listener();

    public static BufferedImage toBufferedImage(Image image) {
        /*
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        */
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.TRANSLUCENT;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
        }

        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_ARGB;
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        Graphics g = bimage.createGraphics();

        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    public static BufferedImage imageMask(Image dest, Image mask) {
        BufferedImage img = cloneImage(dest);
        Graphics2D g = imageGetGraphics(img);
        g.setComposite(AlphaComposite.DstIn);
        g.drawImage(mask, 0, 0, null);
        g.dispose();
        return img;
    }

    public static BufferedImage imageIconMask(Image dest, Image mask, Image bg) {
        BufferedImage b = cloneImage(bg);
        Graphics2D g = imageGetGraphics(b);
        g.drawImage(imageMask(dest, mask), 0, 0, null);
        g.dispose();
        return b;
    }

    public static BufferedImage imageResize(Image src, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imageGetGraphics(img);
        g.drawImage(src, 0, 0, width, height, 0, 0, src.getWidth(null), src.getHeight(null), null);
        g.dispose();
        return img;
    }

    public static void clearImage(Image image) {
        Graphics2D g = imageGetGraphics(image);
        g.setBackground(new Color(0, 0, 0, 0));
        g.clearRect(0, 0, image.getWidth(null), image.getHeight(null));
        g.dispose();
    }
}