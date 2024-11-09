package exel.userinterface.resources.app.home;

import engine.util.FileData;
import exel.userinterface.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;
import utils.perms.PermissionRequest;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Constants.GSON_INSTANCE;
import static utils.Constants.HOME_PAGE;

public class HomeRefresher extends TimerTask
{
    private final Consumer<List<String>> activeUsersConsumer;
    private final Consumer<List<FileData>> savedFilesConsumer;
    private final Consumer<List<PermissionRequest>> pendingRequestsConsumer;
    private final Consumer<List<PermissionRequest>> filePermissionRequestsConsumer;
    private final Runnable hideLoadingFileIndicator;

    private String fileForPermissionTable;
    private int requestNumber;
    private ProgressIndicator loadingFileIndicator;


    public HomeRefresher(Consumer<List<String>> activeUsersConsumer, Consumer<List<FileData>> savedFilesConsumer,
                         Consumer<List<PermissionRequest>> pendingRequestsConsumer,
                         Consumer<List<PermissionRequest>> filePermissionRequestsConsumer,
                         Runnable hideLoadingFileIndicator)
    {
        this.activeUsersConsumer = activeUsersConsumer;
        this.savedFilesConsumer = savedFilesConsumer;
        this.pendingRequestsConsumer = pendingRequestsConsumer;
        this.filePermissionRequestsConsumer = filePermissionRequestsConsumer;
        this.hideLoadingFileIndicator = hideLoadingFileIndicator;
        fileForPermissionTable = "";
        requestNumber = 0;
    }

    @Override
    public void run() {
        updateData();
    }

    public void updateData(){
        String finalURL = HttpUrl
                .parse(HOME_PAGE)
                .newBuilder()
                .addQueryParameter("requestNumber", String.valueOf(requestNumber))
                .addQueryParameter("fileForPermissionTable", fileForPermissionTable)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalURL, response ->
        {
            if ("true".equals(response.header("X-Data-Update-Available")))
            {
                Platform.runLater(() ->
                {
                    updateListsFromJson(response.body());
                    hideLoadingFileIndicator.run();
                });
                requestNumber = Integer.parseInt(response.header("X-Latest-Number"));
            }
        });
    }

    private void updateListsFromJson(ResponseBody body){
        try
        {
            //Map<String, List> jsonHeaderToList = GSON_INSTANCE.fromJson(body.string(), Map.class);
            String json = body.string();
            HomeDataWrapper data = GSON_INSTANCE.fromJson(json, HomeDataWrapper.class);
            activeUsersConsumer.accept(data.getUserNames());
            savedFilesConsumer.accept(data.getFileData());
            pendingRequestsConsumer.accept(data.getPermissionRequests());
            filePermissionRequestsConsumer.accept(data.getPermissionRequestsForFile());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void setFileForTableFetch (String fileForTableFetch){
        fileForPermissionTable = fileForTableFetch;
        requestNumber++;
    }
}
