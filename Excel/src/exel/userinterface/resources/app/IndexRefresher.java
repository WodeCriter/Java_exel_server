package exel.userinterface.resources.app;

import exel.userinterface.util.http.HttpClientUtil;
import javafx.application.Platform;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

import java.util.TimerTask;

import static utils.Constants.FULL_SERVER_PATH;
import static utils.Constants.HOME_PAGE;

public class IndexRefresher extends TimerTask
{
    private int requestNumber;

    @Override
    public void run() {
        String finalURL = HttpUrl
                .parse(FULL_SERVER_PATH + "/index") //todo: add constant
                .newBuilder()
                .addQueryParameter("requestNumber", String.valueOf(requestNumber))
                //.addQueryParameter("fileForPermissionTable", fileForPermissionTable)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalURL, response ->
        {
            if ("true".equals(response.header("X-Data-Update-Available")))
            {
                Platform.runLater(() -> updateSheetFromJson(response.body()));
                requestNumber = Integer.parseInt(response.header("X-Latest-Number"));
            }
        });
    }

    private void updateSheetFromJson(ResponseBody body) {
        //send event so that it will appear on screen that there is an updated sheet
    }
}
