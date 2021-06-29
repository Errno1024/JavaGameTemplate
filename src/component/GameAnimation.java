package component;

import java.awt.image.BufferedImage;
import java.util.Vector;

import utility.GameUtils;
import data.DataAnimation;
import data.DataUtils;

public class GameAnimation extends GameSpriteBase {
    private final Vector<BufferedImage> pictureCache = new Vector<BufferedImage>();
    private DataAnimation animation = null;
    private boolean animationStarted = false;
    private int frameIndex = 0;
    private int animationFrameCount = 0;
    private int animationCount = 0;
    public boolean disposeAfterStop = false;

    public GameAnimation() {
    }

    public GameAnimation(DataAnimation animation) {
        loadAnimation(animation);
    }

    public void loadAnimation(DataAnimation animation) {
        if (disposed) return;
        this.animation = animation;
        pictureCache.clear();
        frameIndex = 0;
        animationFrameCount = 0;
    }

    public DataAnimation getAnimation() {
        return animation;
    }

    public boolean isStarted() {
        return animationStarted;
    }

    public void start(int times) {
        if (disposed) return;
        animationStarted = true;
        animationCount = times;
    }

    public void start() {
        start(1);
    }

    public void loop() {
        start(-1);
    }

    public void stop() {
        animationStarted = false;
    }

    @Override
    protected void generateImage() {
        if (disposed) return;
        if (animation != null && animationCount != 0 && animation.frames.length > frameIndex) {
            if (pictureCache.size() <= frameIndex) {
                pictureCache.add(DataUtils.getAnimationImage(animation, animation.frames[frameIndex].pictureIndex));
            }
            setImage(pictureCache.elementAt(frameIndex));
            setMirror(animation.frames[frameIndex].mirror);
        } else setImage(GameUtils.emptyImage);
        super.generateImage();
    }

    @Override
    public void refresh() {
        super.refresh();
        if (animation != null && animationCount != 0 && animationStarted && animation.frames.length > 0 && animation.frames.length > frameIndex) {
            if (pictureCache.size() <= frameIndex) {
                pictureCache.add(DataUtils.getAnimationImage(animation, animation.frames[frameIndex].pictureIndex));
            }
            if (animation.frames[frameIndex].flashCount > 0) {
                forceFlash(animation.frames[frameIndex].flashCount, animation.frames[frameIndex].flashColor);
            }
            if (++animationFrameCount >= animation.frames[frameIndex].frameCount) {
                animationFrameCount = 0;
                if (++frameIndex >= animation.frames.length) {
                    frameIndex = 0;
                    if (animationCount > 0) {
                        if (--animationCount == 0) {
                            stop();
                            if (disposeAfterStop)
                                dispose();
                        }
                    }
                }
            }
        }
    }
}
