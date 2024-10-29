package utils.perms;

public class PermissionRequest
{
    private Permission permission;
    private Status status;

    public PermissionRequest(Permission permission, Status status) {
        this.permission = permission;
        this.status = status;
    }
}
