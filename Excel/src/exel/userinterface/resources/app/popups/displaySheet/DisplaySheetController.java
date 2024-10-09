package exel.userinterface.resources.app.popups.displaySheet;

import exel.eventsys.events.*;
import exel.engine.spreadsheet.cell.api.ReadOnlyCell;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import exel.engine.spreadsheet.api.ReadOnlySheet;
import exel.eventsys.EventBus;
import exel.userinterface.resources.app.Sheet.SheetController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class DisplaySheetController {

    @FXML
    private Label labelCellId;

    @FXML
    private Label labelCellVal;

    @FXML
    private Label labalCellVersion;

    @FXML
    private AnchorPane sheetContainer;

    private ReadOnlySheet sheetData;

    public void setSheetData(ReadOnlySheet sheetData) {
        this.sheetData = sheetData;
        initializeSheet();
    }

    //subscribe to the events of the sheet
    private void subscribeToEvents(EventBus eventBus) {
        eventBus.subscribe(CellSelectedEvent.class, this::handleCellSelected);
    }

    private void initializeSheet() {
        try {
            // Create a new EventBus for the popup
            EventBus popupEventBus = new EventBus();
            subscribeToEvents(popupEventBus);

            // Load the Sheet.fxml
            FXMLLoader sheetLoader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/Sheet/Sheet.fxml"));
            Pane sheetRoot = sheetLoader.load();

            // Get the SheetController and set the popup EventBus
            SheetController sheetController = sheetLoader.getController();
            sheetController.setEventBus(popupEventBus);

            // Set the sheet into the AnchorPane
            sheetContainer.getChildren().clear();
            sheetContainer.getChildren().add(sheetRoot);
            // Anchor the sheet to all sides
            AnchorPane.setTopAnchor(sheetRoot, 0.0);
            AnchorPane.setBottomAnchor(sheetRoot, 0.0);
            AnchorPane.setLeftAnchor(sheetRoot, 0.0);
            AnchorPane.setRightAnchor(sheetRoot, 0.0);

            // Publish the sheet data to the popup's EventBus
            if (sheetData != null) {
                popupEventBus.publish(new SheetCreatedEvent(
                        sheetData.getName(),
                        sheetData.getCellHeight(),
                        sheetData.getCellWidth(),
                        sheetData.getNumOfRows(),
                        sheetData.getNumOfCols()));
                popupEventBus.publish(new SheetDisplayEvent(sheetData));
            } else {
                // Handle the case where sheetData is null
                System.err.println("Sheet data is null.");
            }

        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }

    private void handleCellSelected(CellSelectedEvent event){
        ReadOnlyCell cell = sheetData.getCell(event.getCellId());
        if (cell != null) {
            setCellIdLabel(cell.getCoordinate());
            setCellValLabel(cell.getOriginalValue());
            setCellVersionLabel(String.valueOf(cell.getVersion()));
        }

    }

    public void setCellIdLabel(String cellId) {
        labelCellId.setText("Cell: " + cellId);
    }

    public void setCellValLabel(String cellValue) {
        labelCellVal.setText("Original Value: " + cellValue);
    }

    public void setCellVersionLabel(String version) {
        labalCellVersion.setText("Cell version: " + version);
    }
}
