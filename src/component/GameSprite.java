package component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.ListIterator;

public class GameSprite extends GameSpriteBase {
    private LinkedList<GameAnimation> animations = new LinkedList<>();

    public GameSprite() {
        super();
    }

    public GameSprite(String path) {
        super(path);
    }

    public GameSprite(BufferedImage image) {
        super(image);
    }

    public void attachAnimation(GameAnimation animation, int times) {
        animations.addLast(animation);
        animation.start(times);
        animation.disposeAfterStop = true;
    }

    public void attachAnimation(GameAnimation animation) {
        attachAnimation(animation, -1);
    }

    public void refresh() {
        if (disposed) return;
        super.refresh();
        ListIterator<GameAnimation> it = animations.listIterator();
        while (it.hasNext()) {
            GameAnimation a = (GameAnimation) it.next();
            a.refresh();
            if (a.isDisposed()) {
                it.remove();
            }
        }
    }

    public void paintImage(Graphics2D g) {
        super.paintImage(g);
        g.translate(this.getX(), this.getY());
        for (GameAnimation animation : animations) {
            animation.paintImage(g);
        }
        g.translate(-this.getX(), -this.getY());
    }
}
