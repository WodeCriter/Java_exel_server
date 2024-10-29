package exel.userinterface;

import com.google.gson.GsonBuilder;
import engine.api.Engine;
import engine.spreadsheet.api.ReadOnlySheet;
import engine.spreadsheet.cell.api.ReadOnlyCell;
import engine.spreadsheet.imp.ReadOnlySheetImpAdapter;
import engine.spreadsheet.imp.ReadOnlySheetImp;
import engine.spreadsheet.range.ReadOnlyRange;
import exel.eventsys.EventBus;
import exel.eventsys.events.*;
import exel.eventsys.events.cell.CellSelectedEvent;
import exel.eventsys.events.cell.CellUpdateEvent;
import exel.eventsys.events.cell.CellsRequestedToBeMarkedEvent;
import exel.eventsys.events.cell.DisplaySelectedCellEvent;
import exel.eventsys.events.file.DeleteFileRequestedEvent;
import exel.eventsys.events.file.FileSelectedForOpeningEvent;
import exel.eventsys.events.range.*;
import exel.eventsys.events.sheet.*;
import exel.userinterface.resources.app.IndexController;
import exel.userinterface.resources.app.home.HomeController;
import exel.userinterface.resources.app.login.LoginController;
import exel.userinterface.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import static utils.Constants.*;

public class UIManager {

    private Engine engine;
    private EventBus eventBus;
    private ReadOnlySheet readOnlySheet;
    private String currSheetFileName;
    private IndexController indexController;
    private HomeController homeController;
    private Stage primaryStage;

    public UIManager(Engine engine, EventBus eventBus, Stage primaryStage) {
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
        eventBus.subscribe(LoadSheetEvent.class, this::handleLoadSheetFromPath);
        //eventBus.subscribe(FileContentReceivedEvent.class, this::handleLoadSheet);
        eventBus.subscribe(SaveSheetEvent.class, this::handleSaveSheet);
        eventBus.subscribe(VersionSelectedEvent.class, this::handleVersionSelectedEvent);
        eventBus.subscribe(SheetResizeWidthEvent.class, this::handleSheetResizeWidthEvent);
        eventBus.subscribe(SheetResizeHeightEvent.class, this::handleSheetResizeHeightEvent);
        eventBus.subscribe(FilterRequestedEvent.class, this::handleFilterRequested);
        eventBus.subscribe(LogInSuccessfulEvent.class, this::handleLogInSuccessfulEvent);
        eventBus.subscribe(FileSelectedForOpeningEvent.class, this::handleFileSelected);
        eventBus.subscribe(DeleteFileRequestedEvent.class, this::handleFileSelectedForDeletion);
    }

    //TODO: REDUNDANT?
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

    //TODO: REDUNDANT
    private void handleLoadSheetFromPath(LoadSheetEvent event){
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

    //TODO:  REDUNDANT?
    private void handleSaveSheet(SaveSheetEvent event){
        engine.saveXmlFile( event.getAbsolutePath() );
    }


    private void handleCreateNewRange(CreateNewRangeEvent event) {
        try
        {
            // Send a request to the server to update the ranges
            String finalURL = HttpUrl
                    .parse(ADD_RANGE_REQUEST_PATH(currSheetFileName))
                    .newBuilder()
                    .addQueryParameter("rangeName", event.getRangeName())
                    .addQueryParameter("topLeftCord", event.getTopLeftCord())
                    .addQueryParameter("bottomRightCord", event.getBottomRightCord())
                    .build()
                    .toString();

            Request request = new Request.Builder()
                    .url(finalURL)
                    .put(RequestBody.create(null, new byte[0]))
                    .build();

            HttpClientUtil.runAsync(request, response ->
            {
                readOnlySheet = getSheetFromResponse(response);
                Platform.runLater(() -> eventBus.publish(new RangeCreatedEvent(
                        event.getRangeName(),
                        event.getTopLeftCord(),
                        event.getBottomRightCord())));
            }
            );
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void handleFileSelected(FileSelectedForOpeningEvent event){
        String finalURL = HttpUrl
                .parse(VIEW_SHEET_PAGE(event.getFileName()))
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.runAsync(finalURL, response ->
        {
            readOnlySheet = getSheetFromResponse(response);
            currSheetFileName = event.getFileName();
            Platform.runLater(() -> {
                showSheetPage();
                loadSheetHelper();
            });

            homeController.stopDataRefresher();
        });
    }

    private void handleFileSelectedForDeletion(DeleteFileRequestedEvent event){
        String finalURL = HttpUrl
                .parse(DELETE_SHEET_PAGE(event.getFileName()))
                .newBuilder()
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalURL)
                .delete()
                .build();

        HttpClientUtil.runAsync(request, r -> homeController.updateData());
    }

    private void handleCellSelected(CellSelectedEvent event) {
        // Read a specific cell from ReadOnlySheet
        ReadOnlyCell cell = readOnlySheet.getCell(event.getCellId());
        eventBus.publish(new DisplaySelectedCellEvent(cell));
    }

    private void handleCellUpdate(CellUpdateEvent event) {
        String finalURL = HttpUrl
                .parse(UPDATE_CELL_REQUEST_PATH(currSheetFileName))
                .newBuilder()
                .addQueryParameter("coordinate", event.getCoordinate())
                .addQueryParameter("newValue", event.getOriginalValue())
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalURL)
                .put(RequestBody.create(null, new byte[0]))
                .build();

        HttpClientUtil.runAsync(request, response ->
        {
            readOnlySheet = getSheetFromResponse(response);
            Platform.runLater(() -> {
                eventBus.publish(new SheetDisplayEvent(readOnlySheet));
            });
        });
    }

    private void handleRangeSelected(RangeSelectedEvent event) {
        List<String> cords = readOnlySheet.getCoordsInRange(event.getRangeName());
        eventBus.publish(new CellsRequestedToBeMarkedEvent(cords));
    }

    private void handleRangeDelete(RangeDeleteEvent event) {
        try
        {
            String finalURL = HttpUrl
                    .parse(DELETE_RANGE_REQUEST_PATH(currSheetFileName))
                    .newBuilder()
                    .addQueryParameter("rangeName", event.getRangeName())
                    .build()
                    .toString();

            Request request = new Request.Builder()
                    .url(finalURL)
                    .delete()
                    .build();

            HttpClientUtil.runAsync(request, response ->
                    {
                        readOnlySheet = getSheetFromResponse(response);
                        Platform.runLater(() -> eventBus.publish(new DeletedRangeEvent(event.getRangeName())));
                    }
            );
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    //TODO: CHANGE
    private void handleSortRequested(SortRequestedEvent event)
    {
        ReadOnlySheet sortedSheet = engine.createSortedSheetFromCords(event.getCord1(), event.getCord2(), event.getPickedColumns());
        eventBus.publish(new DisplaySheetPopupEvent(sortedSheet));
    }

    //TODO: CHANGE
    private void handleFilterRequested(FilterRequestedEvent event)
    {
        ReadOnlySheet sortedSheet = engine.createFilteredSheetFromCords(event.getCord1(), event.getCord2(), event.getPickedData());
        eventBus.publish(new DisplaySheetPopupEvent(sortedSheet));
    }

    private void handleVersionSelectedEvent(VersionSelectedEvent event){
        //ReadOnlySheet versionSheet = engine.getSheetOfVersion(event.getVersion());
        try
        {
            String finalURL = HttpUrl
                    .parse(VIEW_SHEET_BY_VERSION_REQUEST_PATH(currSheetFileName))
                    .newBuilder()
                    .addQueryParameter("version", String.valueOf(event.getVersion()))
                    .build()
                    .toString();

            HttpClientUtil.runAsync(finalURL, response ->
                    {
                        ReadOnlySheet versionSheet = getSheetFromResponse(response);
                        Platform.runLater(() -> eventBus.publish(new DisplaySheetPopupEvent(versionSheet)));
                    }
            );
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
        //eventBus.publish(new DisplaySheetPopupEvent(versionSheet));
    }

    //TODO: CHANGE TO HTTP
    private void handleSheetResizeWidthEvent(SheetResizeWidthEvent event){
        ReadOnlySheet updatedSheet = engine.changeCellWidth(event.getWidth());
        eventBus.publish(new SheetDisplayRefactorEvent(updatedSheet));

    }

    //TODO: CHANGE TO HTTP
    private void handleSheetResizeHeightEvent(SheetResizeHeightEvent event){
        ReadOnlySheet updatedSheet = engine.changeCellHeight(event.getHeight());
        eventBus.publish(new SheetDisplayRefactorEvent(updatedSheet));
    }


    private void handleLogInSuccessfulEvent(LogInSuccessfulEvent event){
        showHomePage();
        homeController.startDataRefresher();
    }

    public void showLogin() {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/login/login.fxml"));
            Parent root = loader.load();
            Object controller = loader.getController();

            if (controller instanceof LoginController) {
                LoginController loginController = (LoginController) controller;
                loginController.setEventBus(eventBus);
            }

            setPrimaryStage(root);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void showHomePage() {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/home/home.fxml"));
            Parent root = loader.load();
            Object controller = loader.getController();

            if (controller instanceof HomeController)
            {
                homeController = (HomeController) controller;
                homeController.setEventBus(eventBus);
            }
            setPrimaryStage(root);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void showSheetPage(){
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/exel/userinterface/resources/app/Index.fxml"));
            Parent root = loader.load();
            Object controller = loader.getController();

            if (controller instanceof IndexController)
            {
                indexController = (IndexController) controller;
                indexController.setEventBus(eventBus);
            }
            setPrimaryStage(root);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setPrimaryStage(Parent root) {
        // Setting the title of the stage (optional)
        primaryStage.setTitle(MAIN_TITLE);
        Image icon = new Image(getClass().getResourceAsStream("/exel/userinterface/resources/images/Logo.png"));
        primaryStage.getIcons().add(icon);

        // Creating a scene object with the loaded layout
        Scene scene = new Scene(root);

        // Adding the scene to the stage
        primaryStage.setScene(scene);

        // Displaying the contents of the stage
        primaryStage.show();
    }

    private Gson getGsonForSheet(){
        return new GsonBuilder()
                .registerTypeAdapter(ReadOnlySheetImp.class, new ReadOnlySheetImpAdapter())
                .create();
    }

    private ReadOnlySheet getSheetFromResponse(Response response) throws IOException{
        return getGsonForSheet().fromJson(response.body().string(), ReadOnlySheetImp.class);
    }
}