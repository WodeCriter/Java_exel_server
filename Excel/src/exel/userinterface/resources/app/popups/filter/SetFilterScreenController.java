package exel.userinterface.resources.app.popups.filter;

import engine.imp.EngineImp;
import exel.eventsys.events.sheet.FilterRequestedEvent;
import exel.userinterface.resources.app.general.ControllerWithEventBus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.*;

public class SetFilterScreenController extends ControllerWithEventBus
{
    @FXML
    private GridPane gridPane;
    @FXML
    private TextField cell1TextField, cell2TextField;
    private String text1, text2;
    @FXML
    private ComboBox<String> mainColumnComboBox;
    @FXML
    private Label sortByLabel;
    @FXML
    private TextArea mainValuesTextArea;
    @FXML
    private Button addColumnButton;
    @FXML
    private Button startFilterButton;

    private List<String> possibleColumnChoices = null;
    private List<ComboBox<String>> allComboBoxes;
    private Map<ComboBox<String>, TextArea> boxToTextAreaMap;
    private ComboBox<String> lastComboBoxWithValue = null;
    private int colIndex = 2;  // Track the current row index for new rows

    @FXML
    private void initialize()
    {
        //pickedColumns = new ArrayList<>(5);
        allComboBoxes = new ArrayList<>(5);
        allComboBoxes.add(mainColumnComboBox);
        boxToTextAreaMap = new HashMap<>();
        boxToTextAreaMap.put(mainColumnComboBox, mainValuesTextArea);
        text1 = text2 = null;

        cell1TextField.textProperty().addListener((observable, oldValue, newValue) -> handleText1Input(newValue));
        cell2TextField.textProperty().addListener((observable, oldValue, newValue) -> handleText2Input(newValue));
    }

    @FXML
    public void addCol()
    {
        shiftColumnsRight(colIndex);
        // Create new content for the new row
        Label guideLabel = new Label("then by");
        ComboBox<String> newComboBox = new ComboBox<>();
        TextArea newTextArea = new TextArea();

        newComboBox.setPrefWidth(mainColumnComboBox.getPrefWidth());
        newComboBox.setMinWidth(mainColumnComboBox.getMinWidth());
//        newComboBox.setPrefHeight(mainColumnComboBox.getPrefHeight());
//        newComboBox.setMinHeight(mainColumnComboBox.getMinHeight());
        newComboBox.setPromptText(mainColumnComboBox.getPromptText());
        newComboBox.setOpacity(mainColumnComboBox.getOpacity());

        newTextArea.setPrefWidth(mainValuesTextArea.getPrefWidth());
        newTextArea.setPrefHeight(mainValuesTextArea.getPrefHeight());
        newTextArea.setPromptText(mainValuesTextArea.getPromptText());

        guideLabel.setPrefWidth(sortByLabel.getPrefWidth());
        guideLabel.setPrefHeight(sortByLabel.getPrefHeight());
        guideLabel.setAlignment(Pos.CENTER_RIGHT);

        //gridPane.addColumn(colIndex, guideLabel, newComboBox, newTextArea);
        gridPane.add(newComboBox, colIndex, 3);
        gridPane.add(newTextArea, colIndex, 4);
        newComboBox.setOnAction(this::whenPickingAColumn);
        allComboBoxes.add(newComboBox);
        boxToTextAreaMap.put(newComboBox, newTextArea);
        setNextComboBoxes();

        colIndex++;
    }

    private void shiftColumnsRight(int startCol) {
        // Loop through all nodes in the grid pane
        for (Node node : gridPane.getChildren()) {

            // Get the current row index of the node
            Integer col = GridPane.getColumnIndex(node);
            if (col == null) {
                col = 0; // If no row index is set, it's assumed to be in row 0
            }

            // If the node is in or below the startRow, move it down one row
            if (col >= startCol) {
                GridPane.setColumnIndex(node, col + 1);
                GridPane.setColumnSpan(node, col + 1);
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
            throw new RuntimeException("Please enter 2 cell coordinates");

        eventBus.publish(new FilterRequestedEvent(text1, text2, parseAllUserInput()));
    }

    private List<String> getValuesToFilterBy(TextArea textArea){
        String allText = textArea.getText();
        return List.of(allText.split("\\n"));
    }

    private Map<String, List<String>> parseAllUserInput(){
        Map<String, List<String>> toReturn = new HashMap<>();
        List<ComboBox<String>> nonEmptyBoxes = allComboBoxes.stream()
                .filter(stringComboBox -> stringComboBox != null && stringComboBox.getValue() != null &&
                        !stringComboBox.getValue().isEmpty()).toList();

        for (ComboBox<String> comboBox : nonEmptyBoxes)
        {
            TextArea textArea = boxToTextAreaMap.get(comboBox);

            if (textArea == null || textArea.getText() == null || textArea.getText().isEmpty())
                throw new IllegalArgumentException("If you've picked a column, you must also type text.");

            toReturn.put(comboBox.getValue(), getValuesToFilterBy(textArea));
        }
        return toReturn;
    }
}
