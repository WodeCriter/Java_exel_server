package exel;

import engine.api.Engine;
import engine.imp.EngineImp;
import exel.eventsys.EventBus;
import exel.userinterface.UIManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            EventBus eventBus = new EventBus();
            Engine engine = new EngineImp();


            UIManager ui = new UIManager(engine, eventBus, primaryStage);
            ui.showLogin();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);  // This static method launches the JavaFX application
    }
}