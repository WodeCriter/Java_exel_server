package utils.perms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PermissionHelper
{
    private Map<String, Permission> permissions;
    private Map<String, PermissionRequest> allRequestsEverMade;

    protected PermissionHelper(String ownerName){
        permissions = new ConcurrentHashMap<>();
        allRequestsEverMade = new ConcurrentHashMap<>();
        permissions.put(ownerName, Permission.OWNER);
    }

    public PermissionRequest getUserRequest(String username) {
        return allRequestsEverMade.get(username);
    }

    public Boolean requestForPermission(String username, Permission requestedPermission){
        if (!allRequestsEverMade.containsKey(username) ||
                getUserRequest(username).status() == Status.DENIED ||
                getUserRequest(username).permission().compareTo(requestedPermission) < 0)
        {
            allRequestsEverMade.put(username, new PermissionRequest(requestedPermission, Status.PENDING));
            return true;
        }
        else
            return false;
    }

    public void acceptPendingRequest(String username){
        PermissionRequest userRequest = getPendingUserRequest(username);
        userRequest.setStatus(Status.ACCEPTED);
        permissions.put(username, userRequest.permission());
    }

    public void denyPendingRequest(String username){
        PermissionRequest userRequest = getPendingUserRequest(username);
        userRequest.setStatus(Status.DENIED);
    }

    public Boolean removePermission(String username){
        return permissions.remove(username) != null;
    }

    private PermissionRequest getPendingUserRequest(String username) {
        PermissionRequest userRequest = getUserRequest(username);

        if (!allRequestsEverMade.containsKey(username) || userRequest.status() != Status.PENDING)
            throw new IllegalArgumentException("\""+ username +"\" has no pending request");
        return userRequest;
    }
}
