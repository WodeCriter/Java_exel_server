package exel.userinterface.resources.app.home.items;

import exel.eventsys.events.file.DeleteFileRequestedEvent;
import exel.eventsys.events.file.FileSelectedForOpeningEvent;
import exel.userinterface.resources.app.ControllerWithEventBus;
import exel.userinterface.resources.app.home.HomeController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class FilesListController extends ControllerWithEventBus
{
    private HomeController homeController;
    @FXML
    private ListView<String> filesList;
    @FXML
    private ContextMenu contextMenu;


    // Property to hold the selected item for communication with MainController
    private final ObjectProperty<String> selectedItemProperty = new SimpleObjectProperty<>();

    public void initialize() {

    }

    @FXML
    private void handleFileMouseClicked(MouseEvent event){
        String selectedFile = filesList.getSelectionModel().getSelectedItem();
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
        String selectedFile = filesList.getSelectionModel().getSelectedItem();
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
        String selectedFile = filesList.getSelectionModel().getSelectedItem();
        if (selectedFile == null)
            return;

        handleFileSelectedForOpening(selectedFile);
    }

    @FXML
    private void handleContextMenuDeletePicked(ActionEvent event){
        String selectedFile = filesList.getSelectionModel().getSelectedItem();
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

    public ObjectProperty<String> selectedItemProperty() {
        return selectedItemProperty;
    }

    public void updateFilesList(List<String> filesList) {
        this.filesList.getItems().clear();
        this.filesList.getItems().addAll(filesList);
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
}
