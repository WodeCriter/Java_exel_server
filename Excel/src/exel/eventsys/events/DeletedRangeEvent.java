package exel.eventsys.events;

public class DeletedRangeEvent
{
    private String rangeName;

    public DeletedRangeEvent(String rangeName)
    {
        this.rangeName = rangeName;
    }

    public String getRangeName()
    {
        return rangeName;
    }
}
