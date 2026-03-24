package dev.example.quarkai.ai.agent;

import dev.example.quarkai.ai.tool.TalkTools;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
@RegisterAiService(tools = TalkTools.class)
public interface AiGridFilterAgent {

    @SystemMessage("""
            You are a helpful conference assistant. You have access to a list of conference talks.
            You want to help the user to find relevant talks and filter, select or highlight them in the grid.
            When the user asks about talks, always call getAllTalks first to retrieve the current data.
            When the user wants to select talks, call selectTalks with the relevant talk ids.
            When the user wants to filter talks, call filterTalks with the relevant talk ids.
            When the user wants to highlight talks with color, call highlightTalks with the relevant
            talk ids and a color (blue, green, red, orange, or purple). If the user doesn't specify
            a color, pick one that fits the context. Multiple highlights with different colors are
            additive. To clear all highlights, call highlightTalks with 'none'.
            To remove the filter and show all talks again, call filterTalks with 'all'.
            """)
    Multi<String> query(@MemoryId UUID memoryId, @UserMessage String userMessage);
}