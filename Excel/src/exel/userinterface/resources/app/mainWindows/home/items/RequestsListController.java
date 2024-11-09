package exel.userinterface.resources.app.mainWindows.home.items;

import exel.eventsys.events.ApproveOrDenyRequestPickedEvent;
import exel.userinterface.resources.app.general.ControllerWithEventBus;
import exel.userinterface.resources.app.mainWindows.home.HomeController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import utils.perms.PermissionRequest;

import java.util.List;

public class RequestsListController extends ControllerWithEventBus
{
    private HomeController homeController;
    @FXML
    private ListView<PermissionRequest> requestsList;
    @FXML
    private ContextMenu contextMenu;
    private TooltipUtil<PermissionRequest> toolTip;

    @FXML
    public void initialize() {
        setUpTooltip();
    }

    private void setUpTooltip() {
        toolTip = new TooltipUtil<>(requestsList, request ->
                "For File: " + request.getFileName() + '\n' +
                        "Requested Permission: " + request.permission());
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

    private void contextMenuPickedHelper(Boolean toApprove){
        PermissionRequest selectedRequest = requestsList.getSelectionModel().getSelectedItem();
        eventBus.publish(new ApproveOrDenyRequestPickedEvent(selectedRequest, toApprove));
    }
}
