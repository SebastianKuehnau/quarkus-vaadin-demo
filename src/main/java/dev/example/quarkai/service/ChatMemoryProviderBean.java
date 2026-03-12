package dev.example.quarkai.service;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ChatMemoryProviderBean implements ChatMemoryProvider {

    private final Map<Object, MessageWindowChatMemory> memories = new ConcurrentHashMap<>();

    @Override
    public MessageWindowChatMemory get(Object memoryId) {
        return memories.computeIfAbsent(memoryId, id ->
                MessageWindowChatMemory.withMaxMessages(20));
    }
}