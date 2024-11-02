package webApp.managers.requestManager;

import engine.api.Engine;
import utils.perms.Permission;
import utils.perms.PermissionRequest;
import webApp.managers.fileManager.FileManager;

import java.util.*;
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

    public boolean addRequest(String senderName, Permission permission, String fileName){
        Engine engineToAddRequestTo = fileManager.getEngine(fileName);
        String username = engineToAddRequestTo.getOwnerName();

        if (engineToAddRequestTo.requestForPermission(senderName, permission))
        {
            addRequest(username, new PermissionRequest(senderName, permission, fileName));
            return true;
        }
        return false;
    }

    public void approveRequest(PermissionRequest request){
        approveOrDenyRequest(request, true);
    }

    public void denyRequest(PermissionRequest request){
        approveOrDenyRequest(request, false);
    }

    public void approveOrDenyRequest(PermissionRequest request, boolean toApprove){
        Engine engineWithRequestToApprove = fileManager.getEngine(request.getFileName());
        String username = engineWithRequestToApprove.getOwnerName();

        if (usernameToRequestsForUserMap.containsKey(username))
        {
            if (toApprove)
                engineWithRequestToApprove.approvePendingRequest(request);
            else
                engineWithRequestToApprove.denyPendingRequest(request);

            usernameToRequestsForUserMap.get(username).remove(request);
        }
    }

//    public boolean removeRequest(String username, PermissionRequest request){
//        Set<PermissionRequest> requests = usernameToRequestsForUserMap.get(username);
//        if (requests == null)
//            return false;
//        return requests.remove(request);
//    }

    public List<PermissionRequest> getRequestsForUser(String username){
        if (usernameToRequestsForUserMap.containsKey(username))
            return usernameToRequestsForUserMap.get(username).stream().toList();
        else
            return Collections.emptyList();
    }
}
