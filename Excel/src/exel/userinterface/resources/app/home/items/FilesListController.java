package exel.userinterface.resources.app.home.items;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class FilesListController
{
    @FXML
    private ListView<String> filesList;

    // Property to hold the selected item for communication with MainController
    private final ObjectProperty<String> selectedItemProperty = new SimpleObjectProperty<>();

    public void initialize() {
        filesList.getItems().add("hello");
        filesList.getItems().add("world");

        // Listen for selection changes and update selectedItemProperty
        filesList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedItemProperty.set(newSelection);
        });
    }

    public ObjectProperty<String> selectedItemProperty() {
        return selectedItemProperty;
    }
}
