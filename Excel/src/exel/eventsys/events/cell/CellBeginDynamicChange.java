package exel.eventsys.events.cell;

public class CellBeginDynamicChange {
    private final String coordinate;

    public CellBeginDynamicChange(String cellCoordinate) {
        this.coordinate = cellCoordinate;
    }

    public String getCoordinate() {
        return coordinate;
    }
}
