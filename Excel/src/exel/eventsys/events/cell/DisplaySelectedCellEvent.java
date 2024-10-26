package exel.eventsys.events.cell;

import engine.spreadsheet.cell.api.ReadOnlyCell;

public class DisplaySelectedCellEvent {
    private final ReadOnlyCell cell;

    public DisplaySelectedCellEvent(ReadOnlyCell cell) {
        this.cell = cell;
    }

    public ReadOnlyCell getCell() {
        return this.cell;
    }
}
