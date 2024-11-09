package exel;

import engine.api.Engine;
import engine.imp.EngineImp;
import exel.eventsys.EventBus;
import exel.userinterface.UIManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            EventBus eventBus = new EventBus();
            //Engine engine = new EngineImp();
            UIManager ui = new UIManager(null, eventBus, primaryStage); //todo: delete null
            ui.showLogin();

            primaryStage.setOnCloseRequest(event -> {
                // Stop any background tasks
                ui.stopAllBackgroundTasks();
                ui.removeUserFromServer();
                Platform.exit();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);  // This static method launches the JavaFX application
    }
}