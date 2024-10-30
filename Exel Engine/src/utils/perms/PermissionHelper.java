package utils.perms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface PermissionHelper
{
    PermissionRequest getUserRequest(String username);

    Boolean requestForPermission(String username, Permission requestedPermission);

    void acceptPendingRequest(String username);

    void denyPendingRequest(String username);

    Boolean removePermission(String username);
}
