package exel.userinterface.resources.app.popups.newRange;

import exel.eventsys.events.range.CreateNewRangeEvent;
import exel.userinterface.resources.app.ControllerWithEventBus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateNewRangeScreenController extends ControllerWithEventBus
{
    @FXML
    private TextField textboxRangeName;
    @FXML
    private TextField textboxTopLeft;
    @FXML
    private TextField textboxBottomRight;
    @FXML
    private Button buttonCreateRange;

    @FXML
    void createNewRangeListener(ActionEvent event) {
        try
        {
            String rangeName = textboxRangeName.getText();
            String topLeftCord = textboxTopLeft.getText();
            String bottomRightCord = textboxBottomRight.getText();

            eventBus.publish(new CreateNewRangeEvent(rangeName, topLeftCord.toUpperCase(), bottomRightCord.toUpperCase()));
            closeStage();
        }
        catch (Exception e)
        {
            showAlert(e.getMessage(), null);
        }
    }

    private void closeStage() {
        Stage stage = (Stage) textboxRangeName.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
