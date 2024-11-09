package exel.eventsys.events.cell;

import java.util.Set;

public class CellBeginDynamicChange {
    private final Set<String> coordinates;

    public CellBeginDynamicChange(Set<String> cellCoordinate) {
        this.coordinates = cellCoordinate;
    }

    public Set<String> getCoordinates() {
        return coordinates;
    }
}
