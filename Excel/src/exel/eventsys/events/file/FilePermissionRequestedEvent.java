package exel.eventsys.events.file;

import java.io.FilePermission;

public class FilePermissionRequestedEvent {
    private String permission;
    private String fileName;
    public FilePermissionRequestedEvent(String permission, String fileName) {
        this.permission = permission;
        this.fileName = fileName;
    }
    public String getPermission() {
        return permission;
    }

    public String getFileName() {
        return fileName;
    }
}
