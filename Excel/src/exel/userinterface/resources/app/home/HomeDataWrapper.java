package exel.userinterface.resources.app.home;

import engine.util.FileData;
import utils.perms.PermissionRequest;

import java.util.ArrayList;
import java.util.List;

class HomeDataWrapper
{
    private List<String> userNames;
    private List<FileData> fileData;
    private List<PermissionRequest> permissionRequests = new ArrayList<>();
    private List<PermissionRequest> permissionRequestsForFile = new ArrayList<>();

    public HomeDataWrapper(){
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public List<FileData> getFileData() {
        return fileData;
    }

    public List<PermissionRequest> getPermissionRequests() {
        return permissionRequests;
    }

    public List<PermissionRequest> getPermissionRequestsForFile() {
        return permissionRequestsForFile;
    }
}
