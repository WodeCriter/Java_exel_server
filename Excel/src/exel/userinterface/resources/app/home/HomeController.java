package exel.userinterface.resources.app.home;

import exel.eventsys.EventBus;
import exel.eventsys.events.FileContentReceivedEvent;
import exel.userinterface.resources.app.file.FileHelper;
import exel.userinterface.resources.app.home.items.FilesListController;
import exel.userinterface.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
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
    private static final String FILES_PATH = "/exel/userinterface/resources/app/home/items/FilesList.fxml";

    private EventBus eventBus;
    private List<String> activeUsers;
    private List<String> savedFiles;

    private TimerTask refresher;
    private Timer timer;

    @FXML
    private FilesListController filesController;
    @FXML
    private AnchorPane filesListContainer;

    public void initialize() {
        try
        {
            initializeFilesListController();
            setupFilesControllerListener();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void initializeFilesListController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FILES_PATH));
        Parent filesList = loader.load();

        // Retrieve the controller and set it to filesController
        filesController = loader.getController();

        // Now add the files list view to the parent container
        filesListContainer.getChildren().add(filesList);
    }
    private void setupFilesControllerListener(){
        //Whenever a file is pressed, handleItemSelected is activated with the item selected
        filesController.selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                handleFileSelected(newItem);
            }
        });
    }

    // Method to handle file selected
    private void handleFileSelected(String selectedFile) {
        System.out.println("Selected item: " + selectedFile);
        // Add code here to do something with the selected item
    }

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
