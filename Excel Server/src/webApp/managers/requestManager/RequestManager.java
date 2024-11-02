package webApp.managers.requestManager;

import engine.api.Engine;
import utils.perms.Permission;
import utils.perms.PermissionRequest;
import webApp.managers.fileManager.FileManager;
import webApp.utils.ServletUtils;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RequestManager
{
    private Map<String, Set<PermissionRequest>> usernameToRequestsForUserMap = new ConcurrentHashMap<>();
    private FileManager fileManager;

    public RequestManager(FileManager fileManager){
        this.fileManager = fileManager;
    }

    private void addRequest(String username, PermissionRequest request){
        Set<PermissionRequest> requests = usernameToRequestsForUserMap.computeIfAbsent(username, k -> new LinkedHashSet<>());
        requests.add(request);
    }

    public void addRequest(String senderName, Permission permission, String fileName){
        Engine engineToAddRequestTo = fileManager.getEngine(fileName);
        String username = engineToAddRequestTo.getOwnerName();

        if (engineToAddRequestTo.requestForPermission(senderName, permission))
            addRequest(username, new PermissionRequest(senderName, permission, fileName));
    }

//    public boolean removeRequest(String username, PermissionRequest request){
//        Set<PermissionRequest> requests = usernameToRequestsForUserMap.get(username);
//        if (requests == null)
//            return false;
//        return requests.remove(request);
//    }

    public Set<PermissionRequest> getRequestsForUser(String username){
        return usernameToRequestsForUserMap.get(username);
    }
}
