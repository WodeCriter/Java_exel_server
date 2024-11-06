package exel.eventsys.events.cell;

public class CellDynamicReturnToNormal {
    private final String coordinate;

    public CellDynamicReturnToNormal(String cellCoordinate) {
        this.coordinate = cellCoordinate;
    }

    public String getCoordinate() {
        return coordinate;
    }

}
