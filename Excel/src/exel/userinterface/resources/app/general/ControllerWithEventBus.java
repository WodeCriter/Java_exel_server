package exel.userinterface.resources.app.general;

import exel.eventsys.EventBus;

public abstract class ControllerWithEventBus
{
    protected EventBus eventBus;

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public <T> void publishToEventBus(T event){
        eventBus.publish(event);
    }
}
