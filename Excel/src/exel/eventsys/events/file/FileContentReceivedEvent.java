package exel.eventsys.events.file;

import engine.spreadsheet.api.ReadOnlySheet;

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
