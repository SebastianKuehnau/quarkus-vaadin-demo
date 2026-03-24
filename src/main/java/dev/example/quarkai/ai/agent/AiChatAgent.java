package dev.example.quarkai.ai.agent;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.SessionScoped;

import java.util.UUID;

/**
 * LangChain4j AI service for chat interactions.
 */
@SessionScoped
@RegisterAiService
public interface AiChatAgent {

    Multi<String> chat(@MemoryId UUID chatId, @UserMessage String question);
}
