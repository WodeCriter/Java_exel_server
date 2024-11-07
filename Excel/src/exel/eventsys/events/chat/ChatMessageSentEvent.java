package exel.eventsys.events.chat;

public class ChatMessageSentEvent {
    private final String message;

    public ChatMessageSentEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
