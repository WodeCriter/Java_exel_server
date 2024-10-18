package webApp.managers.engineManager;

import java.io.InputStream;
import java.util.*;

public class SheetManager
{
    private Map<String, InputStream> nameToFileMap = new HashMap<>();

    public void addFile(String sheetName, InputStream stream){
        if (isSheetExists(sheetName))
            throw new RuntimeException("Sheet already exists");
        nameToFileMap.put(sheetName, stream);
    }

    public boolean removeSheet(String sheetName){
        return nameToFileMap.remove(sheetName) != null;
    }

    public boolean isSheetExists(String sheetName) {
        return nameToFileMap.containsKey(sheetName);
    }
}
