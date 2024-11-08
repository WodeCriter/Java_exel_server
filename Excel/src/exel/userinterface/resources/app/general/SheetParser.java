package exel.userinterface.resources.app.general;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.spreadsheet.api.ReadOnlySheet;
import engine.spreadsheet.imp.ReadOnlySheetImp;
import engine.spreadsheet.imp.ReadOnlySheetImpAdapter;
import okhttp3.Response;

import java.io.IOException;

public class SheetParser
{
    public static ReadOnlySheet getSheetFromResponse(Response response) throws IOException {
        return getGsonForSheet().fromJson(response.body().string(), ReadOnlySheetImp.class);
    }

    private static Gson getGsonForSheet(){
        return new GsonBuilder()
                .registerTypeAdapter(ReadOnlySheetImp.class, new ReadOnlySheetImpAdapter())
                .create();
    }
}
