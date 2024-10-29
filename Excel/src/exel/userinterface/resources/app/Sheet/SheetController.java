package exel.userinterface.resources.app.Sheet;


import engine.spreadsheet.api.ReadOnlySheet;
import engine.spreadsheet.cell.api.ReadOnlyCell;
import exel.eventsys.EventBus;
import exel.eventsys.events.cell.CellSelectedEvent;
import exel.eventsys.events.cell.CellStyleUpdateEvent;
import exel.eventsys.events.cell.CellsRequestedToBeMarkedEvent;
import exel.eventsys.events.cell.DisplaySelectedCellEvent;
import exel.eventsys.events.sheet.SheetCreatedEvent;
import exel.eventsys.events.sheet.SheetDisplayEvent;
import exel.eventsys.events.sheet.SheetDisplayRefactorEvent;
import exel.userinterface.resources.app.ControllerWithEventBus;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.geometry.Pos;

public class SheetController extends ControllerWithEventBus
{
    private Label currentlySelectedCell = null;
    private List<String> currentlyMarkedCellCords = null;
    private Map<String, Map<String, String>> cellStyles = new HashMap<>();

    @FXML
    private GridPane spreadsheetGrid;

    @FXML
    private ScrollPane sheetScrollPane; // Reference to ScrollPane (optional)

    // Map to store cell labels with their coordinates
    private Map<String, Label> cellLabelMap = new HashMap<>();

    @FXML
    private void initialize() {
    }

    @Override
    public void setEventBus(EventBus eventBus) {
        super.setEventBus(eventBus);
        subscribeToEvents();
    }

    private void subscribeToEvents() {
        eventBus.subscribe(SheetCreatedEvent.class, this::handleSheetCreated);
        eventBus.subscribe(SheetDisplayEvent.class, this::handleSheetDisplay);
        eventBus.subscribe(CellsRequestedToBeMarkedEvent.class, this::handleMarkCell);
        eventBus.subscribe(DisplaySelectedCellEvent.class, this::handleDisplaySelectedCell);
        eventBus.subscribe(SheetDisplayRefactorEvent.class, this::handleSheetRefactor);
        eventBus.subscribe(CellStyleUpdateEvent.class, this::handleCellStyleUpdate);
    }

    private void handleSheetCreated(SheetCreatedEvent event) {
        Platform.runLater(() -> {
            // Clear any existing cells
            spreadsheetGrid.getChildren().clear();
            cellLabelMap.clear();

            // Build the grid based on the event's sheet details
            int numRows = event.getNumOfRows();
            int numCols = event.getNumOfCols();

            // Create column headers (A, B, C, ...)
            for (int col = 1; col <= numCols; col++) {
                Label label = new Label(String.valueOf((char) ('A' + col - 1)));
                label.getStyleClass().add("header-label");
                spreadsheetGrid.add(label, col, 0); // Column headers at row 0
            }

            // Create row headers (1, 2, 3, ...)
            for (int row = 1; row <= numRows; row++) {
                Label label = new Label(String.valueOf(row));
                label.getStyleClass().add("header-label");
                label.setMinWidth(30);
                spreadsheetGrid.add(label, 0, row); // Row headers at column 0
            }

            // Create cells
            for (int row = 1; row <= numRows; row++) {
                for (int col = 1; col <= numCols; col++) {
                    Label cellLabel = new Label();
                    cellLabel.setMinWidth(event.getCellWidth());
                    cellLabel.setMaxWidth(event.getCellWidth());
                    cellLabel.setMinHeight(event.getCellHeight());
                    cellLabel.setMaxHeight(event.getCellHeight());
                    //cellLabel.setStyle("-fx-border-color: lightgrey;");
                    cellLabel.getStyleClass().add("cell-label");
                    final int currentRow = row;
                    final int currentCol = col;

                    cellLabel.setOnMouseClicked(mouseEvent -> handleCellClick(mouseEvent, currentRow, currentCol));
                    // Add any additional cell properties or event handlers here


                    // Store the cell label with its coordinates
                    String cellId = getCellId(row, col);
                    cellLabelMap.put(cellId, cellLabel);

                    spreadsheetGrid.add(cellLabel, col, row);
                }
            }
        });
    }

    private String getCellId(int row, int col) {
        // Convert column number to letter (e.g., 1 -> A, 2 -> B)
        String columnLetter = String.valueOf((char) ('A' + col - 1));
        return columnLetter + row;
    }

    // Method to update a cell value
    private void updateCellUI(String coordinate, String value) {
        Label cellLabel = cellLabelMap.get(coordinate);
        if (cellLabel != null) {
            cellLabel.setText(value);
            Map<String, String> styles = cellStyles.get(coordinate);
            if (styles != null) {
                applyStylesToCell(cellLabel, styles);
            } else {
                // If no styles are stored, reset to default alignment
                cellLabel.setAlignment(Pos.CENTER_LEFT); // or your preferred default alignment
            }
        } else {

        }
    }

    private void handleSheetDisplay(SheetDisplayEvent event) {
        ReadOnlySheet sheet = event.getSheet();

        Platform.runLater(() -> {
            // Iterate through all cells in the sheet
            for (ReadOnlyCell cell : sheet.getCells()) {
                String coordinate = cell.getCoordinate();
                String value = cell.getEffectiveValue();

                // Update the cell in the UI
                updateCellUI(coordinate, value);
            }
        });
    }

    // Event handler for cell clicks
    private void handleCellClick(MouseEvent event, int row, int col) {
        unmarkCellsInList();
        currentlyMarkedCellCords = null;

        String cellId = getCellId(row, col);
        //System.out.println("Cell clicked: " + cellId);

        // Get the clicked cell Label from the map
        Label clickedCell = cellLabelMap.get(cellId);

        // Remove selection from the previously selected cell
        if (currentlySelectedCell != null) {
            currentlySelectedCell.getStyleClass().remove("cell-selected");
        }

        // Add selection to the clicked cell
        if (!clickedCell.getStyleClass().contains("cell-selected")) {
            clickedCell.getStyleClass().add("cell-selected");
            currentlySelectedCell = clickedCell;
        }

        // Create and publish the CellSelectedEvent
        CellSelectedEvent cellSelectedEvent = new CellSelectedEvent(cellId, row, col);
        eventBus.publish(cellSelectedEvent);
    }

    private void handleMarkCell(CellsRequestedToBeMarkedEvent event)
    {
        //todo: If 2 ranges have the same cells, pressing the other range will unmark the currently picked range. Need to fix that.
        unmarkCellsInList();
        List<String> cellsCordsToMark = event.getCellsToMarkCords();
        if (cellsCordsToMark.equals(currentlyMarkedCellCords))
        {
            currentlyMarkedCellCords = null;
            return;
        }

        markAllCellsInList(cellsCordsToMark, "cell-marked-for-range");
        currentlyMarkedCellCords = cellsCordsToMark;
    }

    private void handleDisplaySelectedCell(DisplaySelectedCellEvent event)
    {
        unmarkCellsInList();
        currentlyMarkedCellCords = null;

        List<String> dependsOnCords = event.getCell().getDependsOn();
        List<String> influencingOnCords = event.getCell().getInfluencingOn();
        markAllCellsInList(dependsOnCords, "cell-marked-for-dependency");
        markAllCellsInList(influencingOnCords, "cell-marked-for-influencing");
        currentlyMarkedCellCords = Stream.concat(dependsOnCords.stream(), influencingOnCords.stream()).collect(Collectors.toList());
    }

    private void markAllCellsInList(List<String> cellsCordsToMark, String headerToMarkBy)
    {
        for (String cellId : cellsCordsToMark)
        {
            Label cellLabel = cellLabelMap.get(cellId);
            if (!cellLabel.getStyleClass().contains(headerToMarkBy))
                cellLabel.getStyleClass().add(headerToMarkBy);
        }
    }
    private void handleSheetRefactor(SheetDisplayRefactorEvent event) {
        ReadOnlySheet readOnlySheet = event.getSheet();

        for (Label cellLabel : cellLabelMap.values()) {
            cellLabel.setMinWidth(readOnlySheet.getCellWidth());
            cellLabel.setMaxWidth(readOnlySheet.getCellWidth());
            cellLabel.setMinHeight(readOnlySheet.getCellHeight());
            cellLabel.setMaxHeight(readOnlySheet.getCellHeight());
        }

    }



    private void unmarkCellsInList()
    {
        if (currentlyMarkedCellCords != null)
        {
            for (String cellId : currentlyMarkedCellCords)
                cellLabelMap.get(cellId).getStyleClass().removeIf(str->str.contains("cell-marked"));
        }
    }

    private void handleCellStyleUpdate(CellStyleUpdateEvent event) {
        Platform.runLater(() -> {
            String coordinate = event.getCoordinate();
            Label cellLabel = cellLabelMap.get(coordinate);
            if (cellLabel != null) {
                Map<String, String> styles = cellStyles.getOrDefault(coordinate, new HashMap<>());

                if (event.isClearStyle()) {
                    styles.clear();
                    cellLabel.setStyle("");
                    cellLabel.setAlignment(Pos.CENTER_LEFT); // Default alignment
                } else {
                    if (event.getBackgroundColor() != null) {
                        styles.put("background-color", event.getBackgroundColor());
                    }
                    if (event.getTextColor() != null) {
                        styles.put("text-fill", event.getTextColor());
                    }
                    if (event.getAlignment() != null) {
                        styles.put("alignment", event.getAlignment());
                    }
                }

                cellStyles.put(coordinate, styles);

                // Build and apply the style
                applyStylesToCell(cellLabel, styles);
            } else {
                System.err.println("Cell label not found for coordinate: " + coordinate);
            }
        });
    }

    private void applyStylesToCell(Label cellLabel, Map<String, String> styles) {
        StringBuilder styleBuilder = new StringBuilder();

        for (Map.Entry<String, String> entry : styles.entrySet()) {
            switch (entry.getKey()) {
                case "background-color":
                    styleBuilder.append("-fx-background-color: ").append(entry.getValue()).append(";");
                    break;
                case "text-fill":
                    styleBuilder.append("-fx-text-fill: ").append(entry.getValue()).append(";");
                    break;
                case "alignment":
                    switch (entry.getValue()) {
                        case "left":
                            cellLabel.setAlignment(Pos.CENTER_LEFT);
                            break;
                        case "center":
                            cellLabel.setAlignment(Pos.CENTER);
                            break;
                        case "right":
                            cellLabel.setAlignment(Pos.CENTER_RIGHT);
                            break;
                    }
                    break;
            }
        }

        cellLabel.setStyle(styleBuilder.toString());
    }
}
