package engine.spreadsheet.cell;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import engine.spreadsheet.cell.imp.ReadOnlyCellImp;

import java.io.IOException;
import java.util.List;

public class ReadOnlyCellImpAdapter extends TypeAdapter<ReadOnlyCellImp> {
    @Override
    public void write(JsonWriter out, ReadOnlyCellImp cell) throws IOException {
        // Serialization logic if needed
    }

    @Override
    public ReadOnlyCellImp read(JsonReader in) throws IOException {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(in).getAsJsonObject();

        String coordinate = jsonObject.get("coordinate").getAsString();
        String originalValue = jsonObject.get("originalValue").getAsString();
        String effectiveValue = jsonObject.get("effectiveValue").getAsString();
        int version = jsonObject.get("version").getAsInt();

        Gson gson = new Gson();
        List<String> dependsOn = gson.fromJson(jsonObject.get("dependsOn"), new TypeToken<List<String>>() {}.getType());
        List<String> influencingOn = gson.fromJson(jsonObject.get("influencingOn"), new TypeToken<List<String>>() {}.getType());

        return new ReadOnlyCellImp(coordinate, originalValue, effectiveValue, version, dependsOn, influencingOn);
    }
}
