package exel.eventsys.events;

public class FileSelectedForOpeningEvent
{
    private String fileName;

    public FileSelectedForOpeningEvent(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
