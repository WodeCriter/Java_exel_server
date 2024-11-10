package exel.userinterface.resources.app.popups.chat;

import exel.userinterface.util.http.HttpClientUtil;
import javafx.application.Platform;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Constants.*;

public class ChatRefresher extends TimerTask
{
    private Consumer<List<String>> enterMessages;
    private int requestNumber = 0;

    public ChatRefresher(Consumer<List<String>> enterMessages) {
        this.enterMessages = enterMessages;
    }

    @Override
    public void run() {
        updateData();
    }

    public void updateData(){
        String finalURL = HttpUrl
                .parse(CHAT_PAGE)
                .newBuilder()
                .addQueryParameter("requestNumber", String.valueOf(requestNumber))
                .build()
                .toString();

        HttpClientUtil.runAsync(finalURL, response ->
        {
            if ("true".equals(response.header(DATA_UPDATE_HEADER)))
            {
                updateListsFromJson(response.body());
                requestNumber = Integer.parseInt(response.header("X-Latest-Number"));
            }
        });
    }

    private void updateListsFromJson(ResponseBody body){
        try
        {
            if (body == null)
                return;

            String json = body.string();
            List<String> allMessages = GSON_INSTANCE.fromJson(json, LinkedList.class);
            enterMessages.accept(allMessages);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
