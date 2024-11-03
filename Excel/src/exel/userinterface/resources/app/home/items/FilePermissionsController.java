package exel.userinterface.resources.app.home.items;

import exel.userinterface.resources.app.ControllerWithEventBus;
import exel.userinterface.resources.app.home.HomeController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.perms.Permission;
import utils.perms.PermissionRequest;
import javafx.scene.control.TableColumn;

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
        userColumn.setCellValueFactory(new PropertyValueFactory<>("getSender"));
        permColumn.setCellValueFactory(new PropertyValueFactory<>("permission"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Create and populate the list with sample data
        requestsList = FXCollections.observableArrayList(
                new PermissionRequest("John Doe", Permission.WRITER, "read"),
                new PermissionRequest("Jane Smith", Permission.READER, "write")
        );

        // Add data to the TableView
        filePermTable.setItems(requestsList);
        
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
}
