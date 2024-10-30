package exel.userinterface.resources.app.home;

import exel.eventsys.EventBus;
import exel.userinterface.resources.app.ControllerWithEventBus;
import exel.userinterface.resources.app.file.FileHelper;
import exel.userinterface.resources.app.home.items.FilesListController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeController extends ControllerWithEventBus
{
    private static final String FILES_PATH = "/exel/userinterface/resources/app/home/items/FilesList.fxml";

    private List<String> activeUsers;
    private List<String> savedFiles;

    private TimerTask refresher;
    private Timer timer;



    @FXML
    private Menu userNameButton;
    @FXML
    private FilesListController filesController;
    @FXML
    private AnchorPane filesListContainer;
    @FXML
    private ProgressIndicator loadingFileIndicator;

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

        filesController = loader.getController();
        filesController.setHomeController(this);

        filesListContainer.getChildren().add(filesList);
    }

    @Override
    public void setEventBus(EventBus eventBus) {
        super.setEventBus(eventBus);
        filesController.setEventBus(eventBus);
    }

    @FXML
    void uploadFileListener(ActionEvent event) {
        Window ownerWindow = filesListContainer.getScene().getWindow();
        File loadedFile = FileHelper.selectFileFromPC(ownerWindow);

        loadingFileIndicator.setVisible(true);
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

            loadingFileIndicator.setVisible(false);
        }
    }

    public void startDataRefresher() {
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

    public void handleFileSelectedForLooking(String fileName) {
        //todo: need to present users with access to file
        System.out.println("File pressed on Once: " + fileName);
    }

    public ProgressIndicator getProgressIndicator() {
        return loadingFileIndicator;
    }

    public void updateData(){
        refresher.run();
    }

    public void setUsernameButtonText(String username) {
        userNameButton.setText("User: " + username);
    }
}