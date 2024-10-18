package exel.eventsys.events;

import java.io.InputStream;

public class FileContentReceivedEvent
{
    private InputStream fileContent;

    public FileContentReceivedEvent(InputStream fileContent) {
        this.fileContent = fileContent;
    }

    public InputStream getFileContent() {
        return fileContent;
    }
}
