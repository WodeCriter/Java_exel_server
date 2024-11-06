package exel.eventsys.events.cell;

public class CellDynamicValChange {
    private final String coordinate;
    private final String originalValue;

    public CellDynamicValChange(String cellCoordinate, String OriginalVal) {
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
