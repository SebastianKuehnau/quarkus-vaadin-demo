package dev.example.quarkai.ui.aichat;

import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import dev.example.quarkai.ai.agent.AiChatAgent;
import dev.example.quarkai.ui.MainLayout;

import java.time.Instant;
import java.util.UUID;

/**
 * Main chat view that streams AI responses token by token.
 */
@Route(value = "", layout = MainLayout.class)
public class AiChatView extends VerticalLayout {

    private final MessageList messageList;
    private final Scroller scroller;
    private final AiChatAgent chatAiService;
    private final UUID memoryId = UUID.randomUUID();

    public AiChatView(AiChatAgent chatAiService) {
        this.chatAiService = chatAiService;
        setSizeFull();

        messageList = new MessageList();
        messageList.setMarkdown(true);

        scroller = new Scroller(messageList);
        scroller.setSizeFull();

        var messageInput = new MessageInput();
        messageInput.setWidthFull();
        messageInput.addSubmitListener(this::onSubmit);

        add(scroller, messageInput);
        expand(scroller);

        addWelcomeMessage();
    }

    private void addWelcomeMessage() {
        var welcome = new MessageListItem(
                "This is a simple chat. Ask me anything and I'll do my best to help.",
                Instant.now(), "Assistant");
        welcome.setUserColorIndex(1);
        messageList.addItem(welcome);
    }

    private void onSubmit(MessageInput.SubmitEvent event) {
        // Get a reference to the UI for thread-safe updates from the streaming callback
        var ui = event.getSource().getUI().orElseThrow();
        var question = event.getValue();

        // Show the user's message immediately
        var userMsg = new MessageListItem(question, Instant.now(), "You");
        userMsg.setUserColorIndex(0);
        messageList.addItem(userMsg);

        // Prepare an empty assistant message that will be filled token by token
        var assistantMsg = new MessageListItem("", Instant.now(), "Assistant");
        assistantMsg.setUserColorIndex(1);
        messageList.addItem(assistantMsg);

        // Stream the AI response and collect the full response for memory
        chatAiService.chat(memoryId, question)
                .subscribe()
                .with(token -> ui.access(() -> {
                    assistantMsg.appendText(token);
                    scroller.scrollToBottom();
                }));

        scroller.scrollToBottom();
    }
}