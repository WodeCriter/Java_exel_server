package exel.eventsys.events;

import engine.spreadsheet.api.ReadOnlySheet;

public class DisplaySheetPopupEvent {
    private ReadOnlySheet readOnlySheet;

    public DisplaySheetPopupEvent(ReadOnlySheet readOnlySheet) {
        this.readOnlySheet = readOnlySheet;
    }

    public ReadOnlySheet getReadOnlySheet() {
        return readOnlySheet;
    }
}
