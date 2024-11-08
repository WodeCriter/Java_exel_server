package exel.userinterface.resources.app;

import engine.spreadsheet.api.ReadOnlySheet;
import exel.eventsys.events.sheet.TheresUpdateToSheetEvent;
import exel.userinterface.resources.app.general.ControllerWithEventBus;
import exel.userinterface.resources.app.general.SheetParser;
import exel.userinterface.util.http.HttpClientUtil;
import okhttp3.HttpUrl;
import okhttp3.Response;

import java.io.IOException;
import java.util.TimerTask;

import static utils.Constants.FULL_SERVER_PATH;

public class IndexRefresher extends TimerTask
{
    private int requestNumber;
    private String currentlyEditingFileName;
    private IndexController controller;

    public IndexRefresher(String currentlyEditingFileName, IndexController controller) {
        this.currentlyEditingFileName = currentlyEditingFileName;
        this.controller = controller;
    }

    @Override
    public void run() {
        String finalURL = HttpUrl
                .parse(FULL_SERVER_PATH + "/index") //todo: add constant
                .newBuilder()
                .addQueryParameter("requestNumber", String.valueOf(requestNumber))
                .addQueryParameter("fileName", currentlyEditingFileName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalURL, response ->
        {
            if ("true".equals(response.header("X-Data-Update-Available")))
            {
                notifyUserIfTheresUpdateToSheet(response);
                requestNumber = Integer.parseInt(response.header("X-Latest-Number"));
            }
        });
    }

    private void notifyUserIfTheresUpdateToSheet(Response response) throws IOException {
        ReadOnlySheet mostRecentSheet = SheetParser.getSheetFromResponse(response);
        if (mostRecentSheet != null)
            controller.setMostRecentSheetFromServer(mostRecentSheet);
    }
}
