package dev.example.quarkai.service;

import dev.example.quarkai.service.tool.CustomerFormTools;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterAiService(tools = CustomerFormTools.class)
public interface CustomerAiService {

    @SystemMessage("""
        You are a helpful UI assistant. When the user wants to create, fill or edit in
        customer data, use the available tool to populate the form fields directly.
        Always confirm what you've filled in.
    """)
    String assist(@MemoryId Object memoryId, @UserMessage String userMessage);
}
