package exel.eventsys.events;

public class DeleteFileRequestedEvent
{
    private String fileName;

    public DeleteFileRequestedEvent(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
