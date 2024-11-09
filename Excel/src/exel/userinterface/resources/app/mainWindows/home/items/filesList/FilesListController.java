package exel.userinterface.resources.app.mainWindows.home.items.filesList;

import engine.util.FileData;
import exel.eventsys.events.file.DeleteFileRequestedEvent;
import exel.eventsys.events.file.FilePermissionRequestedEvent;
import exel.eventsys.events.file.FileSelectedForOpeningEvent;
import exel.userinterface.resources.app.general.ControllerWithEventBus;
import exel.userinterface.resources.app.mainWindows.home.HomeController;
import exel.userinterface.resources.app.mainWindows.home.items.TooltipUtil;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class FilesListController extends ControllerWithEventBus
{
    private HomeController homeController;
    @FXML
    private ListView<FileData> filesList;
    @FXML
    private ContextMenu contextMenu;
    private TooltipUtil<FileData> tooltip;

    // Property to hold the selected item for communication with MainController
    private final ObjectProperty<String> selectedItemProperty = new SimpleObjectProperty<>();

    public void initialize() {
        setUpTooltipAndColors();
        //setUpFilesListListener();
    }

    private void setUpFilesListListener() {
        filesList.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                filesList.getSelectionModel().clearSelection();
            }
        });
    }

    private void setUpTooltipAndColors() {
        new TooltipUtil<>(filesList,
                fileData -> "Owner: " + fileData.getOwnerName() + '\n' +
                        "Size: " + fileData.getNumOfCols() + 'x' + fileData.getNumOfRows() + '\n' +
                        "Permission: " + fileData.getUserPermission(),

                fileData -> { // Style based on user permission
                    switch (fileData.getUserPermission()) {
                        case READER:
                            return "reader-item";
                        case WRITER:
                            return "writer-item";
                        case OWNER:
                            return "owner-item";
                        default: return ""; // No style for NONE
                    }
                });
    }

    @FXML
    private void handleFileMouseClicked(MouseEvent event){
        String selectedFile = filesList.getSelectionModel().getSelectedItem().getFilename();
        if (selectedFile == null || homeController == null)
            return;

        if (event.getClickCount() == 1)
        {
            if (event.getButton() == MouseButton.SECONDARY)
                contextMenu.show(filesList, event.getScreenX(), event.getScreenY());
            else
                homeController.handleFileSelectedForLooking(selectedFile);
        }
        else if (event.getClickCount() == 2)
            handleFileSelectedForOpening(selectedFile);
    }

    @FXML
    private void handleFileKeyPress(KeyEvent event) {
        String selectedFile = filesList.getSelectionModel().getSelectedItem().getFilename();
        if (selectedFile == null)
            return;

        switch (event.getCode())
        {
            case ENTER:
                handleFileSelectedForOpening(selectedFile);
                break;
            case DELETE:
            case BACK_SPACE:
                handleFileSelectedForDeletion(selectedFile);
        }
    }

    @FXML
    private void handleContextMenuOpenPicked(ActionEvent event) {
        String selectedFile = filesList.getSelectionModel().getSelectedItem().getFilename();
        if (selectedFile == null)
            return;

        handleFileSelectedForOpening(selectedFile);
    }

    @FXML
    private void handleContextMenuDeletePicked(ActionEvent event){
        String selectedFile = filesList.getSelectionModel().getSelectedItem().getFilename();
        if (selectedFile == null)
            return;

        handleFileSelectedForDeletion(selectedFile);
    }

    private void handleFileSelectedForOpening(String selectedFileName) {
        eventBus.publish(new FileSelectedForOpeningEvent(selectedFileName));
    }

    private void handleFileSelectedForDeletion(String selectedFileName) {
        eventBus.publish(new DeleteFileRequestedEvent(selectedFileName));
    }

    @FXML
    private void handleFileReaderPermRequest(ActionEvent event){
        String selectedFile = filesList.getSelectionModel().getSelectedItem().getFilename();
        eventBus.publish(new FilePermissionRequestedEvent("READER", selectedFile));
    }

    @FXML
    private void handleFileWriterPermRequest(ActionEvent event){
        String selectedFile = filesList.getSelectionModel().getSelectedItem().getFilename();
        eventBus.publish(new FilePermissionRequestedEvent("WRITER", selectedFile));
    }

    public ObjectProperty<String> selectedItemProperty() {
        return selectedItemProperty;
    }

    public void updateFilesList(List<FileData> filesList) {
        this.filesList.getItems().clear();
        this.filesList.getItems().addAll(filesList);
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
}
