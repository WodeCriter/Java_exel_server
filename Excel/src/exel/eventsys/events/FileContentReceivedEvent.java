package exel.eventsys.events;

import engine.spreadsheet.api.ReadOnlySheet;

import java.io.InputStream;

public class FileContentReceivedEvent
{
    private ReadOnlySheet sheet;

    public FileContentReceivedEvent(ReadOnlySheet sheet) {
        this.sheet = sheet;
    }

    public ReadOnlySheet getSheet() {
        return sheet;
    }
}
