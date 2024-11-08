package exel.eventsys.events.sheet;

import engine.spreadsheet.api.ReadOnlySheet;

public class TheresUpdateToSheetEvent
{
    private ReadOnlySheet mostRecentSheet;
    public TheresUpdateToSheetEvent(ReadOnlySheet mostRecentSheet) {
        this.mostRecentSheet = mostRecentSheet;
    }

    public ReadOnlySheet getMostRecentSheet() {
        return mostRecentSheet;
    }
}
