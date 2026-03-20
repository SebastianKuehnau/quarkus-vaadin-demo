package dev.example.quarkai.ui.aigrid;

import com.vaadin.flow.signals.local.ValueSignal;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class GridQueryState {

    private final ConcurrentHashMap<Object, ValueSignal<List<Integer>>> filterSignals = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Object, ValueSignal<List<Integer>>> selectionSignals = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Object, ValueSignal<Map<Integer, String>>> highlightSignals = new ConcurrentHashMap<>();

    public ValueSignal<List<Integer>> getFilterSignal(Object key) {
        return filterSignals.computeIfAbsent(key, _ -> new ValueSignal<>(List.of()));
    }

    public ValueSignal<List<Integer>> getSelectionSignal(Object key) {
        return selectionSignals.computeIfAbsent(key, _ -> new ValueSignal<>(List.of()));
    }

    public ValueSignal<Map<Integer, String>> getHighlightSignal(Object key) {
        return highlightSignals.computeIfAbsent(key, _ -> new ValueSignal<>(Map.of()));
    }

    public void remove(Object key) {
        filterSignals.remove(key);
        selectionSignals.remove(key);
        highlightSignals.remove(key);
    }
}
