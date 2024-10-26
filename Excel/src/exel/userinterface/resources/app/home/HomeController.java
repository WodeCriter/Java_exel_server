package exel.userinterface.resources.app.home;

import com.sun.javafx.collections.ImmutableObservableList;
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
            //setupFilesControllerListener();
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
            if (newItem != null)
                System.out.println("File selected: " + newItem);
        });
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        filesController.setEventBus(eventBus); 
    }

    @FXML
    void uploadFileListener(ActionEvent event) {
        Window ownerWindow = filesListContainer.getScene().getWindow();
        File loadedFile = FileHelper.selectFileFromPC(ownerWindow);
        FileHelper.uploadFile(loadedFile);
        refresher.run();
    }

    private void setActiveUsers(List<String> activeUsers) {
        if (activeUsers == null || !activeUsers.equals(this.activeUsers))
            this.activeUsers = activeUsers;
    }

    private void setSavedFiles(List<String> savedFiles) {
        if (savedFiles == null || !savedFiles.equals(this.savedFiles))
        {
            this.savedFiles = savedFiles;
            filesController.updateFilesList(savedFiles);
        }
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
