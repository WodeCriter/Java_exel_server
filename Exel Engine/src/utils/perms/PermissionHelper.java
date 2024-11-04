package utils.perms;

import java.util.Set;

public interface PermissionHelper
{
    Boolean requestForPermission(String requestSender, Permission requestedPermission);

    void approvePendingRequest(PermissionRequest request);

    void denyPendingRequest(PermissionRequest request);

    Boolean removePermission(String username);

    Permission getUserPermission(String userName);

    Set<PermissionRequest> getAllPendingRequests();

    String getOwnerName();

    Set<PermissionRequest> getAllRequestsEverMade();
}
