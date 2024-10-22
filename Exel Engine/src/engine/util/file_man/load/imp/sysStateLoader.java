package engine.util.file_man.load.imp;

import engine.spreadsheet.api.Sheet;
import java.io.*;

public class sysStateLoader {
    public static Sheet loadSysState(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (Sheet) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
