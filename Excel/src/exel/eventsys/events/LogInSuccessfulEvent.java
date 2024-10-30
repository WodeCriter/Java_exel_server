package exel.eventsys.events;

public class LogInSuccessfulEvent {
    String username;

    public LogInSuccessfulEvent(String homeURL) {
        this.username = homeURL;
    }

    public String getUsername() {
        return username;
    }
}
