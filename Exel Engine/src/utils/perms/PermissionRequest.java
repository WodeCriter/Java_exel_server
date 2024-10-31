package utils.perms;

public class PermissionRequest implements Comparable<PermissionRequest>
{
    private String requestSender;
    private Permission permission;
    private Status status;

    public PermissionRequest(String requestSender, Permission permission, Status status) {
        this.requestSender = requestSender;
        this.setPermission(permission);
        this.status = status;
    }

    public PermissionRequest(String requestSender, Permission permission) {
        this(requestSender, permission, Status.PENDING);
    }

    public Permission permission() {
        return permission;
    }

    private void setPermission(Permission permission) {
        if (permission == Permission.OWNER || permission == Permission.NONE)
            throw new IllegalArgumentException("Can't request "+ permission +" permission");
        this.permission = permission;
    }

    public Status status() {
        return status;
    }

    public void approveRequest(){
        status = Status.ACCEPTED;
    }

    public void denyRequest(){
        status = Status.DENIED;
    }

    public String getSender() {
        return requestSender;
    }

    @Override
    public int compareTo(PermissionRequest o) {
        return permission.compareTo(o.permission());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PermissionRequest))
            return false;
        PermissionRequest other = (PermissionRequest) o;

        return requestSender.equals(other.requestSender)
                && permission.equals(other.permission)
                && status.equals(other.status);
    }

    @Override
    public int hashCode() {
        return requestSender.hashCode() + permission.hashCode() + status.hashCode();
    }
}
