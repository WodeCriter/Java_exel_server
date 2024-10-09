package exel.userinterface.resources.app.popups.about;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class AboutController {

    @FXML
    private void handleCloseButton(ActionEvent event) {
        // Close the window
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
