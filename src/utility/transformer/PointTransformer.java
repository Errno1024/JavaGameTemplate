package utility.transformer;

import java.awt.*;

public class PointTransformer {
    private final ValueTransformer vx;
    private ValueTransformer vy;

    public PointTransformer(Point init, Point dest, long totalTime, double initSmooth, double destSmooth) {
        vx = new ValueTransformer(init.x, dest.x, totalTime, initSmooth, destSmooth);
        vy = new ValueTransformer(init.y, dest.y, totalTime, initSmooth, destSmooth);
    }

    public PointTransformer(Point init, Point dest, long totalTime) {
        vx = new ValueTransformer(init.x, dest.x, totalTime);
        vy = new ValueTransformer(init.y, dest.y, totalTime);
    }

    public void refresh() {
        vx.refresh();
        vy.refresh();
    }

    public Point getValue() {
        double x = vx.getValue(), y = vy.getValue();
        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    public boolean isEnd() {
        return vx.isEnd();
    }
}
