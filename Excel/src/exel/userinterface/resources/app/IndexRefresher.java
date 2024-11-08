package exel.userinterface.resources.app;

import engine.spreadsheet.api.ReadOnlySheet;
import exel.userinterface.util.SheetParser;
import exel.userinterface.util.http.HttpClientUtil;
import okhttp3.HttpUrl;
import okhttp3.Response;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

import static utils.Constants.FULL_SERVER_PATH;

public class IndexRefresher extends TimerTask
{
    private int requestNumber;
    private String currentlyEditingFileName;
    private Consumer<ReadOnlySheet> setMostRecentSheet;
    private boolean isThisFirstRun;

    public IndexRefresher(String currentlyEditingFileName, Consumer<ReadOnlySheet> setMostRecentSheet) {
        this.currentlyEditingFileName = currentlyEditingFileName;
        this.setMostRecentSheet = setMostRecentSheet;
        isThisFirstRun = true;
    }

    @Override
    public void run() {
        updateSheetDataIfNeeded();
    }

    private void updateSheetDataIfNeeded() {
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
                isThisFirstRun = false;
            }
        });
    }

    private void notifyUserIfTheresUpdateToSheet(Response response) throws IOException {
        ReadOnlySheet mostRecentSheet = SheetParser.getSheetFromResponse(response);
        if (mostRecentSheet != null && !isThisFirstRun)
            setMostRecentSheet.accept(mostRecentSheet);
    }
}
