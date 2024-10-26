package exel.eventsys.events.sheet;

public class SheetResizeWidthEvent {
    private final int width;
    public SheetResizeWidthEvent(int width) {
        this.width = width;
    }
    public int getWidth() {
        return width;
    }
}
