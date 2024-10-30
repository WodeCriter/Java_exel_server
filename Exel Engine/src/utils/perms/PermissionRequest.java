package utils.perms;

public class PermissionRequest implements Comparable<PermissionRequest>
{
    private Permission permission;
    private Status status;

    public PermissionRequest(Permission permission, Status status) {
        this.setPermission(permission);
        this.status = status;
    }

    public Permission permission() {
        return permission;
    }

    private void setPermission(Permission permission) {
        if (permission == Permission.OWNER)
            throw new IllegalArgumentException("Can't request owner permission");
        this.permission = permission;
    }

    public Status status() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public int compareTo(PermissionRequest o) {
        return permission.compareTo(o.permission());
    }
}
