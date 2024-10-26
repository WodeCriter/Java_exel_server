package exel.eventsys.events.cell;

public class CellSelectedEvent {
    private final String cellId;
    private final int row;
    private final int column;

    public CellSelectedEvent(String cellId, int row, int column) {
        this.cellId = cellId;
        this.row = row;
        this.column = column;
    }

    public String getCellId() {
        return cellId;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
