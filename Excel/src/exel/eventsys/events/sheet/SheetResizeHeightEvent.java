package exel.eventsys.events.sheet;

public class SheetResizeHeightEvent {
    private final int height;
    public SheetResizeHeightEvent(int newHeight) {
        this.height = newHeight;
    }
    public int getHeight() {
        return height;
    }
}
