package exel.eventsys.events;

public class FileSelectedEvent {
    private String fileName;

    public FileSelectedEvent(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
