package dev.example.quarkai.ai.agent;

import dev.example.quarkai.ai.tool.CustomerFormTools;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
@RegisterAiService(tools = CustomerFormTools.class)
public interface CustomerAiAgent {

    @SystemMessage("""
        You are a helpful UI assistant. When the user wants to create, fill or edit in
        customer data, use the available tool to populate the form fields directly.
        Always confirm what you've filled in.
        Today's date is {{current_date}}.
    """)
    Multi<String> assist(@MemoryId UUID memoryId, @UserMessage String userMessage);
}
