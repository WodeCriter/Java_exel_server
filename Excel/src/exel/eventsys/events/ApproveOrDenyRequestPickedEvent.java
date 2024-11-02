package exel.eventsys.events;

import utils.perms.PermissionRequest;

public class ApproveOrDenyRequestPickedEvent
{
    private PermissionRequest request;
    private boolean toApprove;

    public ApproveOrDenyRequestPickedEvent(PermissionRequest request, boolean toApprove) {
        this.request = request;
        this.toApprove = toApprove;
    }

    public PermissionRequest getRequest() {
        return request;
    }

    public boolean isToApprove() {
        return toApprove;
    }
}
