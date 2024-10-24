package exel.userinterface.resources.app.home;

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

import static utils.Constants.*;

public class HomeController {
    private EventBus eventBus;
    private List<String> activeUsers;
    private List<String> savedFiles;

    private TimerTask refresher;
    private Timer timer;

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
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

    private void setActiveUsers(List<String> activeUsers) {
        if (activeUsers == null || !activeUsers.equals(this.activeUsers))
            this.activeUsers = activeUsers;
    }

    private void setSavedFiles(List<String> savedFiles) {
        if (savedFiles == null || !savedFiles.equals(this.savedFiles))
            this.savedFiles = savedFiles;
    }

    public void startDataRefresher(){
        refresher = new HomeRefresher(this::setActiveUsers, this::setSavedFiles);
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
}
