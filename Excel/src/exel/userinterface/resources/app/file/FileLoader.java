package exel.userinterface.resources.app.file;

import exel.userinterface.util.Constants;
import exel.userinterface.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class FileLoader
{
    public static File selectFileFromPC(Window ownerWindow){
        // Create a new FileChooser instance
        FileChooser fileChooser = new FileChooser();

        // Set the title of the dialog
        fileChooser.setTitle("Open Spreadsheet File");

        // (Optional) Set initial directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Add file extension filters
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Xml Files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show the open file dialog
        return fileChooser.showOpenDialog(ownerWindow);
    }

    public static void uploadFile(String uploadPath, File file){
        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart(file.getName(), file.getName(), RequestBody.create(file, MediaType.parse("text/plain")))
                .build();

        Request request = new Request.Builder()
                .url(Constants.FULL_SERVER_PATH + uploadPath)
                .post(body)
                .build();

        HttpClientUtil.runAsync(request.toString(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> System.out.println("Something went wrong: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();

                if (response.code() == 200)
                    Platform.runLater(() -> System.out.println("File uploaded successfully."));
                else
                    Platform.runLater(() -> System.out.println("Something went wrong: " + responseBody));
            }
        });
    }
}
