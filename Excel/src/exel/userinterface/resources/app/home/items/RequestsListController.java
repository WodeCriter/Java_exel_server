package exel.userinterface.resources.app.home.items;

import exel.userinterface.resources.app.ControllerWithEventBus;
import exel.userinterface.resources.app.home.HomeController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class RequestsListController extends ControllerWithEventBus
{
    private HomeController homeController;
    @FXML
    private ListView<String> requestsList;
    @FXML
    private ContextMenu contextMenu;

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
