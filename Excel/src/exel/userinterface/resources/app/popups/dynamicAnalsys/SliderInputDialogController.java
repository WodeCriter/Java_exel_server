package exel.userinterface.resources.app.popups.dynamicAnalsys;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

public class SliderInputDialogController {
    @FXML private TextField minField;
    @FXML private TextField maxField;
    @FXML private TextField stepField;
    @FXML private DialogPane dialogPane;

    private ButtonType confirmButtonType;
    private ButtonType cancelButtonType;

    @FXML
    private void initialize() {
        // Retrieve the ButtonType instances from the DialogPane
        for (ButtonType bt : dialogPane.getButtonTypes()) {
            if (bt.getButtonData() == ButtonBar.ButtonData.OK_DONE && "Confirm".equals(bt.getText())) {
                confirmButtonType = bt;
            } else if (bt.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE && "Cancel".equals(bt.getText())) {
                cancelButtonType = bt;
            }
        }

        // Now use the retrieved confirmButtonType to lookup the button
        Node confirmButton = dialogPane.lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        // Add listeners for input validation
        minField.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmButton.setDisable(!isInputValid());
        });
        maxField.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmButton.setDisable(!isInputValid());
        });
        stepField.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmButton.setDisable(!isInputValid());
        });
    }

    public Map<String, Double> getInputValues() {
        Map<String, Double> result = new HashMap<>();
        result.put("min", Double.parseDouble(minField.getText()));
        result.put("max", Double.parseDouble(maxField.getText()));
        result.put("step", Double.parseDouble(stepField.getText()));
        return result;
    }

    private boolean isInputValid() {
        try {
            double min = Double.parseDouble(minField.getText());
            double max = Double.parseDouble(maxField.getText());
            double step = Double.parseDouble(stepField.getText());
            return min < max && step > 0 && (max - min) >= step;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Getter for confirmButtonType
    public ButtonType getConfirmButtonType() {
        return confirmButtonType;
    }
}