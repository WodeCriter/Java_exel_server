package exel.eventsys.events;

public class LoadSheetEvent {
    private final String filePath;

    public LoadSheetEvent(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
