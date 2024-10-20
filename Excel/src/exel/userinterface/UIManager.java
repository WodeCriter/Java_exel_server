package exel.userinterface;

import exel.engine.api.Engine;
import exel.engine.spreadsheet.api.ReadOnlySheet;
import exel.engine.spreadsheet.cell.api.ReadOnlyCell;
import exel.engine.spreadsheet.range.ReadOnlyRange;
import exel.eventsys.EventBus;
import exel.eventsys.events.*;
import exel.userinterface.resources.app.IndexController;
import exel.userinterface.resources.app.home.HomeController;
import exel.userinterface.resources.app.login.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.List;

public class UIManager {

    private Engine engine;
    private EventBus eventBus;
    private ReadOnlySheet readOnlySheet;
    private IndexController indexController;
    private HomeController homeController;
    private Stage primaryStage;


    public UIManager(Engine engine, EventBus eventBus,Stage primaryStage ) {
        this.engine = engine;
        this.eventBus = eventBus;
        this.primaryStage = primaryStage;
        subscribeToEvents();
    }

    private void subscribeToEvents() {
        // Subscribe to the CreateNewSheetEvent
        eventBus.subscribe(CreateNewSheetEvent.class, this::handleCreateNewSheet);
        eventBus.subscribe(CellSelectedEvent.class, this::handleCellSelected);
        eventBus.subscribe(CellUpdateEvent.class, this::handleCellUpdate);
        eventBus.subscribe(CreateNewRangeEvent.class, this::handleCreateNewRange);
        eventBus.subscribe(RangeSelectedEvent.class, this::handleRangeSelected);
        eventBus.subscribe(RangeDeleteEvent.class, this::handleRangeDelete);
        eventBus.subscribe(SortRequestedEvent.class, this::handleSortRequested);
        eventBus.subscribe(LoadSheetEvent.class, this::handleLoadSheet);
        eventBus.subscribe(FileContentReceivedEvent.class, this::handleLoadSheet);
        eventBus.subscribe(SaveSheetEvent.class, this::handleSaveSheet);
        eventBus.subscribe(VersionSelectedEvent.class, this::handleVersionSelectedEvent);
        eventBus.subscribe(SheetResizeWidthEvent.class, this::handleSheetResizeWidthEvent);
        eventBus.subscribe(SheetResizeHeightEvent.class, this::handleSheetResizeHeightEvent);
        eventBus.subscribe(FilterRequestedEvent.class, this::handleFilterRequested);
        eventBus.subscribe(LogInSuccessfulEvent.class, this::handleLogInSuccessfulEvent);
    }

    private void handleCreateNewSheet(CreateNewSheetEvent event) {
        // Call the engine to create a new sheet based on the event details
        readOnlySheet = engine.createSheet(event.getSheetName(), event.getRows(), event.getCols(), event.getWidth(), event.getHeight());
        indexController.refreshSheetPlane();
        eventBus.publish(new SheetCreatedEvent(
                readOnlySheet.getName(),
                readOnlySheet.getCellHeight(),
                readOnlySheet.getCellWidth(),
                readOnlySheet.getNumOfRows(),
                readOnlySheet.getNumOfCols()));


        eventBus.publish(new SheetDisplayEvent(readOnlySheet));
    }

    private void handleLoadSheet(LoadSheetEvent event){
        try
        {
            readOnlySheet = engine.loadSheet(event.getFilePath());
            loadSheetHelper();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleLoadSheet(FileContentReceivedEvent event) {
        try
        {
            readOnlySheet = engine.loadSheet(event.getFileContent());
            loadSheetHelper();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void loadSheetHelper()
    {
        indexController.refreshSheetPlane();
        eventBus.publish(new SheetCreatedEvent(
                readOnlySheet.getName(),
                readOnlySheet.getCellHeight(),
                readOnlySheet.getCellWidth(),
                readOnlySheet.getNumOfRows(),
                readOnlySheet.getNumOfCols()));

        eventBus.publish(new SheetDisplayEvent(readOnlySheet));
        for (ReadOnlyRange range : readOnlySheet.getRanges()){
            eventBus.publish(new RangeCreatedEvent(range.getRangeName(),
                    range.getTopLeftCord(),
                    range.getBottomRightCord()));
        }
    }

    //added an annotation
    private void handleSaveSheet(SaveSheetEvent event){
        engine.saveXmlFile( event.getAbsolutePath() );
    }

    private void handleCreateNewRange(CreateNewRangeEvent event) {
        engine.addNewRange(event.getRangeName(), event.getTopLeftCord(), event.getBottomRightCord());
        eventBus.publish(new RangeCreatedEvent(event.getRangeName(), event.getTopLeftCord(), event.getBottomRightCord()));
    }

    private void handleCellSelected(CellSelectedEvent event) {
        // Call the engine to create a new sheet based on the event details
        ReadOnlyCell cell = engine.getCellContents(event.getCellId());
        eventBus.publish(new DisplaySelectedCellEvent(cell));
    }

    private void handleCellUpdate(CellUpdateEvent event) {
        try
        {
            engine.updateCellContents(event.getCoordinate(), event.getOriginalValue());
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
        ReadOnlySheet updatedSheet = engine.getSheet();
        eventBus.publish(new SheetDisplayEvent(updatedSheet));
    }

    private void handleRangeSelected(RangeSelectedEvent event)
    {
        List<String> cords = engine.getCordsOfCellsInRange(event.getRangeName());
        eventBus.publish(new CellsRequestedToBeMarkedEvent(cords));
    }

    private void handleRangeDelete(RangeDeleteEvent event)
    {
        engine.deleteRange(event.getRangeName());
        eventBus.publish(new DeletedRangeEvent(event.getRangeName()));
    }

    private void handleSortRequested(SortRequestedEvent event)
    {
        ReadOnlySheet sortedSheet = engine.createSortedSheetFromCords(event.getCord1(), event.getCord2(), event.getPickedColumns());
        eventBus.publish(new DisplaySheetPopupEvent(sortedSheet));
    }

    private void handleFilterRequested(FilterRequestedEvent event)
    {
        ReadOnlySheet sortedSheet = engine.createFilteredSheetFromCords(event.getCord1(), event.getCord2(), event.getPickedData());
        eventBus.publish(new DisplaySheetPopupEvent(sortedSheet));
    }

    private void handleVersionSelectedEvent(VersionSelectedEvent event){
        ReadOnlySheet versionSheet = engine.getSheetOfVersion(event.getVersion() - 1);
        eventBus.publish(new DisplaySheetPopupEvent(versionSheet));
    }

    private void handleSheetResizeWidthEvent(SheetResizeWidthEvent event){
        ReadOnlySheet updatedSheet = engine.changeCellWidth(event.getWidth());
        eventBus.publish(new SheetDisplayRefactorEvent(updatedSheet));

    }

    private void handleSheetResizeHeightEvent(SheetResizeHeightEvent event){
        ReadOnlySheet updatedSheet = engine.changeCellHeight(event.getHeight());
        eventBus.publish(new SheetDisplayRefactorEvent(updatedSheet));
    }

    private void handleLogInSuccessfulEvent(LogInSuccessfulEvent event){
        showHomePage(this.primaryStage);
        homeController.startDataRefresher();
    }

    public void showLogin(Stage primaryStage) {
        try {
            // Assuming the FXML file is named "MainScreen.fxml" and is located in the "app" directory under resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/login/login.fxml"));

            Parent root = loader.load();

            Object controller = loader.getController();

            if (controller instanceof LoginController) {
                ((LoginController) controller).setEventBus(eventBus);
                LoginController loginController = (LoginController) controller;
            }
            // Setting the title of the stage (optional)
            primaryStage.setTitle("Exel");
            Image icon = new Image(getClass().getResourceAsStream("/exel/userinterface/resources/images/Logo.png"));
            primaryStage.getIcons().add(icon);

            // Creating a scene object with the loaded layout
            Scene scene = new Scene(root);

            // Adding the scene to the stage
            primaryStage.setScene(scene);

            // Displaying the contents of the stage
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showHomePage(Stage primaryStage) {
        try {
            // Assuming the FXML file is named "MainScreen.fxml" and is located in the "app" directory under resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/home/home.fxml"));

            Parent root = loader.load();

            Object controller = loader.getController();

            if (controller instanceof HomeController)
            {
                ((HomeController) controller).setEventBus(eventBus);
                homeController = (HomeController) controller;
            }
            // Setting the title of the stage (optional)
            primaryStage.setTitle("Exel");
            Image icon = new Image(getClass().getResourceAsStream("/exel/userinterface/resources/images/Logo.png"));
            primaryStage.getIcons().add(icon);

            // Creating a scene object with the loaded layout
            Scene scene = new Scene(root);

            // Adding the scene to the stage
            primaryStage.setScene(scene);

            // Displaying the contents of the stage
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}