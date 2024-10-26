package exel.eventsys.events.file;

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
