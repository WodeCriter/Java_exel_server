package exel.userinterface.resources.app.popups.dynamicAnalsys;


import exel.eventsys.EventBus;
import exel.eventsys.events.cell.CellDynamicReturnToNormal;
import exel.eventsys.events.cell.CellDynamicValChange;
import exel.eventsys.events.cell.CellUpdateDynamicValInSheet;
import exel.userinterface.resources.app.general.ControllerWithEventBus;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SliderWindowController extends ControllerWithEventBus {

    //@FXML private Slider slider;
    @FXML private GridPane slidersGridPane;
    //@FXML private Label valueLabel;

    private double stepSize;
    private double prevVal = 0;
    private Stage stage;
    //private String cell;
    private Set<String> cells;
    private Map<String, Slider> cellToSliderMap;
    private Map<String, Label> cellToLabelMap;
    private int latestRow = 0;

    @FXML
    private void initialize() {
        cellToSliderMap = new HashMap<>();
        cellToLabelMap = new HashMap<>();
        //cells = new HashSet<>();
    }

    @Override
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;

        for (String cell : cells)
        {
            Slider slider = cellToSliderMap.get(cell);
            Label valueLabel = cellToLabelMap.get(cell);

            slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double value = Math.round(newValue.doubleValue() / stepSize) * stepSize;
                valueLabel.setText("Current Value: " + value);
                handleSliderChange(cell, value);
            });
        }
    }

//    public void setCell(String cell) {
//        this.cell = cell;
//    }

    public void setCells(Set<String> cells) {
        this.cells = cells;
    }

    public void initializeSlider(double min, double max, double step) {
        this.stepSize = step;
        for (String cell : cells)
        {
            Slider slider = new Slider();
            slider.setMin(min);
            slider.setMax(max);
            slider.setValue(min);
            slider.setMajorTickUnit(step);
            slider.setMinorTickCount(0);
            slider.setBlockIncrement(step);
            slider.setSnapToTicks(true);

            Label valueLabel = new Label("Current Value: " + min);

            slidersGridPane.addRow(latestRow++, slider);
            slidersGridPane.addRow(latestRow++, valueLabel);

            cellToSliderMap.put(cell, slider);
            cellToLabelMap.put(cell, valueLabel);
            //slidersGridPane.addRow(latestRow++);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleAccept(ActionEvent event) {
        for (String cell : cells)
        {
            Slider slider = cellToSliderMap.get(cell);
            double value = Math.round(slider.getValue() / stepSize) * stepSize;
            handleAcceptAction(cell, value);
        }
        stage.close();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        handleCancelAction();
        stage.close();
    }

    private void handleSliderChange(String cell, double value) {
        if(value != prevVal){
            eventBus.publish(new CellDynamicValChange(cell, String.valueOf(value)));
            prevVal = value;
        }
    }

    private void handleAcceptAction(String cell, double value) {
        eventBus.publish(new CellUpdateDynamicValInSheet(cell, String.valueOf(value)));
    }

    private void handleCancelAction() {
        eventBus.publish(new CellDynamicReturnToNormal(""));
    }
}
