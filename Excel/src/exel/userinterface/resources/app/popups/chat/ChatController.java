package exel.userinterface.resources.app.popups.chat;


import exel.eventsys.events.chat.ChatMessageSentEvent;
import exel.userinterface.resources.app.general.ControllerWithEventBus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List;
import java.util.Timer;

public class ChatController extends ControllerWithEventBus
{
    @FXML private TextArea chatLineTextArea;
    @FXML private TextArea mainChatLinesTextArea;
    private String userName = "You";

    private ChatRefresher refresher;
    private Timer timer;

    @FXML
    private void sendButtonClicked(ActionEvent event) {
        sendMessage();
    }

    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            sendMessage();
    }

    private void sendMessage() {
        String message = chatLineTextArea.getText().trim();
        if (!message.isEmpty())
        {
            // Publish an event with the sent message
            eventBus.publish(new ChatMessageSentEvent(message));

            // Add the message to the chat list as "You"
            mainChatLinesTextArea.appendText(userName + ": " + message + '\n');
            //chatListView.getItems().add("You: " + message);
            chatLineTextArea.clear();
        }
    }

    private void putMessagesOnScreen(List<String> messages){
        mainChatLinesTextArea.clear();
        messages.forEach(this::appendChatMessage);
        scrollDown();
    }

    private void scrollDown() {
        mainChatLinesTextArea.setScrollTop(Double.MAX_VALUE);
    }

    private void appendChatMessage(String message){
        mainChatLinesTextArea.appendText(message + '\n');
    }

    public void startDataRefresher() {
        refresher = new ChatRefresher(this::putMessagesOnScreen);
        timer = new Timer();
        timer.schedule(refresher, 0, 2000);
    }

    public void stopDataRefresher() {
        if (refresher != null && timer != null)
        {
            refresher.cancel();
            timer.cancel();
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}