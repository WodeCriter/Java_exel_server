package exel.engine.util.file_man.save.imp;

import exel.engine.spreadsheet.api.Sheet;
import java.io.*;

public class sysStateSaver {

    public static void saveSheetState(String filePath, Sheet sheet ) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(sheet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
