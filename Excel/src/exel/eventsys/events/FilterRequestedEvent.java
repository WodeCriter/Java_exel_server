package exel.eventsys.events;

import java.util.List;
import java.util.Map;

public class FilterRequestedEvent
{
    private String cord1, cord2;
    private Map<String, List<String>> pickedData;

    public FilterRequestedEvent(String cord1, String cord2, Map<String, List<String>> pickedData)
    {
        this.cord1 = cord1;
        this.cord2 = cord2;
        this.pickedData = pickedData;
    }

    public String getCord1()
    {
        return cord1;
    }

    public String getCord2()
    {
        return cord2;
    }

    public Map<String, List<String>> getPickedData()
    {
        return pickedData;
    }
}
