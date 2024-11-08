package exel.eventsys.events.sheet;

import engine.spreadsheet.api.ReadOnlySheet;


public class SheetDisplayEvent {
    private final ReadOnlySheet sheet;
    private final boolean shouldUpdateVersion;

    public SheetDisplayEvent(ReadOnlySheet sheet) {
        this(sheet, true);
    }

    public SheetDisplayEvent(ReadOnlySheet sheet, boolean shouldUpdateVersion) {
        this.sheet = sheet;
        this.shouldUpdateVersion = shouldUpdateVersion;
    }

    public ReadOnlySheet getSheet() {
        return sheet;
    }

    public boolean shouldUpdateVersion() {
        return shouldUpdateVersion;
    }
}
