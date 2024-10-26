package exel.eventsys.events.cell;

import java.util.List;

public class CellsRequestedToBeMarkedEvent
{
    private List<String> cellsMarkedCords;

    public CellsRequestedToBeMarkedEvent(List<String> cellsMarkedCords)
    {
        this.cellsMarkedCords = cellsMarkedCords;
    }

    public List<String> getCellsToMarkCords()
    {
        return cellsMarkedCords;
    }
}
