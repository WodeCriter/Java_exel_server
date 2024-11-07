package exel.eventsys.events.chat;

public class ChatMessageReceivedEvent {
    private final String sender;
    private final String message;

    public ChatMessageReceivedEvent(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
