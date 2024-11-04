package exel.eventsys.events.file;

public class FileSelectedForOverview
{
    private String fileName;

    public FileSelectedForOverview(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
