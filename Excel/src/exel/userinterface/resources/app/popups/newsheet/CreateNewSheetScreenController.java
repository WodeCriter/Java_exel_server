package exel.userinterface.resources.app.popups.newsheet;


import exel.eventsys.events.sheet.CreateNewSheetEvent;
import exel.userinterface.resources.app.general.ControllerWithEventBus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class CreateNewSheetScreenController extends ControllerWithEventBus
{
    @FXML
    private TextField textboxSheetName;

    @FXML
    private TextField textboxRowNum;

    @FXML
    private TextField textboxColNum;

    @FXML
    private Button buttonCreateSheet;

    @FXML
    void createNewSheetListener(ActionEvent event) {
        try {
            int rowNum = Integer.parseInt(textboxRowNum.getText());
            int colNum = Integer.parseInt(textboxColNum.getText());
            if (rowNum < 1 || colNum < 1) {
                throw new IllegalArgumentException("Row and column numbers must be at least 1.");
            }
            eventBus.publish(new CreateNewSheetEvent(textboxSheetName.getText(),30,80, rowNum, colNum));
            closeStage();
        } catch (NumberFormatException e) {
            showAlert("Invalid input", "Row and column numbers must be integers.");
        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage(), null);
        }
    }

    private void closeStage() {
        Stage stage = (Stage) buttonCreateSheet.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

