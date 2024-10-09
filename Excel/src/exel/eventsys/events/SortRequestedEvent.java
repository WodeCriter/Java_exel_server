package exel.eventsys.events;

import java.util.List;

public class SortRequestedEvent
{
    private String cord1, cord2;
    private List<String> pickedColumns;

    public SortRequestedEvent(String cord1, String cord2, List<String> pickedColumns)
    {
        this.cord1 = cord1;
        this.cord2 = cord2;
        this.pickedColumns = pickedColumns;
    }

    public String getCord1()
    {
        return cord1;
    }

    public String getCord2()
    {
        return cord2;
    }

    public List<String> getPickedColumns()
    {
        return pickedColumns;
    }
}
