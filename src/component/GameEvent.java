package component;

public class GameEvent {
    private GameObject source;
    private int eventType;
    private int eventValue;

    public GameEvent(GameObject source, int type, int value) {
        this.source = source;
        eventType = type;
        eventValue = value;
    }

    public GameObject getSource() {
        return source;
    }

    public int getType() {
        return eventType;
    }

    public int getValue() {
        return eventValue;
    }

    public static final int WINDOW_SELECT = 0;
}
