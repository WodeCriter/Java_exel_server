package exel.eventsys.events;

public class SaveSheetEvent {
    private String absolutePath;

    public SaveSheetEvent(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
