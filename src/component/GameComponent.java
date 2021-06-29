package component;

import java.awt.Graphics2D;

public abstract class GameComponent extends GameObject {
    public abstract void paintImage(Graphics2D g);
    Integer serial = null;
}
