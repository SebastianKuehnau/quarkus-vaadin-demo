package dev.example.quarkai.ui.aigrid;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@ApplicationScoped
public class GridQueryState {

    private final ConcurrentHashMap<UUID, Consumer<List<Integer>>> filterSetters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Consumer<List<Integer>>> selectionSetters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Consumer<Map<Integer, String>>> highlightUpdaters = new ConcurrentHashMap<>();

    public void register(UUID key,
                         Consumer<List<Integer>> filterSetter,
                         Consumer<List<Integer>> selectionSetter,
                         Consumer<Map<Integer, String>> highlightUpdater) {
        filterSetters.put(key, filterSetter);
        selectionSetters.put(key, selectionSetter);
        highlightUpdaters.put(key, highlightUpdater);
    }

    public void unregister(UUID key) {
        filterSetters.remove(key);
        selectionSetters.remove(key);
        highlightUpdaters.remove(key);
    }

    public void setFilter(UUID key, List<Integer> ids) {
        var setter = filterSetters.get(key);
        if (setter != null) setter.accept(ids);
    }

    public void setSelection(UUID key, List<Integer> ids) {
        var setter = selectionSetters.get(key);
        if (setter != null) setter.accept(ids);
    }

    public void updateHighlight(UUID key, Map<Integer, String> updater) {
        var u = highlightUpdaters.get(key);
        if (u != null) u.accept(updater);
    }

    public void setHighlight(UUID key, Map<Integer, String> highlights) {
        updateHighlight(key, highlights);
    }
}