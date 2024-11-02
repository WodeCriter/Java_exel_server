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
import utils.perms.Permission;
import utils.perms.PermissionRequest;

import java.util.List;

public class RequestsListController extends ControllerWithEventBus
{
    private HomeController homeController;
    @FXML
    private ListView<PermissionRequest> requestsList;
    @FXML
    private ContextMenu contextMenu;

    private Tooltip tooltip;

    @FXML
    public void initialize() {
        // Populate the ListView with sample data
        //requestsList.getItems().addAll(new PermissionRequest("a", Permission.NONE, "c"));

        //setUpTooltip();
    }

    private void setUpTooltip() {
        // Initialize the tooltip with default settings
        tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.millis(600)); // Optional: set delay for better user experience
        tooltip.setHideDelay(Duration.millis(100));


        // Add mouse hover listeners to display the tooltip
        requestsList.setCellFactory(lv -> {
            var cell = new ListCell<PermissionRequest>() {
                @Override
                protected void updateItem(PermissionRequest item, boolean empty) {
                    super.updateItem(item, empty);

                    // Clear previous content
                    setText(null);
                    setTooltip(null);

                    if (!empty && item != null) {
                        // Set cell text and tooltip for non-empty items
                        setText(item.getSender());
                        setTooltip(tooltip);  // Attach the tooltip to the cell
                    }
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

    private void showTooltip(MouseEvent event, PermissionRequest request) {
        tooltip.setText("For File: " + request.getFileName() + '\n' +
                "Requested Permission: " + request.permission());
        tooltip.show(requestsList, event.getScreenX() + 10, event.getScreenY() + 10);
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void updateRequestsList(List<PermissionRequest> requests) {
        if (requests != null)
        {
            requestsList.getItems().clear();
            requestsList.getItems().addAll(requests);
        }
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
        //String selectedRequest = requestsList.getSelectionModel().getSelectedItem();
    }
}
