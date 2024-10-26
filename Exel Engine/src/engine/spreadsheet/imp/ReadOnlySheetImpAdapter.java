package engine.spreadsheet.imp;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import engine.spreadsheet.cell.api.ReadOnlyCell;
import engine.spreadsheet.cell.imp.ReadOnlyCellImp;
import engine.spreadsheet.range.ReadOnlyRange;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import static utils.Constants.GSON_INSTANCE;

public class ReadOnlySheetImpAdapter extends TypeAdapter<ReadOnlySheetImp> {
    private final TypeAdapter<ReadOnlyCellImp> cellAdapter;

    public ReadOnlySheetImpAdapter() {
        // Initialize adapter for ReadOnlyCellImp
        this.cellAdapter = GSON_INSTANCE.getAdapter(ReadOnlyCellImp.class);
    }

    @Override
    public void write(JsonWriter out, ReadOnlySheetImp sheet) throws IOException {
        // Serialization logic if needed
    }

    @Override
    public ReadOnlySheetImp read(JsonReader in) throws IOException {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(in).getAsJsonObject();

        int version = jsonObject.get("version").getAsInt();
        String name = jsonObject.get("name").getAsString();
        int numOfCols = jsonObject.get("numOfCols").getAsInt();
        int numOfRows = jsonObject.get("numOfRows").getAsInt();
        int cellWidth = jsonObject.get("cellWidth").getAsInt();
        int cellHeight = jsonObject.get("cellHeight").getAsInt();

        // Deserialize `cells` list using ReadOnlyCellImpAdapter
        Gson gson = new Gson();
        Type cellListType = new TypeToken<List<ReadOnlyCellImp>>() {}.getType();
        List<ReadOnlyCell> cells = gson.fromJson(jsonObject.get("cells"), cellListType);

        // Deserialize `ranges` list, assuming they don’t need special handling
        Type rangeListType = new TypeToken<List<ReadOnlyRange>>() {}.getType();
        List<ReadOnlyRange> ranges = gson.fromJson(jsonObject.get("ranges"), rangeListType);

        // Create ReadOnlySheetImp with the extracted values
        return new ReadOnlySheetImp(version, cells, name, numOfCols, numOfRows, cellWidth, cellHeight, ranges);
    }
}

