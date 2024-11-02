package exel.userinterface.resources.app.home.items;

import exel.userinterface.resources.app.ControllerWithEventBus;
import exel.userinterface.resources.app.home.HomeController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class RequestsListController extends ControllerWithEventBus
{
    private HomeController homeController;
    @FXML
    private ListView<String> requestsList;
    @FXML
    private ContextMenu contextMenu;

    private Tooltip tooltip;

    @FXML
    public void initialize() {
        // Populate the ListView with sample data
        requestsList.getItems().addAll("hello", "world");

        setUpTooltip();
    }

    private void setUpTooltip() {
        // Initialize the tooltip with default settings
        tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.millis(600)); // Optional: set delay for better user experience
        tooltip.setHideDelay(Duration.millis(100));

        // Add mouse hover listeners to display the tooltip
        requestsList.setCellFactory(lv ->
        {
            var cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item);
                }
            };

            // Show tooltip when hovering over an item
            cell.setOnMouseEntered(event -> {
                if (!cell.isEmpty()) {
                    showTooltip(event, cell.getItem());
                }
            });

            // Hide tooltip when not hovering
            cell.setOnMouseExited(event -> tooltip.hide());

            return cell;
        });
    }

    private void showTooltip(MouseEvent event, String request) {
        tooltip.setText("Requester Name: " + request);
        tooltip.show(requestsList, event.getScreenX() + 10, event.getScreenY() + 10);
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    @FXML
    private void handleRequestMouseClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY)
            contextMenu.show(requestsList, event.getScreenX(), event.getScreenY());
    }

    @FXML
    private void handleContextMenuApprovePicked(ActionEvent event) {
        contextMenuPickedHelper(true);
    }
    @FXML
    private void handleContextMenuDenyPicked(ActionEvent event) {
        contextMenuPickedHelper(false);
    }

    private void contextMenuPickedHelper(Boolean isApproved){
        String selectedRequest = requestsList.getSelectionModel().getSelectedItem();
    }
}
