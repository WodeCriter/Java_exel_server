package exel.eventsys.events.cell;

public class CordsEnteredEvent
{
    private String cord1, cord2;

    public CordsEnteredEvent(String cord1, String cord2)
    {
        this.cord1 = cord1;
        this.cord2 = cord2;
    }

    public String getCord1()
    {
        return cord1;
    }

    public String getCord2()
    {
        return cord2;
    }
}
