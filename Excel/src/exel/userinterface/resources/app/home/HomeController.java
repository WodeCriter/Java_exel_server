package exel.userinterface.resources.app.home;

import com.google.gson.Gson;
import exel.eventsys.EventBus;
import exel.userinterface.util.Constants;
import exel.userinterface.util.http.HttpClientUtil;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HomeController {
    private EventBus eventBus;
    private List<String> activeUsers = new LinkedList<>();

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void updateUsersList(String homeURL){
        HttpClientUtil.runAsync(Constants.FULL_SERVER_PATH + homeURL, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.code() == 200)
                    Platform.runLater(() -> updateUsersListFromJson(responseBody));
                else
                    Platform.runLater(() -> System.out.println("Something went wrong: " + responseBody));
            }
        });
    }

    private void updateUsersListFromJson(String json){
        Gson gson = new Gson();
        Map<String, List<String>> jsonHeaderToList = gson.fromJson(json, Map.class);
        activeUsers = jsonHeaderToList.get("userNames");
    }

}
