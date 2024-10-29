package exel.userinterface.resources.app;

import exel.eventsys.EventBus;

public abstract class ControllerWithEventBus
{
    protected EventBus eventBus;

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
