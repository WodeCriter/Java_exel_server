package exel.userinterface.resources.app.mainWindows.home;

import engine.util.FileData;
import exel.eventsys.EventBus;
import exel.eventsys.events.LogOutEvent;
import exel.eventsys.events.chat.OpenChatRequestedEvent;
import exel.userinterface.resources.app.general.ControllerWithEventBus;
import exel.userinterface.resources.app.general.FileHelper;
import exel.userinterface.resources.app.mainWindows.home.items.filePermissions.FilePermissionsController;
import exel.userinterface.resources.app.mainWindows.home.items.filesList.FilesListController;
import exel.userinterface.resources.app.mainWindows.home.items.requestsList.RequestsListController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;
import utils.perms.PermissionRequest;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeController extends ControllerWithEventBus
{
    private static final String FILES_PATH = "/exel/userinterface/resources/app/mainWindows/home/items/filesList/FilesList.fxml";
    private static final String REQUESTS_PATH = "/exel/userinterface/resources/app/mainWindows/home/items/requestsList/RequestsList.fxml";
    private static final String PERMISSIONS_TABLE_PATH = "/exel/userinterface/resources/app/mainWindows/home/items/filePermissions/filePermissionsTable.fxml";

    private List<String> activeUsers;
    private List<FileData> savedFiles;
    private List<PermissionRequest> requestsForUser;

    private HomeRefresher refresher;
    private Timer timer;

    private FilesListController filesController;
    private RequestsListController requestsController;
    private FilePermissionsController permissionsController;

    @FXML
    private Menu userNameButton;
    @FXML
    private AnchorPane filesListContainer;
    @FXML
    private AnchorPane requestsListContainer;
    @FXML
    private AnchorPane permissionsTableContainer;
    @FXML
    private ProgressIndicator loadingFileIndicator;

    public void initialize() {
        try
        {
            initializeFilesListController();
            initializeRequestsListController();
            initializePermissionsTableController();
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

    private void initializeRequestsListController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(REQUESTS_PATH));
        Parent requestsList = loader.load();

        requestsController = loader.getController();
        requestsController.setHomeController(this);

        requestsListContainer.getChildren().add(requestsList);
    }

    private void initializePermissionsTableController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PERMISSIONS_TABLE_PATH));
        Parent requestsList = loader.load();

        permissionsController = loader.getController();
        permissionsController.setHomeController(this);

        permissionsTableContainer.getChildren().add(requestsList);
    }

    @Override
    public void setEventBus(EventBus eventBus) {
        super.setEventBus(eventBus);
        filesController.setEventBus(eventBus);
        requestsController.setEventBus(eventBus);
        permissionsController.setEventBus(eventBus);
    }

    @FXML
    void uploadFileListener(ActionEvent event) {
        Window ownerWindow = filesListContainer.getScene().getWindow();
        File loadedFile = FileHelper.selectFileFromPC(ownerWindow);
        if (loadedFile != null)
        {
            FileHelper.uploadFile(loadedFile, this::hideLoadingIndicator);
            loadingFileIndicator.setVisible(true);
            refresher.run();
        }
    }
    @FXML
    void logOutButtonListener(ActionEvent event) {
        eventBus.publish(new LogOutEvent());
    }

    private void setActiveUsers(List<String> activeUsers) {
        if (activeUsers == null || !activeUsers.equals(this.activeUsers))
            this.activeUsers = activeUsers;
    }

    private void setSavedFiles(List<FileData> savedFiles) {
        if (savedFiles == null || !savedFiles.equals(this.savedFiles))
        {
            this.savedFiles = savedFiles;
            filesController.updateFilesList(savedFiles);
        }
    }

    private void hideLoadingIndicator() {
        loadingFileIndicator.setVisible(false);
    }

    private void setFilePermissionsTable(List<PermissionRequest> permissionRequests) {
        permissionsController.updateRequestsTable(permissionRequests);
    }

    private void setPermissionRequests(List<PermissionRequest> permissionRequests) {
        requestsController.updateRequestsList(permissionRequests);
    }

    public void startDataRefresher() {
        refresher = new HomeRefresher(this::setActiveUsers, this::setSavedFiles,
                this::setPermissionRequests, this::setFilePermissionsTable,
                this::hideLoadingIndicator);
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
        //System.out.println("File pressed on Once: " + fileName);
        refresher.setFileForTableFetch(fileName);
        updateData();
    }

    @FXML
    public void handleOpenChatClicked(ActionEvent event){
        eventBus.publish(new OpenChatRequestedEvent());
    }

    public void updateData(){
        refresher.run();
    }

    public void setUsernameButtonText(String username) {
        userNameButton.setText("User: " + username);
    }
}