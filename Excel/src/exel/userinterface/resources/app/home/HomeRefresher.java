package exel.userinterface.resources.app.home;

import com.google.gson.Gson;
import exel.userinterface.util.http.HttpClientUtil;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Constants.GSON_INSTANCE;
import static utils.Constants.HOME_PAGE;

public class HomeRefresher extends TimerTask
{
    private final Consumer<List<String>> activeUsersConsumer;
    private final Consumer<List<String>> savedFilesConsumer;
    private int requestNumber;

    public HomeRefresher(Consumer<List<String>> activeUsersConsumer, Consumer<List<String>> savedFilesConsumer)
    {
        this.activeUsersConsumer = activeUsersConsumer;
        this.savedFilesConsumer = savedFilesConsumer;
        requestNumber = 0;
    }

    @Override
    public void run() {
        updateData();
    }

    public void updateData(){
        HttpClientUtil.runAsync(HOME_PAGE, "requestNumber", String.valueOf(requestNumber), response ->
        {
            if ("true".equals(response.header("X-Data-Update-Available")))
            {
                Platform.runLater(() -> updateListsFromJson(response.body()));
                requestNumber = Integer.parseInt(response.header("X-Latest-Number"));
            }
        });
    }

    private void updateListsFromJson(ResponseBody body){
        try
        {
            Map<String, List<String>> jsonHeaderToList = GSON_INSTANCE.fromJson(body.string(), Map.class);
            activeUsersConsumer.accept(jsonHeaderToList.get("userNames"));
            savedFilesConsumer.accept(jsonHeaderToList.get("fileNames"));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
