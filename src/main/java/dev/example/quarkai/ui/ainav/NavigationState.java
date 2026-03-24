package dev.example.quarkai.ui.ainav;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@ApplicationScoped
public class NavigationState {

    private final ConcurrentHashMap<UUID, Consumer<Integer>> tabSetters = new ConcurrentHashMap<>();

    public void register(UUID key, Consumer<Integer> tabSetter) {
        tabSetters.put(key, tabSetter);
    }

    public void unregister(UUID key) {
        tabSetters.remove(key);
    }

    public void setTab(UUID key, int index) {
        var setter = tabSetters.get(key);
        if (setter != null) setter.accept(index);
    }
}
