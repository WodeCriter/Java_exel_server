package exel.userinterface.resources.app.mainWindows.home.items.filePermissions;

import exel.userinterface.resources.app.general.ControllerWithEventBus;
import exel.userinterface.resources.app.mainWindows.home.HomeController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import utils.perms.PermissionRequest;
import javafx.scene.control.TableColumn;
import utils.perms.Status;

import java.util.List;

public class FilePermissionsController extends ControllerWithEventBus
{
    @FXML
    private TableView<PermissionRequest> filePermTable;
    private HomeController homeController;

    @FXML
    private TableColumn<PermissionRequest, String> userColumn;
    @FXML
    private TableColumn<PermissionRequest, String> permColumn;
    @FXML
    private TableColumn<PermissionRequest, String> statusColumn;
    private ObservableList<PermissionRequest> requestsList;


    public void initialize(){
        userColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender()));
        permColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().permission().toString()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().status().toString()));
        setUpRowColors();
    }

    private void setUpRowColors() {
        filePermTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(PermissionRequest item, boolean empty) {
                super.updateItem(item, empty);

                // Clear any previous style
                getStyleClass().removeAll("accepted-row", "denied-row", "pending-row");

                if (item != null && !empty)
                {
                    if (item.status() == Status.ACCEPTED)
                        getStyleClass().add("accepted-row");
                    else if (item.status() == Status.DENIED)
                        getStyleClass().add("denied-row");
                    else if (item.status() == Status.PENDING)
                        getStyleClass().add("pending-row");
                }
            }
        });
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void updateRequestsTable(List<PermissionRequest> permissionRequests){
        // Clear existing items
        filePermTable.getItems().clear();

        // Create an ObservableList from the provided list
        ObservableList<PermissionRequest> observableRequests = FXCollections.observableArrayList(permissionRequests);

        // Set the new items in the TableView
        filePermTable.setItems(observableRequests);
    }
}
