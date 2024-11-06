package exel.userinterface.resources.app.popups.dynamicAnalsys;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class SliderWindowController {

    @FXML private Slider slider;
    @FXML private Label valueLabel;

    private double stepSize;
    private Stage stage;

    @FXML
    private void initialize() {
        // Listener for slider value changes
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double value = Math.round(newValue.doubleValue() / stepSize) * stepSize;
            valueLabel.setText("Current Value: " + value);
            handleSliderChange(value);
        });
    }

    public void initializeSlider(double min, double max, double step) {
        this.stepSize = step;
        slider.setMin(min);
        slider.setMax(max);
        slider.setValue(min);
        slider.setMajorTickUnit(step);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(step);
        slider.setSnapToTicks(true);
        valueLabel.setText("Current Value: " + min);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleAccept(ActionEvent event) {
        double value = Math.round(slider.getValue() / stepSize) * stepSize;
        handleAcceptAction(value);
        stage.close();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        handleCancelAction();
        stage.close();
    }

    private void handleSliderChange(double value) {
        // Handle the change in the slider value
        System.out.println("Slider value changed to: " + value);
        // Add your code here to handle slider value changes
    }

    private void handleAcceptAction(double value) {
        // Handle the accept action
        System.out.println("Accepted value: " + value);
        // Add your code here to handle acceptance
    }

    private void handleCancelAction() {
        // Handle the cancel action
        System.out.println("Action cancelled");
        // Add your code here to handle cancellation
    }
}
