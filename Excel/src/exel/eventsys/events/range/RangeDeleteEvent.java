package exel.eventsys.events.range;

public class RangeDeleteEvent
{
    private String rangeName;

    public RangeDeleteEvent(String rangeName)
    {
        this.rangeName = rangeName;
    }

    public String getRangeName()
    {
        return rangeName;
    }
}
