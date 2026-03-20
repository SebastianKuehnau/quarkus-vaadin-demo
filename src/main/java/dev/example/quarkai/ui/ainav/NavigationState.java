package dev.example.quarkai.ui.ainav;

import com.vaadin.flow.signals.local.ValueSignal;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class NavigationState {

    private final ConcurrentHashMap<Object, ValueSignal<Integer>> tabSignals = new ConcurrentHashMap<>();

    public ValueSignal<Integer> getTabSignal(Object key) {
        return tabSignals.computeIfAbsent(key, _ -> new ValueSignal<>(0));
    }

    public void remove(Object key) {
        tabSignals.remove(key);
    }
}
