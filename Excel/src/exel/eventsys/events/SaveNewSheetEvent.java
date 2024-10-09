package exel.eventsys.events;

public class SaveNewSheetEvent {
    private String absolutePath;

    public SaveNewSheetEvent(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
