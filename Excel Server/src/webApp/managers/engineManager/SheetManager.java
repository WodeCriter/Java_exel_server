package webApp.managers.engineManager;

import java.util.LinkedList;
import java.util.List;

public class SheetManager
{
    private List<String> sheetNames = new LinkedList<>();

    public void addSheet(String sheetName){
        if (isSheetExists(sheetName))
            throw new RuntimeException("Sheet already exists");
        sheetNames.add(sheetName);
    }

    public boolean removeSheet(String sheetName){
        return sheetNames.remove(sheetName);
    }

    public boolean isSheetExists(String sheetName) {
        return sheetNames.contains(sheetName);
    }
}
