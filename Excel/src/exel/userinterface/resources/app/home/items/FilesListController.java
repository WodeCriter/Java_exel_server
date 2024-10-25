package exel.userinterface.resources.app.home.items;

import exel.eventsys.EventBus;
import exel.eventsys.events.FileContentReceivedEvent;
import exel.userinterface.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static utils.Constants.FILES;

public class FilesListController
{
    private EventBus eventBus;
    @FXML
    private ListView<String> filesList;

    // Property to hold the selected item for communication with MainController
    private final ObjectProperty<String> selectedItemProperty = new SimpleObjectProperty<>();

    public void initialize() {
        initializeFileSelectListener();
    }

    private void initializeFileSelectListener(){
        // Listen for selection changes and update selectedItemProperty
        filesList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedItemProperty.set(newSelection);
            handleFileSelected(newSelection);
        });
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    // Method to handle file selected
    private void handleFileSelected(String selectedFileName) {
        String finalURL = HttpUrl
                .parse(FILES)
                .newBuilder()
                .addQueryParameter("fileName", selectedFileName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalURL, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("Something went wrong: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                InputStream responseBody = response.body().byteStream();

                if (response.code() == 200)
                    Platform.runLater(() -> eventBus.publish(new FileContentReceivedEvent(responseBody)));
                else
                    Platform.runLater(() -> System.out.println("Something went wrong: " + response.message()));
            }
        });
    }

    public ObjectProperty<String> selectedItemProperty() {
        return selectedItemProperty;
    }

    public void updateFilesList(List<String> filesList) {
        this.filesList.getItems().clear();
        this.filesList.getItems().addAll(filesList);
    }
}