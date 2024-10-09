package exel.eventsys.events;

import exel.engine.spreadsheet.cell.api.ReadOnlyCell;

import java.util.List;

public class DisplaySelectedCellEvent {
    private final ReadOnlyCell cell;

    public DisplaySelectedCellEvent(ReadOnlyCell cell) {
        this.cell = cell;
    }

    public ReadOnlyCell getCell() {
        return this.cell;
    }
}
