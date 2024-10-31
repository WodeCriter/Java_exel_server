package utils.perms;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface PermissionHelper
{
    Boolean requestForPermission(String username, Permission requestedPermission);

    void acceptPendingRequest(PermissionRequest request);

    void denyPendingRequest(PermissionRequest request);

    Boolean removePermission(String username);

    Permission getUserPermission(String userName);

    Set<PermissionRequest> getAllPendingRequests();

    String getOwnerName();
}
