package dev.example.quarkai.service;

import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.smallrye.mutiny.Multi;

/**
 * LangChain4j AI service for chat interactions.
 */
@RegisterAiService
public interface AiChatService {

    Multi<String> chat(@UserMessage String message);
}
