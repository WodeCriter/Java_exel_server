package exel.eventsys.events.range;

public class RangeSelectedEvent
{
    private String rangeName;

    public RangeSelectedEvent(String rangeName)
    {
        this.rangeName = rangeName;
    }

    public String getRangeName()
    {
        return rangeName;
    }
}
