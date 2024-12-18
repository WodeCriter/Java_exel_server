package exel.userinterface.resources.app.mainWindows.index;


import engine.spreadsheet.api.ReadOnlySheet;
import engine.spreadsheet.cell.api.ReadOnlyCell;
import exel.eventsys.events.*;
import exel.eventsys.events.cell.CellBeginDynamicChange;
import exel.eventsys.events.cell.CellStyleUpdateEvent;
import exel.eventsys.events.cell.CellUpdateEvent;
import exel.eventsys.events.cell.DisplaySelectedCellEvent;
import exel.eventsys.events.chat.OpenChatRequestedEvent;
import exel.eventsys.events.range.DeletedRangeEvent;
import exel.eventsys.events.range.RangeCreatedEvent;
import exel.eventsys.events.range.RangeDeleteEvent;
import exel.eventsys.events.range.RangeSelectedEvent;
import exel.eventsys.events.sheet.*;
import exel.userinterface.resources.app.general.ControllerWithEventBus;
import exel.userinterface.resources.app.general.FileHelper;
import exel.userinterface.resources.app.popups.displaySheet.DisplaySheetController;
import exel.userinterface.resources.app.popups.dynamicAnalsys.SliderInputDialogController;
import exel.userinterface.resources.app.popups.dynamicAnalsys.SliderWindowController;
import exel.userinterface.resources.app.popups.filter.SetFilterScreenController;
import exel.userinterface.resources.app.popups.newRange.CreateNewRangeScreenController;
import exel.userinterface.resources.app.popups.sort.SetSortScreenController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import exel.eventsys.EventBus;
import exel.userinterface.resources.app.popups.newsheet.CreateNewSheetScreenController;
import exel.userinterface.resources.app.Sheet.SheetController;
import javafx.stage.Window;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static exel.userinterface.util.ScreenUtils.showAlert;

public class IndexController extends ControllerWithEventBus
{
    private static final Color ON_MOST_UPDATED_SHEET = Color.web("#1fff2f");
    private static final Color NOT_ON_MOST_UPDATED_SHEET = Color.RED;

    private ReadOnlyCell selectedCell;
    private boolean isSheetLoaded = false;
    private File currentFile;
    private boolean isDarkMode = false;
    private ContextMenu rangeDeleteMenu;
    private boolean isAnimationsEnabled = false;

    private ReadOnlySheet mostRecentSheetFromServer = null;
    private IndexRefresher refresher;
    private Timer timer;
    private int latestDisplayedVersion = 0;

    @FXML private AnchorPane sheetContainer;
    @FXML private Button updateCellButton;
    @FXML private CheckMenuItem cMenItemAnimations;
    @FXML private RadioMenuItem radMenItemLightMode;
    @FXML private RadioMenuItem radMenItemDarkMode;
    //@FXML private Label labelFileLoaded;
    @FXML private Label labelOriginalVal;
    @FXML private Label labelCoordinate;
    @FXML private Label labelCellVersion;
    @FXML private Label labelEditorName;
    @FXML private TextField textFiledOriginalVal;
    @FXML private MenuButton menuButtonSelectVersion;
    @FXML private ListView rangesList;

    @FXML private ToggleGroup themeToggleGroup;
    @FXML private Circle updatedSheetIndicator;
    @FXML private Button loadUpdatedSheetButton;
    @FXML private Button addRangeButton;


    @FXML
    private void initialize() {
        setUpRangeDeleteMenu();

        themeToggleGroup = new ToggleGroup();
        radMenItemLightMode.setToggleGroup(themeToggleGroup);
        radMenItemDarkMode.setToggleGroup(themeToggleGroup);
        // Add a global mouse listener to the scene to hide the context menu when clicking elsewhere
        rangesList.sceneProperty().addListener((obs, oldScene, newScene) ->
        {
            if (newScene != null)
            {
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, this::hideContextMenu);
            }
        });

        menuButtonSelectVersion.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1)
            {
                if (isAnimationsEnabled)
                {
                    triggerCarAnimation();
                }
            }
        });
    }

    @Override
    public void setEventBus(EventBus eventBus) {
        super.setEventBus(eventBus);
        subscribeToEvents();
    }

    private void subscribeToEvents() {
        eventBus.subscribe(DisplaySelectedCellEvent.class, this::handleDisplaySelectedCell);
        eventBus.subscribe(RangeCreatedEvent.class, this::handleNewRangeCreated);
        eventBus.subscribe(DeletedRangeEvent.class, this::handleRangeDeleted);
        eventBus.subscribe(SheetCreatedEvent.class, this::handleSheetCreated);
        eventBus.subscribe(DisplaySheetPopupEvent.class, this::handleDisplaySheetPopup);
        eventBus.subscribe(SheetDisplayEvent.class, this::handleSheetDisplayEvent);
    }

//    @FXML
//    void newFileEventListener(ActionEvent event) {
//        try {
//            // Load the FXML file for the new sheet popup
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/popups/newsheet/CreateNewSheetScreen.fxml"));
//            VBox popupRoot = loader.load();
//
//            // Get the controller and set the EventBus
//            CreateNewSheetScreenController popupController = loader.getController();
//            popupController.setEventBus(eventBus);
//
//            // Create a new stage for the popup
//            Stage popupStage = new Stage();
//            popupStage.setTitle("Create New Sheet");
//            popupStage.initModality(Modality.APPLICATION_MODAL);
//            popupStage.initOwner(((MenuItem) event.getSource()).getParentPopup().getScene().getWindow());  // Set the owner to the current stage
//            Scene popupScene = new Scene(popupRoot, 300, 200);
//
//            applyCurrentTheme(popupScene);
//            popupStage.setScene(popupScene);
//            // Show the popup
//            popupStage.showAndWait();
//        } catch (Exception e) {
//            e.printStackTrace();  // Handle exceptions appropriately
//        }
//    }

    @FXML
    void homeScreenListener(ActionEvent event) {
        //Todo: Implement
    }

    @FXML
    void viewPermChart(ActionEvent event) {
        //Todo: Implement
    }

    private void handleSheetCreated(SheetCreatedEvent event) {
        isSheetLoaded = true;
        currentFile = null;
        menuButtonSelectVersion.getItems().clear();
        rangesList.getItems().clear();
    }

    private void handleDisplaySheetPopup(DisplaySheetPopupEvent event) {
        try
        {
            // Load the FXML file for the display sheet popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/popups/displaySheet/DisplaySheet.fxml"));
            VBox popupRoot = loader.load();

            // Get the controller for the popup
            DisplaySheetController popupController = loader.getController();

            // Get the current sheet data
            ReadOnlySheet sheetData = event.getReadOnlySheet(); // Implement this method to retrieve the sheet

            // Pass the ReadOnlySheet to the popup controller
            popupController.setSheetData(sheetData);

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Display Sheet");
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.initOwner(sheetContainer.getScene().getWindow()); // Set the owner to the current stage

            Scene scene = new Scene(popupRoot);

            applyCurrentTheme(scene);
            popupStage.setScene(scene);

            // Show the popup
            popupStage.showAndWait();

        }
        catch (Exception e)
        {
            e.printStackTrace();  // Handle exceptions appropriately
        }

    }

    public void refreshSheetPlane() {
        try
        {
            setUpDisplaySheet(eventBus);
            applyCurrentTheme(sheetContainer.getScene());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setUpDisplaySheet(EventBus eventBus) {
        // Load the sheet FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/Sheet/Sheet.fxml"));
        Pane sheetRoot;
        try
        {
            sheetRoot = loader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        SheetController controller = loader.getController();
        controller.setEventBus(eventBus);

        // Remove any existing content
        sheetContainer.getChildren().clear();

        // Add the sheet to the container
        sheetContainer.getChildren().add(sheetRoot);

        // Anchor the sheet to all sides
        AnchorPane.setTopAnchor(sheetRoot, 0.0);
        AnchorPane.setBottomAnchor(sheetRoot, 0.0);
        AnchorPane.setLeftAnchor(sheetRoot, 0.0);
        AnchorPane.setRightAnchor(sheetRoot, 0.0);
    }

    private void handleDisplaySelectedCell(DisplaySelectedCellEvent event) {
        this.selectedCell = event.getCell();
        labelCoordinate.setText("Cell: " + selectedCell.getCoordinate());
        labelOriginalVal.setText("original value: " + selectedCell.getOriginalValue());
        labelCellVersion.setText("Cell version:" + selectedCell.getVersion());
        labelEditorName.setText("Last Edited By: " + selectedCell.getEditorName());
        textFiledOriginalVal.setText(selectedCell.getOriginalValue());
    }

    @FXML
    void updateCellButtonListener(ActionEvent event) {
        updateCell();
    }

    @FXML
    void handleKeyPressed(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER)
            updateCell();
    }

    private void updateCell() {
        if (!isSheetLoaded || selectedCell == null)
            return;

        try
        {
            String NewCellValue = textFiledOriginalVal.getText();
            if (!Objects.equals(NewCellValue, selectedCell.getOriginalValue()))
                eventBus.publish(new CellUpdateEvent(selectedCell.getCoordinate(), NewCellValue));

            // Trigger mushroom animation if animations are enabled
            if (isAnimationsEnabled)
            {
                triggerMushroomAnimation();
            }
        }
        catch (Exception e)
        {
            showAlert("Failed to update cell", e.getMessage());
        }
    }

    @FXML
    void addNewRangeButtonListener(ActionEvent event) {
        if (!isSheetLoaded)
            return;

        try
        {
            // Load the FXML file for the new sheet popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/popups/newRange/AddNewRangeScreen.fxml"));
            VBox popupRoot = loader.load();

            CreateNewRangeScreenController controller = loader.getController();
            controller.setEventBus(eventBus);

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Add New Range");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            Scene popupScene = new Scene(popupRoot, 300, 200);

            applyCurrentTheme(popupScene);

            popupStage.setScene(popupScene);
            // Show the popup
            popupStage.showAndWait();
        }
        catch (Exception e)
        {
            e.printStackTrace();  // Handle exceptions appropriately
        }
    }

    private void handleNewRangeCreated(RangeCreatedEvent event) {
        rangesList.getItems().add(event.getRangeName());
    }

    private void handleRangeDeleted(DeletedRangeEvent event) {
        rangesList.getItems().remove(event.getRangeName());
    }

    @FXML
    void rangeSelectedListener(MouseEvent event) {
        String selectedRange = (String) rangesList.getSelectionModel().getSelectedItem();
        if (selectedRange == null)
            return;

        if (event.getButton() == MouseButton.SECONDARY)
            rangeDeleteMenu.show(rangesList, event.getScreenX(), event.getScreenY());
        else
            eventBus.publish(new RangeSelectedEvent(selectedRange));
    }

    private void setUpRangeDeleteMenu() {
        rangeDeleteMenu = new ContextMenu();
        MenuItem deleteRange = new MenuItem("Delete Range");

        deleteRange.setOnAction(event ->
        {
            String selectedRange = (String) rangesList.getSelectionModel().getSelectedItem();
            try
            {
                eventBus.publish(new RangeDeleteEvent(selectedRange));
            }
            catch (Exception e)
            {
                showAlert("Failed to delete range", e.getMessage());
            }
        });

        rangeDeleteMenu.getItems().add(deleteRange);
    }

    private void hideContextMenu(MouseEvent event) {
        // If the context menu is open and the click happens outside the context menu, hide it
        if (rangeDeleteMenu.isShowing())
        {
            rangeDeleteMenu.hide();
        }
    }

    @FXML
    void sortRangeListener(ActionEvent event) {
        if (!isSheetLoaded)
            return;

        try
        {
            // Load the FXML file for the new sheet popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/popups/sort/SetSortScreen.fxml"));
            VBox popupRoot = loader.load();

            SetSortScreenController controller = loader.getController();
            controller.setEventBus(eventBus);

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Sort Range");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((MenuItem) event.getSource()).getParentPopup().getScene().getWindow());
            popupStage.setMinWidth(270);
            popupStage.setMinHeight(220);
            Scene popupScene = new Scene(popupRoot, 300, 600);

            applyCurrentTheme(popupScene);

            popupStage.setScene(popupScene);

            // Show the popup
            popupStage.showAndWait();
        }
        catch (Exception e)
        {
            e.printStackTrace();  // Handle exceptions appropriate
            showAlert("Invalid input", e.getMessage());
        }
    }

    @FXML
    void filterListener(ActionEvent event) {
        if (!isSheetLoaded)
            return;

        try
        {
            // Load the FXML file for the new sheet popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/popups/filter/SetFilterScreen.fxml"));
            VBox popupRoot = loader.load();

            SetFilterScreenController controller = loader.getController();
            controller.setEventBus(eventBus);

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Filter");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((MenuItem) event.getSource()).getParentPopup().getScene().getWindow());  // Set the owner to the current stage
            popupStage.setMinWidth(630);
            popupStage.setMinHeight(270);
            Scene popupScene = new Scene(popupRoot, 1700, 500);

            applyCurrentTheme(popupScene);

            popupStage.setScene(popupScene);

            // Show the popup
            popupStage.showAndWait();
        }
        catch (Exception e)
        {
            e.printStackTrace();  // Handle exceptions appropriate
            showAlert("Invalid input", e.getMessage());
        }
    }

    private void handleSheetDisplayEvent(SheetDisplayEvent sheetEvent) {
        if (sheetEvent.shouldUpdateVersion()) {
            int sheetVersion = sheetEvent.getSheet().getVersion();
            menuButtonSelectVersion.getItems().clear();
            for (int i = 1; i <= sheetVersion; i++) {
                addVersionMenuButton(i);
            }
            latestDisplayedVersion = sheetVersion;
        }
    }

    private void addVersionMenuButton(int versionNum) {
        MenuItem versionItem = new MenuItem("Version " + versionNum);
        versionItem.setOnAction(event -> handleVersionSelected(versionNum));
        menuButtonSelectVersion.getItems().add(versionItem);
    }

    private void handleVersionSelected(int versionId) {
        eventBus.publish(new VersionSelectedEvent(versionId - 1));
    }

    @FXML
    void setHeightListener(ActionEvent event) {
        int newHeight = promptForNumber("Set Height", "Enter new height", "Please enter the new height in pixels:");

        if (newHeight > 0)
        {
            // Apply the new height to relevant cells/labels
            eventBus.publish(new SheetResizeHeightEvent(newHeight));
            // Your logic to set the height goes here
        }

    }

    @FXML
    void setWidthListener(ActionEvent event) {
        int newWidth = promptForNumber("Set Width", "Enter new width", "Please enter the new width in pixels:");

        if (newWidth > 0)
        {
            // Apply the new width to relevant cells/labels
            eventBus.publish(new SheetResizeWidthEvent(newWidth));
            // Your logic to set the width goes here
        }

    }

    private int promptForNumber(String title, String header, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent())
        {
            try
            {
                return Integer.parseInt(result.get()); // Convert the string to an integer
            }
            catch (NumberFormatException e)
            {
                // Show error alert if the input is not a valid number
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Invalid number format");
                alert.setContentText("Please enter a valid number.");
                alert.showAndWait();
            }
        }
        return -1; // Return a default or invalid value if the input was not valid or canceled
    }


    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }


    @FXML
    void formatBGColorListener(ActionEvent event) {
        if (selectedCell == null)
        {
            showAlert("No Cell Selected", "Please select a cell to format.");
            return;
        }

        ColorPicker colorPicker = new ColorPicker();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Select Background Color");
        dialog.getDialogPane().setContent(colorPicker);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            String colorCode = toHexString(colorPicker.getValue());
            CellStyleUpdateEvent styleEvent = new CellStyleUpdateEvent(selectedCell.getCoordinate(), colorCode, null, null, false);
            eventBus.publish(styleEvent);
        }
    }

    @FXML
    void formatTextColorListener(ActionEvent event) {
        if (selectedCell == null)
        {
            showAlert("No Cell Selected", "Please select a cell to format.");
            return;
        }

        ColorPicker colorPicker = new ColorPicker();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Select Text Color");
        dialog.getDialogPane().setContent(colorPicker);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK)
        {
            String colorCode = toHexString(colorPicker.getValue());
            CellStyleUpdateEvent styleEvent = new CellStyleUpdateEvent(selectedCell.getCoordinate(), null, colorCode, null, false);
            eventBus.publish(styleEvent);
        }
    }

    @FXML
    void formatClearStyleListener(ActionEvent event) {
        if (selectedCell == null)
        {
            showAlert("No Cell Selected", "Please select a cell to clear style.");
            return;
        }

        CellStyleUpdateEvent styleEvent = new CellStyleUpdateEvent(selectedCell.getCoordinate(), null, null, null, true);
        eventBus.publish(styleEvent);
    }

    @FXML
    void formatCenterListener(ActionEvent event) {
        if (selectedCell == null)
        {
            showAlert("No Cell Selected", "Please select a cell to format.");
            return;
        }

        CellStyleUpdateEvent styleEvent = new CellStyleUpdateEvent(selectedCell.getCoordinate(), null, null, "center", false);
        eventBus.publish(styleEvent);
    }


    @FXML
    void formatLTRListener(ActionEvent event) {
        if (selectedCell == null)
        {
            showAlert("No Cell Selected", "Please select a cell to format.");
            return;
        }

        CellStyleUpdateEvent styleEvent = new CellStyleUpdateEvent(selectedCell.getCoordinate(), null, null, "left", false);
        eventBus.publish(styleEvent);
    }

    @FXML
    void formatRTLListener(ActionEvent event) {
        if (selectedCell == null)
        {
            showAlert("No Cell Selected", "Please select a cell to format.");
            return;
        }

        CellStyleUpdateEvent styleEvent = new CellStyleUpdateEvent(selectedCell.getCoordinate(), null, null, "right", false);
        eventBus.publish(styleEvent);
    }

    @FXML
    void lightModeListener(ActionEvent event) {
        isDarkMode = false;
        Scene scene = sheetContainer.getScene();
        applyCurrentTheme(scene);
        radMenItemLightMode.setSelected(true);
        radMenItemDarkMode.setSelected(false);
    }

    @FXML
    void darkModeListener(ActionEvent event) {
        isDarkMode = true;
        Scene scene = sheetContainer.getScene();
        applyCurrentTheme(scene);
        radMenItemLightMode.setSelected(false);
        radMenItemDarkMode.setSelected(true);
    }

    private void applyCurrentTheme(Scene scene) {
        String sheetCss = getClass().getResource("/exel/userinterface/resources/app/Sheet/Sheet.css").toExternalForm();
        String darkTheme = getClass().getResource("/exel/userinterface/resources/app/dark-theme.css").toExternalForm();

        // Ensure Sheet.css is always in the stylesheets list
        if (!scene.getStylesheets().contains(sheetCss))
        {
            scene.getStylesheets().add(sheetCss);
        }

        // Remove dark theme if it's applied
        scene.getStylesheets().remove(darkTheme);

        if (isDarkMode)
        {
            // Add the dark theme stylesheet
            scene.getStylesheets().add(darkTheme);
        }

        // Force styles to be reapplied
        scene.getRoot().applyCss();
    }

    @FXML
    void animationsListener(ActionEvent event) {
        isAnimationsEnabled = cMenItemAnimations.isSelected();
    }

    private void triggerCarAnimation() {
        // Load the car image
        Image carImage = new Image(getClass().getResourceAsStream("/exel/userinterface/resources/images/car.png"));

        // Create ImageView
        ImageView carImageView = new ImageView(carImage);

        // Add the carImageView to the sheetContainer
        sheetContainer.getChildren().add(carImageView);

        // Wait for the layout to be calculated
        Platform.runLater(() ->
        {
            // Set initial position (off-screen to the left)
            carImageView.setLayoutX(-carImageView.getImage().getWidth());
            carImageView.setLayoutY(sheetContainer.getHeight() / 2 - carImageView.getImage().getHeight() / 2);

            // Create animation to move the car across the screen
            double endX = sheetContainer.getWidth();

            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), carImageView);
            translateTransition.setFromX(0);
            translateTransition.setToX(endX + carImageView.getImage().getWidth());

            // Remove the car image after animation is finished
            translateTransition.setOnFinished(event ->
            {
                sheetContainer.getChildren().remove(carImageView);
            });

            translateTransition.play();
        });
    }

    private void triggerMushroomAnimation() {

        Image mushroomImage = new Image(getClass().getResourceAsStream("/exel/userinterface/resources/images/mushroom.png"));

        // Create ImageView
        ImageView mushroomImageView = new ImageView(mushroomImage);

        // Scale down the image to 1/16 of its original size
        double scaledWidth = mushroomImage.getWidth() / 16;
        double scaledHeight = mushroomImage.getHeight() / 16;
        mushroomImageView.setFitWidth(scaledWidth);
        mushroomImageView.setFitHeight(scaledHeight);
        mushroomImageView.setPreserveRatio(true); // Maintain aspect ratio

        // Add the mushroomImageView to the sheetContainer
        sheetContainer.getChildren().add(mushroomImageView);

        // Wait for the layout to be calculated
        Platform.runLater(() ->
        {
            // Retrieve the layout position and size of labelOriginalVal
            double labelLayoutX = labelOriginalVal.getLayoutX() - 150;
            double labelLayoutY = labelOriginalVal.getLayoutY();
            double labelWidth = labelOriginalVal.getWidth();
            double labelHeight = labelOriginalVal.getHeight();

            // Set initial position of the mushroom (starting at the left edge of the label)
            double startX = labelLayoutX;
            double startY = labelLayoutY - (labelHeight) - (scaledHeight) - 10;
            mushroomImageView.setLayoutX(startX);
            mushroomImageView.setLayoutY(startY);

            // Define the distance to move: width of the label
            double distance = labelWidth;

            // Create animation to move the mushroom across the label's width and back
            TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(1), mushroomImageView);
            translateTransition.setFromX(0);
            translateTransition.setToX(distance);
            translateTransition.setAutoReverse(true);
            translateTransition.setCycleCount(2); // Forward and back

            // Optionally, add easing for smoother animation
            translateTransition.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

            // Remove the mushroom image after animation is finished
            translateTransition.setOnFinished(event ->
            {
                sheetContainer.getChildren().remove(mushroomImageView);
            });

            translateTransition.play();
        });
    }

    @FXML
    void userGuideListener(ActionEvent event) {
        try
        {
            // Load the FXML file for the about popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/popups/about/About.fxml"));
            VBox popupRoot = loader.load();

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("About Exel");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(sheetContainer.getScene().getWindow()); // Set the owner to the current stage

            // Set the scene
            Scene scene = new Scene(popupRoot, 400, 300);

            // Apply the current theme if necessary
            applyCurrentTheme(scene);

            popupStage.setScene(scene);

            // Show the popup
            popupStage.showAndWait();

        }
        catch (Exception e)
        {
            e.printStackTrace();  // Handle exceptions appropriately
        }
    }

    @FXML
    private void goBackHomeListener(ActionEvent event){
        eventBus.publish(new GoBackHomeEvent());
    }


    @FXML
    public void newFileEventListener(ActionEvent event) {
        try
        {
            // Load the FXML file for the new sheet popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/popups/newsheet/CreateNewSheetScreen.fxml"));
            VBox popupRoot = loader.load();

            // Get the controller and set the EventBus
            CreateNewSheetScreenController popupController = loader.getController();
            popupController.setEventBus(eventBus);

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Create New Sheet");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((MenuItem) event.getSource()).getParentPopup().getScene().getWindow());  // Set the owner to the current stage
            Scene popupScene = new Scene(popupRoot, 300, 200);

            applyCurrentTheme(popupScene);
            popupStage.setScene(popupScene);
            // Show the popup
            popupStage.showAndWait();
        }
        catch (Exception e)
        {
            e.printStackTrace();  // Handle exceptions appropriately
        }
    }

    @FXML
    public void loadFileListener(ActionEvent event) {
        // Retrieve the owner window from a node in the scene
        Window ownerWindow = sheetContainer.getScene().getWindow();

        // Show the open file dialog
        File selectedFile = FileHelper.selectFileFromPC(ownerWindow);

        if (selectedFile != null)
        {
            // Get the absolute path as a String
            String absolutePath = selectedFile.getAbsolutePath();

            try
            {
                // Create a progress bar dialog
                Stage progressStage = new Stage();
                progressStage.initModality(Modality.APPLICATION_MODAL);
                progressStage.initOwner(sheetContainer.getScene().getWindow());

                ProgressBar progressBar = new ProgressBar(0);
                progressBar.setPrefWidth(300);

                VBox vbox = new VBox(10);
                vbox.setAlignment(Pos.CENTER);
                vbox.setPadding(new Insets(20));
                vbox.getChildren().addAll(new Label("Loading..."), progressBar);

                Scene progressScene = new Scene(vbox);

                applyCurrentTheme(progressScene);

                progressStage.setScene(progressScene);
                progressStage.setTitle("Loading");

                // Show the progress bar dialog
                progressStage.show();

                // Use a Timeline to update the progress bar over 3 seconds
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                        new KeyFrame(Duration.seconds(1), new KeyValue(progressBar.progressProperty(), 1))
                );

                timeline.setOnFinished(e ->
                {
                    progressStage.close();
                });

                timeline.play();

                // Start the loading process
                menuButtonSelectVersion.getItems().clear();
                currentFile = selectedFile;
                eventBus.publish(new LoadSheetEvent(absolutePath));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                // Show an error alert to the user
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Load Error");
                alert.setHeaderText("Could not load the file");
                alert.setContentText("An error occurred while loading the file: " + e.getMessage());
                alert.showAndWait();
            }
        }
        else
        {
            // User canceled the file selection; no action needed
        }
    }

    @FXML
    public void saveAsFileListener(ActionEvent event) {
        if (!isSheetLoaded)
            return;

        // Create a new FileChooser instance
        FileChooser fileChooser = new FileChooser();

        // Set the title of the dialog
        fileChooser.setTitle("Save Spreadsheet File As");

        // (Optional) Set initial directory to user's home
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Add file extension filters
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML Files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Retrieve the owner window from a node in the scene
        Window ownerWindow = sheetContainer.getScene().getWindow();

        // Show the save file dialog
        File fileToSave = fileChooser.showSaveDialog(ownerWindow);

        if (fileToSave != null)
        {
            // Ensure the file has the correct extension
            if (!fileToSave.getPath().toLowerCase().endsWith(".xml"))
            {
                fileToSave = new File(fileToSave.getPath() + ".xml");
            }

            // Update the current file reference
            currentFile = fileToSave;

            try
            {
                // **Pass the file path to your engine or handle the saving process**
                // Replace 'SaveSheetEvent' with your actual event or method
                eventBus.publish(new SaveSheetEvent(fileToSave.getAbsolutePath()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                // Show an error alert to the user
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Save Error");
                alert.setHeaderText("Could not save the file");
                alert.setContentText("An error occurred while saving the file: " + e.getMessage());
                alert.showAndWait();
            }
        }
        else
        {
            // User canceled the save dialog; no action needed
        }

    }

    @FXML
    public void saveFileListener(ActionEvent event) {
        if (currentFile != null)
        {
            try
            {
                // **Pass the current file path to your engine or handle the saving process**
                // Replace 'SaveSheetEvent' with your actual event or method
                eventBus.publish(new SaveSheetEvent(currentFile.getAbsolutePath()));

                // Optionally, show a confirmation alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("File Saved");
                alert.setHeaderText(null);
                alert.setContentText("File has been saved successfully.");
                alert.showAndWait();

            }
            catch (Exception e)
            {
                e.printStackTrace();
                // Show an error alert to the user
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Save Error");
                alert.setHeaderText("Could not save the file");
                alert.setContentText("An error occurred while saving the file: " + e.getMessage());
                alert.showAndWait();
            }
        }
        else
        {
            // No current file, perform "Save As"
            saveAsFileListener(event);
        }
    }

    @FXML
    public void handleDynamicAnalysis(ActionEvent event) {
        if (selectedCell == null) {
            return;
        }

        try {
            // Load the range input dialog FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/popups/dynamicAnalsys/SliderInputDialog.fxml"));
            DialogPane dialogPane = loader.load();

            // Create the dialog
            Dialog<Map<String, Double>> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Dynamic Analysis");
            dialog.setHeaderText("Please enter the range and step size.");

            // Get the controller
            SliderInputDialogController controller = loader.getController();


            // Simplify the result converter by checking the ButtonData
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton != null && dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    return controller.getInputValues();
                }
                return null;
            });

            // Show the dialog and wait for the result
            Optional<Map<String, Double>> result = dialog.showAndWait();
            // Initialize and populate pickedCells
            Set<String> pickedCells = controller.getMoreCells();
            pickedCells.add(selectedCell.getCoordinate());

            result.ifPresent(rangeData -> {
                // Open the slider window
                try {
                    FXMLLoader sliderLoader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/popups/dynamicAnalsys/SliderWindow.fxml"));
                    Stage sliderStage = new Stage();
                    sliderStage.setTitle("Adjust Value");
                    sliderStage.setScene(new Scene(sliderLoader.load()));

                    // Publish dynamic change event for the cells
                    eventBus.publish(new CellBeginDynamicChange(pickedCells));

                    // Get the SliderWindowController and set properties
                    SliderWindowController sliderController = sliderLoader.getController();
                    sliderController.setCells(pickedCells);
                    sliderController.initializeSlider(rangeData.get("min"), rangeData.get("max"), rangeData.get("step"));
                    sliderController.setStage(sliderStage);
                    sliderController.setEventBus(eventBus);

                    sliderStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startDataRefresher(String currentlyEditingFileName) {
        refresher = new IndexRefresher(currentlyEditingFileName, this::setMostRecentSheetFromServer);
        timer = new Timer();
        timer.schedule(refresher, 0, 2000);
    }

    public void stopDataRefresher() {
        if (refresher != null && timer != null)
        {
            refresher.cancel();
            timer.cancel();
        }
    }

    private void setMostRecentSheetFromServer(ReadOnlySheet mostRecentSheet) {
        if (!mostRecentSheet.equals(mostRecentSheetFromServer))
        {
            mostRecentSheetFromServer = mostRecentSheet;
            indicateUserIsNotOnUpdatedSheet();
        }
    }

    public void enableEditButtons(boolean enableButtons) {
        updateCellButton.setDisable(!enableButtons);
        addRangeButton.setDisable(!enableButtons);
    }

    private void indicateUserIsNotOnUpdatedSheet() {
        updatedSheetIndicator.setFill(NOT_ON_MOST_UPDATED_SHEET);
        loadUpdatedSheetButton.setDisable(false);
        enableEditButtons(false);
    }

    public void indicateUserIsOnUpdatedSheet() {
        updatedSheetIndicator.setFill(ON_MOST_UPDATED_SHEET);
        loadUpdatedSheetButton.setDisable(true);
        enableEditButtons(true);
    }

    @FXML
    private void handleUpdateSheetButtonPressed(ActionEvent event){
        if (mostRecentSheetFromServer == null)
            throw new RuntimeException();

        indicateUserIsOnUpdatedSheet();
        eventBus.publish(new SheetDisplayEvent(mostRecentSheetFromServer));
        //mostRecentSheetFromServer = null;
    }

    @FXML
    public void handleOpenChatClicked(ActionEvent event){
        eventBus.publish(new OpenChatRequestedEvent());
    }
}
