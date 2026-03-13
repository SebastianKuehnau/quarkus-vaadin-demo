package dev.example.quarkai.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.SessionScoped;

/**
 * LangChain4j AI service for chat interactions.
 */
@SessionScoped
@RegisterAiService
public interface AiChatService {

    Multi<String> chat(@MemoryId Object chatId, @UserMessage String question);
}
