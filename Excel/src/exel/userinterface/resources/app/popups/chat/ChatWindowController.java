package exel.userinterface.resources.app.popups.chat;


import exel.eventsys.EventBus;
import exel.eventsys.events.chat.ChatMessageReceivedEvent;
import exel.eventsys.events.chat.ChatMessageSentEvent;
import exel.userinterface.resources.app.ControllerWithEventBus;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ChatWindowController extends ControllerWithEventBus {

    @FXML
    private ListView<String> chatListView;

    @FXML
    private TextField messageTextField;

    private EventBus eventBus;

    @Override
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;

        // Subscribe to receive messages
        eventBus.subscribe(ChatMessageReceivedEvent.class, this::handleIncomingMessage);
    }

    @FXML
    private void handleSendMessage() {
        String message = messageTextField.getText().trim();
        if (!message.isEmpty()) {
            // Publish an event with the sent message
            eventBus.publish(new ChatMessageSentEvent(message));

            // Add the message to the chat list as "You"
            chatListView.getItems().add("You: " + message);
            messageTextField.clear();
        }
    }

    private void handleIncomingMessage(ChatMessageReceivedEvent event) {
        Platform.runLater(() -> {
            // Display the received message
            chatListView.getItems().add(event.getSender() + ": " + event.getMessage());
        });
    }
}