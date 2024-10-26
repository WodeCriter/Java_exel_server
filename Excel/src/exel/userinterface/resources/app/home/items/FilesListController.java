package exel.userinterface.resources.app.home.items;

import exel.eventsys.EventBus;
import exel.eventsys.events.FileSelectedForOpeningEvent;
import exel.userinterface.resources.app.home.HomeController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class FilesListController
{
    private EventBus eventBus;
    private HomeController homeController;
    @FXML
    private ListView<String> filesList;


    // Property to hold the selected item for communication with MainController
    private final ObjectProperty<String> selectedItemProperty = new SimpleObjectProperty<>();

    public void initialize() {

    }

    @FXML
    private void handleMouseClick(MouseEvent event){
        String selectedFile = filesList.getSelectionModel().getSelectedItem();
        if (selectedFile == null || homeController == null)
            return;

        if (event.getClickCount() == 1)
            homeController.handleFileSelectedForLooking(selectedFile);
        else if (event.getClickCount() == 2)
            handleFileSelectedForOpening(selectedFile);
    }

    @FXML
    private void handleKeyPress(KeyEvent event) {
        String selectedFile = filesList.getSelectionModel().getSelectedItem();
        if (selectedFile == null || homeController == null)
            return;

        if (event.getCode() == KeyCode.ENTER)
            handleFileSelectedForOpening(selectedFile);
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    // Method to handle file selected
    private void handleFileSelectedForOpening(String selectedFileName) {
        eventBus.publish(new FileSelectedForOpeningEvent(selectedFileName));
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
