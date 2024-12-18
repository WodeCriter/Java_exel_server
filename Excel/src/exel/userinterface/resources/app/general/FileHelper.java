package exel.userinterface.resources.app.general;

import exel.userinterface.util.http.HttpClientUtil;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import okhttp3.*;

import java.io.File;

import static utils.Constants.FILES_PAGE;

public class FileHelper
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

    public static void uploadFile(File file, Runnable hideLoading){
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/xml")))
                .build();

        Request request = new Request.Builder()
                .url(FILES_PAGE)
                .post(body)
                .build();

        HttpClientUtil.runAsync(request, r -> {}, hideLoading);
    }
}
