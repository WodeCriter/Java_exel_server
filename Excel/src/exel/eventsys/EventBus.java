package exel.eventsys;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {
    private ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Consumer<Object>>> subscribers = new ConcurrentHashMap<>();

    public <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add((Consumer<Object>) listener);
    }

    public <T> void publish(T event) {
        if (subscribers.containsKey(event.getClass())) {
            subscribers.get(event.getClass()).forEach(consumer -> consumer.accept(event));
        }
    }
}