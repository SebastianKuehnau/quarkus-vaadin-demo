package dev.example.quarkai.ai.agent;

import dev.example.quarkai.ai.tool.NavigationTools;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
@RegisterAiService(tools = NavigationTools.class)
public interface AiNavigationAgent {

    @SystemMessage("""
            You are a helpful navigation assistant for a conference application.
            The application has three tabs that the user can navigate to:
            - Tab 0: "Talks" — a grid showing all conference talks with their category, topic, speaker, and language.
            - Tab 1: "Customer Form" — a form with fields for name, city, and birthday.
            - Tab 2: "Vaadin Docs" — a link to the official Vaadin documentation.
            When the user asks to see or navigate to a tab, call navigateToTab with the appropriate index.
            When the user asks what's available, describe all three tabs.
            Be concise and helpful.
            """)
    Multi<String> chat(@MemoryId UUID memoryId, @UserMessage String userMessage);
}
