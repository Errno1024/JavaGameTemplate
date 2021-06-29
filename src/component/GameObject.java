package component;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;

public abstract class GameObject implements Comparable<GameObject> {
    protected Point location;
    protected Dimension size;
    int depth = 0;
    protected boolean absoluteLocation = false;

    public boolean isAbsoluteLocation() { return absoluteLocation; }
    public void setAbsoluteLocation(boolean absoluteLocation) {
        this.absoluteLocation = absoluteLocation;
    }

    public GameObject() {
        location = new Point(0, 0);
        size = new Dimension(0, 0);
    }

    public GameObject(Point loc) {
        location = loc.getLocation();
        size = new Dimension(0, 0);
    }

    public GameObject(Dimension sz) {
        location = new Point(0, 0);
        size = sz.getSize();
    }

    public GameObject(Rectangle area) {
        location = area.getLocation();
        size = area.getSize();
    }

    public int getX() {
        return location.x;
    }

    public int getY() {
        return location.y;
    }

    public Point getLocation() {
        return location.getLocation();
    }

    public Rectangle getArea() {
        return new Rectangle(getLocation(), getSize());
    }

    public void setX(int x) {
        location.x = x;
    }

    public void setY(int y) {
        location.y = y;
    }

    public void setLocation(Point p) {
        if (p == null)
            p = new Point();
        location = p.getLocation();
    }

    public int getWidth() {
        return size.width;
    }

    public int getHeight() {
        return size.height;
    }

    public Dimension getSize() {
        return size.getSize();
    }

    protected GameObject parent = null;

    public GameObject getParent() {
        return parent;
    }

    public void setParent(GameObject parent) {
        this.parent = parent;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
        if (parent != null)
            parent.onChildDepthChange(this);
    }

    @Override
    public int compareTo(GameObject o) {
        if (o == null) return -1;
        return this.depth - o.depth;
    }

    protected void onChildDepthChange(GameObject source) {}

    protected void onChildDispose(GameObject source) {}

    public boolean isChild(GameObject child) {
        return child.parent == this;
    }

    private Vector<GameObject> listeners;

    public void addListener(GameObject listener) {
        if (listeners == null)
            listeners = new Vector<GameObject>();
        listeners.add(listener);
    }

    public void removeListener(GameObject listener) {
        if (listeners == null)
            return;
        listeners.removeElement(listener);
    }

    protected void raiseEvent(int type, int value) {
        GameEvent e = new GameEvent(this, type, value);
        raiseEvent(e);
    }

    protected void raiseEvent(GameEvent e) {
        for (Object o : listeners.toArray())
            ((GameObject) o).onEvent(e);
    }

    protected void onEvent(GameEvent e) {}

    protected boolean disposed = false;

    public void dispose() {
        if (disposed) return;
        disposed = true;
        if (parent != null)
            parent.onChildDispose(this);
    }

    public boolean isDisposed() {
        return disposed;
    }

    public void refresh() {}
}
