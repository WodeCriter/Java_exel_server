package exel.eventsys.events;

public class VersionSelectedEvent {
    private int version;

    public VersionSelectedEvent(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
