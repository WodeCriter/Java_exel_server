package exel.eventsys.events.range;

public class RangeCreatedEvent
{
    private String rangeName, topLeftCord, bottomRightCord;

    public RangeCreatedEvent(String rangeName, String topLeftCord, String bottomRightCord)
    {
        this.rangeName = rangeName;
        this.topLeftCord = topLeftCord;
        this.bottomRightCord = bottomRightCord;
    }

    public String getRangeName()
    {
        return rangeName;
    }

    public String getTopLeftCord()
    {
        return topLeftCord;
    }

    public String getBottomRightCord()
    {
        return bottomRightCord;
    }
}
