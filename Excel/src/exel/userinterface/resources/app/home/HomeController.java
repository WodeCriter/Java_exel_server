package exel.userinterface.resources.app.home;

import com.google.gson.Gson;
import exel.eventsys.EventBus;
import exel.eventsys.events.FileContentReceivedEvent;
import exel.userinterface.resources.app.file.FileHelper;
import exel.userinterface.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Window;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static exel.userinterface.util.Constants.*;

public class HomeController {
    private EventBus eventBus;
    private List<String> activeUsers = new LinkedList<>();
    private List<String> savedFiles = new LinkedList<>();

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void updateSavedData(String homeURL){
        HttpClientUtil.runAsync(FULL_SERVER_PATH + homeURL, new Callback() {
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
        activeUsers = jsonHeaderToList.get("userNames");
        savedFiles = jsonHeaderToList.get("fileNames");
    }

    @FXML
    void uploadFileListener(ActionEvent event) {
        Window ownerWindow = null; //todo: Need to get ownerWindow from an item in the fxml.
        File loadedFile = FileHelper.selectFileFromPC(ownerWindow);
        FileHelper.uploadFile(loadedFile);
    }

    @FXML
    void openFileListener(ActionEvent event) {
        String chosenFileName = null; //todo: Need to get from pressed item in files names list.
        String finalURL = HttpUrl
                .parse(FILES)
                .newBuilder()
                .addQueryParameter("fileName", chosenFileName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalURL, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("Something went wrong: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                InputStream responseBody = response.body().byteStream();

                if (response.code() == 200)
                    Platform.runLater(() -> eventBus.publish(new FileContentReceivedEvent(responseBody)));
                else
                    Platform.runLater(() -> System.out.println("Something went wrong: " + response.message()));
            }
        });
    }
}
