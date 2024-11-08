package exel.userinterface.resources.app.popups.dynamicAnalsys;


import exel.eventsys.EventBus;
import exel.eventsys.events.cell.CellDynamicReturnToNormal;
import exel.eventsys.events.cell.CellDynamicValChange;
import exel.eventsys.events.cell.CellUpdateDynamicValInSheet;
import exel.userinterface.resources.app.general.ControllerWithEventBus;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class SliderWindowController extends ControllerWithEventBus {

    @FXML private Slider slider;
    @FXML private Label valueLabel;

    private double stepSize;
    private double prevVal = 0;
    private Stage stage;
    private String cell;

    @FXML
    private void initialize() {
        // Listener for slider value changes

    }

    @Override
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double value = Math.round(newValue.doubleValue() / stepSize) * stepSize;
            valueLabel.setText("Current Value: " + value);
            handleSliderChange(value);
        });
    }

    public void setCell(String cell) {
        this.cell = cell;
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
        if(value != prevVal){
            eventBus.publish(new CellDynamicValChange(cell,String.valueOf(value)));
            prevVal = value;
        }
    }

    private void handleAcceptAction(double value) {
        eventBus.publish(new CellUpdateDynamicValInSheet(cell,String.valueOf(value)));
    }

    private void handleCancelAction() {
        eventBus.publish(new CellDynamicReturnToNormal(cell));
    }
}
