package component;

import settings.Constants;
import utility.GameUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.util.ArrayDeque;
import java.util.Vector;

class CursorSprite extends GameSprite {
    private BufferedImage windowSkin;
    private int glitterCount = 0;
    private int glitterTime = 0;
    private Color glitterColor;
    private BufferedImage glitterImage;

    public CursorSprite(Dimension size) {
        this.size = size.getSize();
        this.setDirection(7);
        setCursorImage();
    }

    public CursorSprite(Rectangle area) {
        this.size = area.getSize();
        this.location = area.getLocation();
        this.setDirection(7);
        setCursorImage();
    }

    public CursorSprite(Dimension size, String path) {
        this.size = size.getSize();
        this.setDirection(7);
        setWindowSkin(path);
    }

    public CursorSprite(Rectangle area, String path) {
        this.size = area.getSize();
        this.location = area.getLocation();
        this.setDirection(7);
        setWindowSkin(path);
    }

    public CursorSprite(Dimension size, Image windowSkin) {
        this.size = size.getSize();
        this.setDirection(7);
        setWindowSkin(windowSkin);
    }

    public CursorSprite(Rectangle area, Image windowSkin) {
        this.size = area.getSize();
        this.location = area.getLocation();
        this.setDirection(7);
        setWindowSkin(windowSkin);
    }

    public void setWindowSkin(Image windowSkin) {
        this.windowSkin = GameUtils.cloneImage(windowSkin);
        setCursorImage();
    }

    public void setWindowSkin(String path) {
        windowSkin = GameUtils.getImage(path);
        setCursorImage();
    }

    public void setSize(Dimension size) {
        this.size = size.getSize();
        setCursorImage();
    }

    public void setArea(Rectangle area) {
        this.location = area.getLocation();
        this.size = area.getSize();
        setCursorImage();
    }

    protected void setCursorImage() {
        sourceImage = GameUtils.emptyImage(size);
        if (windowSkin != null) {
            Graphics2D g = GameUtils.imageGetGraphics(sourceImage);
            int width = windowSkin.getWidth(), height = windowSkin.getHeight();
            int frameWidth = width / 64, frameHeight = height / 64;
            int cWidth = sourceImage.getWidth(), cHeight = sourceImage.getHeight();
            //Draw Corner
            g.drawImage(windowSkin, 0, 0, frameWidth, frameHeight, width / 2, height / 2, width / 2 + frameWidth, height / 2 + frameHeight, null);
            g.drawImage(windowSkin, cWidth - frameWidth, 0, cWidth, frameHeight, width * 3 / 4 - frameWidth, height / 2, width * 3 / 4, height / 2 + frameHeight, null);
            g.drawImage(windowSkin, 0, cHeight - frameHeight, frameWidth, cHeight, width / 2, height * 3 / 4 - frameHeight, width / 2 + frameWidth, height * 3 / 4, null);
            g.drawImage(windowSkin, cWidth - frameWidth, cHeight - frameHeight, cWidth, cHeight, width * 3 / 4 - frameWidth, height * 3 / 4 - frameHeight, width * 3 / 4, height * 3 / 4, null);
            //Draw Frame
            g.drawImage(windowSkin, frameWidth, 0, cWidth - frameWidth, frameHeight, width / 2 + frameWidth, height / 2, width * 3 / 4 - frameWidth, height / 2 + frameHeight, null);
            g.drawImage(windowSkin, frameWidth, cHeight - frameHeight, cWidth - frameWidth, cHeight, width / 2 + frameWidth, height * 3 / 4 - frameHeight, width * 3 / 4 - frameWidth, height * 3 / 4, null);
            g.drawImage(windowSkin, 0, frameHeight, frameWidth, cHeight - frameHeight, width / 2, height / 2 + frameHeight, width / 2 + frameWidth, height * 3 / 4 - frameHeight, null);
            g.drawImage(windowSkin, cWidth - frameWidth, frameHeight, cWidth, cHeight - frameHeight, width * 3 / 4 - frameWidth, height / 2 + frameHeight, width * 3 / 4, height * 3 / 4 - frameHeight, null);
            //Draw Content
            g.drawImage(windowSkin, frameWidth, frameHeight, cWidth - frameWidth, cHeight - frameHeight, width / 2 + frameWidth, height / 2 + frameHeight, width * 3 / 4 - frameWidth, height * 3 / 4 - frameHeight, null);
        }
        imageResize();
    }

    protected boolean isGlittering() {
        return glitterCount != 0;
    }

    public void glitter(int frameCount, Color color) {
        glitterCount = frameCount;
        glitterTime = 0;
        glitterColor = color;
        glitterImage = GameUtils.emptyImage(size, color);
    }

    public void glitter(int frameCount) {
        glitter(frameCount, Constants.DEFAULT_CURSOR_GLITTER_COLOR);
    }

    public void glitter(Color color) {
        glitter(Constants.DEFAULT_CURSOR_GLITTER_FRAME_COUNT, color);
    }

    public void glitter() {
        glitter(Constants.DEFAULT_CURSOR_GLITTER_FRAME_COUNT, Constants.DEFAULT_CURSOR_GLITTER_COLOR);
    }

    @Override
    protected void generateImage() {
        super.generateImage();
        if (isGlittering()) {
            if (glitterImage == null) {
                glitterImage = GameUtils.emptyImage(size, glitterColor);
            }
            Graphics2D g = (Graphics2D) GameUtils.imageGetGraphics(outputImage);
            float alpha = 1.0f - (float) Math.abs(2.0 * glitterTime / glitterCount - 1.0);
            AlphaComposite comp = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha);
            g.setComposite(comp);
            g.drawImage(glitterImage, 0, 0, null);
        }
    }

    @Override
    public void refresh() {
        super.refresh();
        if (isGlittering()) {
            if (++glitterTime == glitterCount)
                glitterTime = 0;
        }
    }
}

class PauseSprite extends GameSprite {
    private BufferedImage windowSkin;
    private int animationInterval = Constants.DEFAULT_PAUSE_ANIMATION_INTERVAL;
    private int animationTime = 0;
    private BufferedImage[] pauseImages = new BufferedImage[4];

    public PauseSprite() {
        setWindowSkin(GameUtils.emptyImage(128, 128));
    }

    public PauseSprite(String path) {
        setWindowSkin(path);
    }

    public PauseSprite(Image skin) {
        setWindowSkin(skin);
    }

    public void setWindowSkin(String path) {
        windowSkin = GameUtils.getImage(path);
        generatePauseImages();
    }

    public void setWindowSkin(Image skin) {
        windowSkin = GameUtils.cloneImage(skin);
        generatePauseImages();
    }

    protected void generatePauseImages() {
        int wWidth = windowSkin.getWidth(), wHeight = windowSkin.getHeight();
        int width = wWidth / 8;
        int height = wHeight / 8;
        pauseImages[0] = GameUtils.emptyImage(width, height);
        pauseImages[1] = GameUtils.emptyImage(width, height);
        pauseImages[2] = GameUtils.emptyImage(width, height);
        pauseImages[3] = GameUtils.emptyImage(width, height);
        Graphics2D g;
        g = (Graphics2D) pauseImages[0].getGraphics();
        g.drawImage(windowSkin, 0, 0, width, height, wWidth * 3 / 4, wHeight / 2, wWidth * 7 / 8, wHeight * 5 / 8, null);
        g = (Graphics2D) pauseImages[1].getGraphics();
        g.drawImage(windowSkin, 0, 0, width, height, wWidth * 7 / 8, wHeight / 2, wWidth, wHeight * 5 / 8, null);
        g = (Graphics2D) pauseImages[2].getGraphics();
        g.drawImage(windowSkin, 0, 0, width, height, wWidth * 3 / 4, wHeight * 5 / 8, wWidth * 7 / 8, wHeight * 3 / 4, null);
        g = (Graphics2D) pauseImages[3].getGraphics();
        g.drawImage(windowSkin, 0, 0, width, height, wWidth * 7 / 8, wHeight * 5 / 8, wWidth, wHeight * 3 / 4, null);

        resetAnimation();
    }

    public boolean isAnimating() {
        return animationInterval != 0;
    }

    protected void resetAnimation() {
        animationTime = 0;
        sourceImage = pauseImages[0];
    }

    public void setAnimationInterval(int interval) {
        animationInterval = interval;
        resetAnimation();
    }

    @Override
    public void refresh() {
        super.refresh();
        if (isAnimating()) {
            if (++animationTime == animationInterval) {
                animationTime = 0;
            }
            sourceImage = pauseImages[animationTime * 4 / animationInterval];
        } else sourceImage = pauseImages[0];
    }
}

public class GameWindow extends GameComponent {

    public boolean showSkin = false;
    protected BufferedImage windowSkin;
    protected BufferedImage skinImage;
    protected int skinDirection;
    protected int skinFrameWidth;
    protected int skinFrameHeight;
    protected int opacity = 255;

    protected BufferedImage contents = null;

    protected GameSprite sprite;

    protected CursorSprite cursor;
    private Vector<Rectangle> cursorAreas = new Vector<Rectangle>();
    private Vector<Integer> cursorAreaSlots = new Vector<Integer>();
    private ArrayDeque<Integer> cursorAreaEmptySlots = new ArrayDeque<Integer>();
    protected boolean cursorEnable;
    private int cursorIndex = 0;

    public int getCursorIndes() {
        return cursorIndex;
    }

    protected PauseSprite pauseCursor;
    protected Point pauseCursorLocation;

    private boolean opening = false;
    private int initialAlpha = 0;
    private int openCount = 0;
    private int openTransitionCount = 0;
    private Rectangle openArea;
    private Point openPauseCursorLocation;
    protected boolean disposeAfterClose = false;

    protected boolean disposed = false;

    public GameWindow(Dimension size, String skinPath) {
        this.size = size.getSize();
        setSkin(skinPath);
        show();
        initialize();
    }

    public GameWindow(Dimension size) {
        this.size = size.getSize();
        setSkin(null);
        show();
        initialize();
    }

    public GameWindow(Rectangle area, String skinPath) {
        this.location = area.getLocation();
        this.size = area.getSize();
        setSkin(skinPath);
        show();
        initialize();
    }

    public GameWindow(Rectangle area) {
        this.location = area.getLocation();
        this.size = area.getSize();
        setSkin(null);
        show();
        initialize();
    }

    protected void initialize() {
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public boolean isPauseCursorVisible() {
        return pauseCursor.visible;
    }

    public void setPauseCursorVisible(boolean visible) {
        pauseCursor.visible = visible;
    }

    public void setSkin(String skinPath) {
        if (disposed) return;
        skinImage = GameUtils.getCacheImage(skinPath);
        if (skinImage == null)
            skinImage = GameUtils.emptyImage(128, 128);
        pauseCursorLocation = getPauseCursorLocation(this.getArea());
        skinFrameWidth = skinImage.getWidth() / 8;
        skinFrameHeight = skinImage.getHeight() / 8;
        sprite = new GameSprite(skinImage);
        sprite.setDirection(7);
        windowSkin = GameUtils.getWindowImage(skinImage, this.getWidth(), this.getHeight());
        setContents(contents);
        pauseCursor = new PauseSprite(skinImage);
        pauseCursor.setDirection(5);
    }

    public void setSize(Dimension size) {
        if (disposed) return;
        this.size = size.getSize();
        windowSkin = GameUtils.getWindowImage(skinImage, this.size.width, this.size.height);
        pauseCursorLocation = getPauseCursorLocation(this.getArea());
        pauseCursor.setLocation(pauseCursorLocation);
    }

    public void setLocation(Point location) {
        super.setLocation(location);
        if (isOpening()) {
            openPauseCursorLocation = getPauseCursorLocation(openArea);
        }
        pauseCursorLocation = getPauseCursorLocation(getArea());
    }

    private Point getPauseCursorLocation(Rectangle area) {
        return new Point(area.x + area.width / 2, area.y + area.height - skinImage.getHeight() / 16);
    }

    public BufferedImage getContents() {
        return contents;
    }

    public void setContents(Image image) {
        if (disposed) return;
        contents = GameUtils.emptyImage(this.getWidth() - 2 * skinFrameWidth, this.getHeight() - 2 * skinFrameHeight);
        if (image != null)
            contents.getGraphics().drawImage(image, 0, 0, null);
    }

    public void clearContents() {
        clearContents(0, 0, contents.getWidth(), contents.getHeight());
    }

    public void clearContents(int x, int y, int width, int height) {
        if (disposed) return;
        Graphics2D g = (Graphics2D) contents.getGraphics();
        g.clearRect(x, y, width, height);
    }

    public boolean isStable() {
        if (openCount != 0) return false;
        return true;
    }

    public boolean isOpening() {
        return opening && openCount != 0;
    }

    public boolean isClosing() {
        return !opening && openCount != 0;
    }

    public void open(int frameCount, Rectangle area, int initialAlpha) {
        if (frameCount < 0)
            frameCount = 0;
        openArea = area;
        openPauseCursorLocation = getPauseCursorLocation(openArea);
        this.initialAlpha = initialAlpha;
        openCount = frameCount;
        openTransitionCount = 0;
        opening = true;
    }

    public void open(int frameCount, Rectangle area) {
        open(frameCount, area, 0);
    }

    public void close(int frameCount, Rectangle area, int destAlpha, boolean disposeAfterClose) {
        if (!opening && disposeAfterClose) return;
        if (frameCount < 0)
            frameCount = 0;
        openArea = area;
        openPauseCursorLocation = getPauseCursorLocation(openArea);
        initialAlpha = destAlpha;
        openCount = frameCount;
        openTransitionCount = 0;
        opening = false;
        this.disposeAfterClose = disposeAfterClose;
    }

    public void close(int frameCount, Rectangle area, int destAlpha) {
        close(frameCount, area, destAlpha, false);
    }

    public void close(int frameCount, Rectangle area, boolean disposeAfterClose) {
        close(frameCount, area, 0, disposeAfterClose);
    }

    public void close(int frameCount, Rectangle area) {
        close(frameCount, area, 0, false);
    }

    public void show() {
        openCount = 0;
        openTransitionCount = 0;
        opening = true;
    }

    public void hide() {
        openCount = 0;
        openTransitionCount = 0;
        opening = false;
    }

    public BufferedImage getImage() {
        if (disposed) return null;
        generateImage();
        return sprite.getImage();
    }

    public void paintImage(Graphics2D g) {
        if (disposed) return;
        generateImage();
        sprite.paintImage(g);
        if (pauseCursor != null)
            pauseCursor.paintImage(g);
    }

    public boolean isOpened() {
        return opening;
    }

    protected void generateImage() {
        if (disposed) return;
        BufferedImage image = GameUtils.cloneImage(windowSkin);
        BufferedImage contents = GameUtils.cloneImage(this.contents);
        if (cursor != null)
            cursor.paintImage(GameUtils.imageGetGraphics(contents));
        image.getGraphics().drawImage(contents, skinFrameWidth, skinFrameHeight, this.getWidth() - skinFrameWidth, this.getHeight() - skinFrameHeight, 0, 0, contents.getWidth(), contents.getHeight(), null);
        sprite.setImage(image);
        if (isOpening()) {
            double timeRate = (double) openTransitionCount / openCount;
            double rateX = (double) openArea.getWidth() / windowSkin.getWidth();
            double rateY = (double) openArea.getHeight() / windowSkin.getHeight();
            sprite.setScaleX(GameUtils.valueTransition(timeRate, rateX, 1.0));
            sprite.setScaleY(GameUtils.valueTransition(timeRate, rateY, 1.0));
            sprite.setX((int) GameUtils.valueTransition(timeRate, openArea.x, this.getX()));
            sprite.setY((int) GameUtils.valueTransition(timeRate, openArea.y, this.getY()));
            sprite.setOpacity((int) GameUtils.valueTransition(timeRate, initialAlpha, opacity));
            pauseCursor.setScaleX(sprite.getScaleX());
            pauseCursor.setScaleY(sprite.getScaleY());
            pauseCursor.setOpacity(sprite.getOpacity());
            pauseCursor.setX((int) GameUtils.valueTransition(timeRate, openPauseCursorLocation.x, pauseCursorLocation.x));
            pauseCursor.setY((int) GameUtils.valueTransition(timeRate, openPauseCursorLocation.y, pauseCursorLocation.y));
        } else if (isClosing()) {
            double timeRate = (double) openTransitionCount / openCount;
            double rateX = (double) openArea.getWidth() / windowSkin.getWidth();
            double rateY = (double) openArea.getHeight() / windowSkin.getHeight();
            sprite.setScaleX(GameUtils.valueTransition(timeRate, 1.0, rateX));
            sprite.setScaleY(GameUtils.valueTransition(timeRate, 1.0, rateY));
            sprite.setX((int) GameUtils.valueTransition(timeRate, this.getX(), openArea.x));
            sprite.setY((int) GameUtils.valueTransition(timeRate, this.getY(), openArea.y));
            sprite.setOpacity((int) GameUtils.valueTransition(timeRate, opacity, initialAlpha));
            pauseCursor.setScaleX(sprite.getScaleX());
            pauseCursor.setScaleY(sprite.getScaleY());
            pauseCursor.setOpacity(sprite.getOpacity());
            pauseCursor.setX((int) GameUtils.valueTransition(timeRate, pauseCursorLocation.x, openPauseCursorLocation.x));
            pauseCursor.setY((int) GameUtils.valueTransition(timeRate, pauseCursorLocation.y, openPauseCursorLocation.y));
        } else if (opening) {
            sprite.setScaleX(1.0);
            sprite.setScaleY(1.0);
            sprite.setX(this.getX());
            sprite.setY(this.getY());
            sprite.setOpacity(opacity);
            pauseCursor.setScaleX(1.0);
            pauseCursor.setScaleY(1.0);
            pauseCursor.setOpacity(sprite.getOpacity());
            pauseCursor.setX(pauseCursorLocation.x);
            pauseCursor.setY(pauseCursorLocation.y);
        } else {
            sprite.setScaleX(0.0);
            sprite.setScaleY(0.0);
            pauseCursor.setScaleX(0.0);
            pauseCursor.setScaleY(0.0);
        }
    }

    public boolean isCursorEnabled() {
        return cursorEnable;
    }

    public void cursorEnable() {
        if (disposed) return;
        if (cursorAreas.isEmpty()) return;
        cursorEnable = true;
        if (cursor == null)
            cursor = new CursorSprite(cursorAreas.firstElement(), skinImage);
        cursor.visible = true;
        cursorIndex = 0;
    }

    public void cursorDisable() {
        if (cursor != null) {
            cursor.visible = false;
        }
        cursorEnable = false;
    }

    protected void cursorAreaReset() {
        if (disposed) return;
        if (cursorAreaSlots.isEmpty()) {
            cursorDisable();
            cursorIndex = 0;
        } else {
            cursor.setArea(cursorAreas.elementAt(cursorAreaSlots.elementAt(cursorIndex)));
        }
    }

    public int addCursor(Rectangle area) {
        if (disposed) return 0;
        if (cursorAreaEmptySlots.isEmpty()) {
            cursorAreas.add(area);
            cursorAreaSlots.add(cursorAreas.size() - 1);
            return cursorAreas.size() - 1;
        } else {
            int slot = cursorAreaEmptySlots.poll();
            cursorAreas.set(slot, area);
            cursorAreaSlots.add(slot);
            return slot;
        }
    }

    public Rectangle removeCursor(int slot) {
        if (disposed) return null;
        if (cursorAreas.size() <= slot) return null;
        if (cursorAreaSlots.contains(slot)) {
            if (cursorAreaSlots.elementAt(cursorIndex) == slot) {
                cursorAreaSlots.removeElementAt(cursorIndex);
                if (cursorIndex == cursorAreaSlots.size())
                    cursorIndex = 0;
            } else cursorAreaSlots.removeElement(slot);
            cursorAreaEmptySlots.add(slot);
            cursorAreaReset();
            return cursorAreas.set(slot, null);
        } else return null;
    }

    public Rectangle removeCurrentCursor() {
        if (disposed) return null;
        if (cursorAreaSlots.size() == 0) return null;
        int slot = cursorAreaSlots.elementAt(cursorIndex);
        cursorAreaSlots.removeElementAt(cursorIndex);
        if (cursorIndex == cursorAreaSlots.size())
            cursorIndex = 0;
        cursorAreaEmptySlots.add(slot);
        cursorAreaReset();
        return cursorAreas.set(slot, null);
    }

    public void cursorMoveTo(int index) {
        if (disposed) return;
        if (!cursorEnable) return;
        if (cursorAreaSlots.size() <= index) return;
        cursorIndex = index;
        cursorAreaReset();
    }

    public void cursorMoveToSlot(int slot) {
        if (disposed) return;
        if (!cursorEnable) return;
        if (cursorAreas.size() <= slot) return;
        if (cursorAreas.elementAt(slot) != null) {
            cursorIndex = cursorAreaSlots.indexOf(slot);
            cursorAreaReset();
        }
    }

    public void cursorMoveBack() {
        if (disposed) return;
        if (!cursorEnable || cursorAreaSlots.isEmpty()) return;
        if (--cursorIndex < 0)
            cursorIndex = cursorAreaSlots.size() - 1;
        cursorAreaReset();
    }

    public void cursorMoveForward() {
        if (disposed) return;
        if (!cursorEnable || cursorAreaSlots.isEmpty()) return;
        if (++cursorIndex >= cursorAreaSlots.size())
            cursorIndex = 0;
        cursorAreaReset();
    }

    public Rectangle getCursorArea() {
        if (disposed) return null;
        if (cursorAreaSlots.isEmpty())
            return null;
        return cursorAreas.elementAt(cursorAreaSlots.elementAt(cursorIndex));
    }

    public void cursorSetGlitter(int frameCount, Color color) {
        if (disposed) return;
        if (cursor != null) cursor.glitter(frameCount, color);
    }

    public void cursorSetGlitter(int frameCount) {
        if (disposed) return;
        if (cursor != null) cursor.glitter(frameCount);
    }

    public void cursorSetGlitter(Color color) {
        if (disposed) return;
        if (cursor != null) cursor.glitter(color);
    }

    public void cursorSetGlitter() {
        if (disposed) return;
        if (cursor != null) cursor.glitter();
    }

    public void select() {
        this.raiseEvent(GameEvent.WINDOW_SELECT, cursorIndex);
    }

    public void refresh() {
        if (disposed) return;
        action();
        sprite.refresh();
        if (cursor != null) cursor.refresh();
        if (pauseCursor != null) pauseCursor.refresh();
        if (openCount != 0) {
            if (openCount == openTransitionCount++) {
                openCount = 0;
                openTransitionCount = 0;
                if (!opening && disposeAfterClose)
                    dispose();
            }
        }
    }

    public void dispose() {
        if (disposed) return;
        super.dispose();
        sprite.dispose();
        if (cursor != null)
            cursor.dispose();
        if (pauseCursor != null)
            pauseCursor.dispose();
        windowSkin = null;
        skinImage = null;
        cursorAreas.clear();
        cursorAreaSlots.clear();
        cursorAreaEmptySlots.clear();
    }

    protected void action() {
    }
}
