package exel.eventsys.events.cell;

public class CellUpdateEvent {
    private final String coordinate;
    private final String originalValue;


    public CellUpdateEvent(String cellCoordinate, String OriginalVal) {
        this.coordinate = cellCoordinate;
        this.originalValue = OriginalVal;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public String getOriginalValue() {
        return originalValue;
    }
}
