package exel.eventsys.events.sheet;

import engine.spreadsheet.api.ReadOnlySheet;


public class SheetDisplayEvent {
    private final ReadOnlySheet sheet;

    public SheetDisplayEvent(ReadOnlySheet sheet) {
        this.sheet = sheet;
    }

    public ReadOnlySheet getSheet() {
        return sheet;
    }
}
