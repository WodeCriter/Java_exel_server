package exel.userinterface.resources.app.home;

import com.google.gson.Gson;
import exel.userinterface.util.http.HttpClientUtil;
import gson.CustomGson;
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

import static utils.Constants.HOME_PAGE;

public class HomeRefresher extends TimerTask
{
    private final Consumer<List<String>> activeUsersConsumer;
    private final Consumer<List<String>> savedFilesConsumer;
    private int requestNumber;

    public HomeRefresher(Consumer<List<String>> activeUsersConsumer, Consumer<List<String>> savedFilesConsumer)
    {
        this.activeUsersConsumer = activeUsersConsumer;
        this.savedFilesConsumer = savedFilesConsumer;
        requestNumber = 0;
    }

    @Override
    public void run() {
        updateData();
    }

    private void updateData(){
        HttpClientUtil.runAsync(HOME_PAGE, "requestNumber", String.valueOf(requestNumber), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("Something went wrong: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                assert response.body() != null;
                String responseBody = response.body().string();

                if (response.code() == 200)
                {
                    if ("true".equals(response.header("X-Data-Update-Available")))
                    {
                        Platform.runLater(() -> updateListsFromJson(responseBody));
                        requestNumber = Integer.parseInt(response.header("X-Latest-Number"));
                    }
                }
                else
                    Platform.runLater(() -> System.out.println("Something went wrong: " + responseBody));
            }
        });
    }

    private void updateListsFromJson(String json){
        Map<String, List<String>> jsonHeaderToList = CustomGson.fromJson(json, Map.class);
        activeUsersConsumer.accept(jsonHeaderToList.get("userNames"));
        savedFilesConsumer.accept(jsonHeaderToList.get("fileNames"));
    }
}
