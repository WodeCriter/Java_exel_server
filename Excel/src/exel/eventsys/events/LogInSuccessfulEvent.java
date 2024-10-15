package exel.eventsys.events;

public class LogInSuccessfulEvent {
    String homeURL;

    public LogInSuccessfulEvent(String homeURL) {
        this.homeURL = homeURL;
    }

    public String getHomeURL() {
        return homeURL;
    }
}
