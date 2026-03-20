package dev.example.quarkai.ai.tool;

import dev.example.quarkai.data.Talk;
import dev.example.quarkai.data.TalkRepository;
import dev.example.quarkai.ui.aigrid.GridQueryState;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TalkTools {

    @Inject
    TalkRepository talkRepository;

    @Inject
    GridQueryState state;

    @Tool("Get a list of all scheduled conference talks with their id, category, topic, speaker and language")
    public List<Talk> getAllTalks() {
        return talkRepository.findAll();
    }

    @Tool("Select one or more conference talks in the grid by their id. Pass an empty list to clear the selection.")
    public String selectTalks(
            @ToolMemoryId Object memoryId,
            @P("List of talk ids to select") List<Integer> ids
    ) {
        state.getSelectionSignal(memoryId).set(ids);
        return ids.isEmpty() ? "Selection cleared" : "Selected talks: " + ids;
    }

    @Tool("Filter the grid to show only specific talks by their ids. Pass an empty list to remove the filter and show all talks again.")
    public String filterTalks(
            @ToolMemoryId Object memoryId,
            @P("List of talk ids to show, or empty list to remove the filter") List<Integer> ids
    ) {
        state.getFilterSignal(memoryId).set(ids);
        return ids.isEmpty() ? "Filter removed, showing all talks" : "Grid filtered to show talks: " + ids;
    }

    @Tool("Highlight one or more conference talks with a color in the grid by their id. Available colors: blue, green, red, orange, purple. Multiple calls with different colors are additive. Pass an empty list to clear all highlights.")
    public String highlightTalks(
            @ToolMemoryId Object memoryId,
            @P("List of talk ids to highlight, or empty list to clear all highlights") List<Integer> ids,
            @P("Color name: blue, green, red, orange, or purple") String color
    ) {
        if (ids.isEmpty()) {
            state.getHighlightSignal(memoryId).set(Map.of());
            return "All highlights cleared";
        }

        var normalizedColor = color.trim().toLowerCase();

        state.getHighlightSignal(memoryId).update(current -> {
            var updated = new java.util.HashMap<>(current);
            ids.forEach(id -> updated.put(id, normalizedColor));
            return updated;
        });

        return "Highlighted talks " + ids + " in " + normalizedColor;
    }
}
