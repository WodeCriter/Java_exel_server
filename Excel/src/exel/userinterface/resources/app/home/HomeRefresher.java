package exel.userinterface.resources.app.home;

import com.google.gson.Gson;
import exel.userinterface.util.http.HttpClientUtil;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;

import static exel.userinterface.util.Constants.HOME_PAGE;

public class HomeRefresher extends TimerTask
{
    private final Consumer<List<String>> activeUsersConsumer;
    private final Consumer<List<String>> savedFilesConsumer;

    public HomeRefresher(Consumer<List<String>> activeUsersConsumer, Consumer<List<String>> savedFilesConsumer)
    {
        this.activeUsersConsumer = activeUsersConsumer;
        this.savedFilesConsumer = savedFilesConsumer;
    }

    @Override
    public void run() {
        updateData(HOME_PAGE);
    }

    private void updateData(String homeURL){
        HttpClientUtil.runAsync(homeURL, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("Something went wrong: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.code() == 200)
                    Platform.runLater(() -> updateListsFromJson(responseBody));
                else
                    Platform.runLater(() -> System.out.println("Something went wrong: " + responseBody));
            }
        });
    }

    private void updateListsFromJson(String json){
        Gson gson = new Gson();
        Map<String, List<String>> jsonHeaderToList = gson.fromJson(json, Map.class);
        activeUsersConsumer.accept(jsonHeaderToList.get("userNames"));
        savedFilesConsumer.accept(jsonHeaderToList.get("fileNames"));
    }
}
