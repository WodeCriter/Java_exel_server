package webApp.managers.requestManager;

import engine.api.Engine;
import utils.perms.Permission;
import utils.perms.PermissionRequest;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RequestManager
{
    private Map<String, Set<PermissionRequest>> usernameToRequestsForUserMap = new ConcurrentHashMap<>();

    private void addRequest(String username, PermissionRequest request){
        Set<PermissionRequest> requests = usernameToRequestsForUserMap.computeIfAbsent(username, k -> new LinkedHashSet<>());
        requests.add(request);
    }

    public void addRequest(String username, String senderName, Permission permission, String fileName){
        addRequest(username, new PermissionRequest(senderName, permission, fileName));
    }

    public boolean removeRequest(String username, PermissionRequest request){
        Set<PermissionRequest> requests = usernameToRequestsForUserMap.get(username);
        if (requests == null)
            return false;
        return requests.remove(request);
    }

    public Set<PermissionRequest> getRequestsForUser(String username){
        return usernameToRequestsForUserMap.get(username);
    }
}
