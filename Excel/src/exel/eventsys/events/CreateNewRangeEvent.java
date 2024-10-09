package exel.eventsys.events;

public class CreateNewRangeEvent
{
    private String rangeName, topLeftCord, bottomRightCord;

    public CreateNewRangeEvent(String rangeName, String topLeftCord, String bottomRightCord)
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
