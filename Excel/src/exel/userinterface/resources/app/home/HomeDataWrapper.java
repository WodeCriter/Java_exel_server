package exel.userinterface.resources.app.home;

import utils.perms.Permission;
import utils.perms.PermissionRequest;

import java.util.ArrayList;
import java.util.List;

class HomeDataWrapper
{
    private List<String> userNames;
    private List<String> fileNames;
    private List<PermissionRequest> permissionRequests = new ArrayList<>();

    public HomeDataWrapper(){
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public List<PermissionRequest> getPermissionRequests() {
        return permissionRequests;
    }
}
