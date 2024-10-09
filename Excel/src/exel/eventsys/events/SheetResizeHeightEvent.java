package exel.eventsys.events;

import exel.engine.spreadsheet.api.Sheet;

public class SheetResizeHeightEvent {
    private final int height;
    public SheetResizeHeightEvent(int newHeight) {
        this.height = newHeight;
    }
    public int getHeight() {
        return height;
    }
}
