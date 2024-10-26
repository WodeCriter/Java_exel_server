package exel.userinterface.resources.app.popups.sort;

import engine.imp.EngineImp;
import exel.eventsys.EventBus;
import exel.eventsys.events.sheet.SortRequestedEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.*;

public class SetSortScreenController
{
    private EventBus eventBus;

    @FXML
    private GridPane gridPane;
    @FXML
    private TextField cell1TextField, cell2TextField;
    private String text1, text2;
    @FXML
    private ComboBox<String> mainColumnComboBox;
    @FXML
    private Label sortByLabel;

    private List<String> possibleColumnChoices = null;
    private List<ComboBox<String>> allComboBoxes;
    private ComboBox<String> lastComboBoxWithValue = null;
    private int rowIndex = 4;  // Track the current row index for new rows

    @FXML
    private void initialize()
    {
        //pickedColumns = new ArrayList<>(5);
        allComboBoxes = new ArrayList<>(5);
        allComboBoxes.add(mainColumnComboBox);
        text1 = text2 = null;

        cell1TextField.textProperty().addListener((observable, oldValue, newValue) -> handleText1Input(newValue));

        cell2TextField.textProperty().addListener((observable, oldValue, newValue) -> handleText2Input(newValue));
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @FXML
    public void addRow()
    {
        shiftRowsDown(rowIndex);
        // Create new content for the new row
        Label guideLabel = new Label("then by");
        ComboBox<String> newComboBox = new ComboBox<>();
        //Label choiceBoxTitle = new Label("Column");
        gridPane.addRow(rowIndex, guideLabel, newComboBox);

        newComboBox.setPrefWidth(mainColumnComboBox.getPrefWidth());
        newComboBox.setPrefHeight(mainColumnComboBox.getPrefHeight());
        newComboBox.setMinWidth(mainColumnComboBox.getMinWidth());
        newComboBox.setPromptText(mainColumnComboBox.getPromptText());
        newComboBox.setOpacity(mainColumnComboBox.getOpacity());

        guideLabel.setPrefWidth(sortByLabel.getPrefWidth());
        guideLabel.setPrefHeight(sortByLabel.getPrefHeight());
        guideLabel.setAlignment(Pos.CENTER_RIGHT);

        newComboBox.setOnAction(this::whenPickingAColumn);
        allComboBoxes.add(newComboBox);
        setNextComboBoxes();

        rowIndex++;
    }

    private void shiftRowsDown(int startRow)
    {
        // Loop through all nodes in the grid pane
        for (Node node : gridPane.getChildren()) {
            // Get the current row index of the node
            Integer row = GridPane.getRowIndex(node);
            if (row == null) {
                row = 0; // If no row index is set, it's assumed to be in row 0
            }

            // If the node is in or below the startRow, move it down one row
            if (row >= startRow) {
                GridPane.setRowIndex(node, row + 1);
            }
        }
    }

    private void handleText1Input(String newText)
    {
        text1 = newText;
        setColumnChoices();
    }

    private void handleText2Input(String newText)
    {
        text2 = newText;
        setColumnChoices();
    }

    private void setColumnChoices()
    {
        possibleColumnChoices = EngineImp.getAllColumnsBetween2Cords(text1, text2);

        if (possibleColumnChoices != null && !possibleColumnChoices.equals(mainColumnComboBox.getItems()))
        {
            mainColumnComboBox.getItems().clear();
            mainColumnComboBox.getItems().addAll(possibleColumnChoices);
            lastComboBoxWithValue = null;
        }
    }
    @FXML
    private void whenPickingAColumn(ActionEvent event)
    {
        lastComboBoxWithValue = (ComboBox<String>) event.getSource();
        setNextComboBoxes();
    }

    private void setNextComboBoxes(){
        if (lastComboBoxWithValue == null)
            return;

        ListIterator<ComboBox<String>> iterator = getIteratorPointingAtNextBoxInList(lastComboBoxWithValue);
        if (iterator != null && iterator.hasNext())
        {
            List<String> choicesWithoutPickedChoice = lastComboBoxWithValue.getItems().stream().
                    filter(column->!column.equals(lastComboBoxWithValue.getValue())).toList();

            ComboBox<String> nextBox = iterator.next();
            nextBox.getItems().clear();
            nextBox.setValue(null);
            nextBox.getItems().addAll(choicesWithoutPickedChoice);
            while (iterator.hasNext())
            {
                nextBox = iterator.next();
                nextBox.getItems().clear();
                nextBox.setValue(null);
            }
        }
    }

    private ListIterator<ComboBox<String>> getIteratorPointingAtNextBoxInList(ComboBox<String> box)
    {
        ListIterator<ComboBox<String>> iterator = allComboBoxes.listIterator();

        while (iterator.hasNext())
        {
            if (iterator.next() == box)
                return iterator;
        }

        return null;
    }

    private List<String> createPickedColumnsList()
    {
        return allComboBoxes.stream()
                .filter(stringComboBox -> stringComboBox != null && stringComboBox.getValue() != null &&
                        !stringComboBox.getValue().isEmpty())
                .map(ComboBoxBase::getValue).toList();
    }

    @FXML
    void whenSortButtonClicked(ActionEvent event)
    {
        if (text1 == null || text1.isEmpty() || text2 == null || text2.isEmpty())
            throw new RuntimeException("Please enter 2 cell coordinates"); //todo: edit exception message

        eventBus.publish(new SortRequestedEvent(text1, text2, createPickedColumnsList()));
    }
}
