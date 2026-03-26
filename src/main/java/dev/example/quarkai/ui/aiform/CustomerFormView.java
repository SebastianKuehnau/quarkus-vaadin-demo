package dev.example.quarkai.ui.aiform;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.example.quarkai.ai.agent.CustomerAiAgent;
import dev.example.quarkai.ui.MainLayout;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.UUID;

// AI-controlled form demo: user chats with the AI, which fills form fields via tool calls.
// Flow: User message → Agent → Tool call → CustomerFormState → callback → UI update (server push)
@PageTitle(value = "Customer AI Form")
@Route(value = "ai-form", layout = MainLayout.class)
public class CustomerFormView extends VerticalLayout {

    private final MessageList messageList;
    private final Scroller scroller;
    private final TextField nameField;
    private final TextField cityField;
    private final DatePicker datePicker;

    @Inject
    CustomerAiAgent service;

    @Inject
    CustomerFormState state;

    private final UUID memoryId = UUID.randomUUID();

    public CustomerFormView() {
        nameField = new TextField("Name");
        nameField.setReadOnly(true);
        cityField = new TextField("City");
        cityField.setReadOnly(true);
        datePicker = new DatePicker("Birthday");
        datePicker.setReadOnly(true);

        messageList = new MessageList();
        var input = new MessageInput();
        input.addSubmitListener(this::onSubmit);
        input.setWidthFull();

        scroller = new Scroller(messageList);
        add(nameField, cityField, datePicker, scroller, input);
        setAlignItems(Alignment.CENTER);
        setSizeFull();

        addWelcomeMessage();
    }

    private void addWelcomeMessage() {
        var welcome = new MessageListItem(
                "Tell me about a customer and I'll fill in the form for you. "
                + "For example: \"Create a customer named John Miller from Berlin, born on March 5th 1990.\"",
                Instant.now(), "Assistant");
        welcome.setUserColorIndex(1);
        messageList.addItem(welcome);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        var ui = attachEvent.getUI();

        state.register(memoryId, updatedCustomer -> {
            ui.access(() -> {
                nameField.setValue(updatedCustomer.name() != null ? updatedCustomer.name() : "");
                cityField.setValue(updatedCustomer.city() != null ? updatedCustomer.city() : "");
                datePicker.setValue(updatedCustomer.dateOfBirth());
            });
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        state.unregister(memoryId);
    }

    private void onSubmit(MessageInput.SubmitEvent event) {
        var ui = event.getSource().getUI().orElseThrow();
        var question = event.getValue();

        addMessage(question, "You", 0);
        var assistantMsg = addMessage("", "Assistant", 1);

        service.assist(memoryId, question)
                .subscribe()
                .with(token -> ui.access(() -> {
                    assistantMsg.appendText(token);
                    scroller.scrollToBottom();
                }));

        scroller.scrollToBottom();
    }

    private MessageListItem addMessage(String text, String sender, int colorIndex) {
        var msg = new MessageListItem(text, Instant.now(), sender);
        msg.setUserColorIndex(colorIndex);
        messageList.addItem(msg);
        return msg;
    }
}