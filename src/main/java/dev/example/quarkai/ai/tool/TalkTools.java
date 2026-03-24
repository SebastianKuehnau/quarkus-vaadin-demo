package dev.example.quarkai.ai.tool;

import dev.example.quarkai.data.Talk;
import dev.example.quarkai.data.TalkRepository;
import dev.example.quarkai.ui.aigrid.GridQueryState;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ApplicationScoped
public class TalkTools {

    @Inject
    TalkRepository talkRepository;

    @Inject
    GridQueryState state;

    // Tracks current highlights per session for additive merging
    private final ConcurrentHashMap<UUID, Map<Integer, String>> highlightsBySession = new ConcurrentHashMap<>();

    @Tool("Get a list of all scheduled conference talks with their id, category, topic, speaker and language")
    public List<Talk> getAllTalks() {
        return talkRepository.findAll();
    }

    @Tool("Select one or more conference talks in the grid by their id. Pass an empty list to clear the selection.")
    public String selectTalks(
            @ToolMemoryId UUID memoryId,
            @P("List of talk ids to select") List<Integer> ids
    ) {
        state.setSelection(memoryId, ids);
        return ids.isEmpty() ? "Selection cleared" : "Selected talks: " + ids;
    }

    @Tool("Filter the grid to show only specific talks by their ids. Pass an empty list to remove the filter and show all talks again.")
    public String filterTalks(
            @ToolMemoryId UUID memoryId,
            @P("List of talk ids to show, or empty list to remove the filter") List<Integer> ids
    ) {
        state.setFilter(memoryId, ids);
        return ids.isEmpty() ? "Filter removed, showing all talks" : "Grid filtered to show talks: " + ids;
    }

    @Tool("Highlight one or more conference talks with a color in the grid by their id. Available colors: blue, green, red, orange, purple. Multiple calls with different colors are additive. Pass an empty list to clear all highlights.")
    public String highlightTalks(
            @ToolMemoryId UUID memoryId,
            @P("List of talk ids to highlight, or empty list to clear all highlights") List<Integer> ids,
            @P("Color name: blue, green, red, orange, or purple") String color
    ) {
        if (ids.isEmpty()) {
            highlightsBySession.remove(memoryId);
            state.setHighlight(memoryId, Map.of());
            return "All highlights cleared";
        }

        var normalizedColor = color.trim().toLowerCase();

        var newEntries = ids.stream()
                .collect(Collectors.toMap(id -> id, _ -> normalizedColor));

        var merged = highlightsBySession.merge(memoryId, newEntries, (existing, incoming) -> {
            var result = new HashMap<>(existing);
            result.putAll(incoming);
            return result;
        });

        state.updateHighlight(memoryId, merged);

        return "Highlighted talks " + ids + " in " + normalizedColor;
    }
}
