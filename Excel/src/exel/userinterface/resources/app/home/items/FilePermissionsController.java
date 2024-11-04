package exel.userinterface.resources.app.home.items;

import exel.userinterface.resources.app.ControllerWithEventBus;
import exel.userinterface.resources.app.home.HomeController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.perms.Permission;
import utils.perms.PermissionRequest;
import javafx.scene.control.TableColumn;

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
