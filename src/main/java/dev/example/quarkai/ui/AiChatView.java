package dev.example.quarkai.ui;

import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import dev.example.quarkai.service.AiChatService;
import jakarta.inject.Inject;

import java.time.Instant;

/**
 * Main chat view that streams AI responses token by token.
 */
@Route("")
public class AiChatView extends VerticalLayout {

    private final MessageList messageList;
    private final Scroller scroller;

    @Inject
    AiChatService chatAiService;

    public AiChatView() {
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

        // Stream the AI response: each token arrives asynchronously.
        // ui.access() is required because the callback runs on a background thread,
        // and Vaadin UI updates must happen within a UI lock.
        chatAiService.chat(question).subscribe()
                .with(token -> ui.access(() -> {
                    assistantMsg.appendText(token);
                    scroller.scrollToBottom();
                }));

        scroller.scrollToBottom();
    }
}
