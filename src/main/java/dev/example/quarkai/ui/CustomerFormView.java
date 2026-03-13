package dev.example.quarkai.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import dev.example.quarkai.data.CustomerFormState;
import dev.example.quarkai.service.CustomerAiService;
import jakarta.inject.Inject;

import java.time.Instant;

@Route("customer-form")
public class CustomerFormView extends VerticalLayout {

    private final MessageList messageList;
    private final Scroller scroller;

    @Inject
    CustomerAiService service;

    @Inject
    CustomerFormState state;

    private final TextField nameField;
    private final TextField cityField;
    private final DatePicker datePicker;

    public CustomerFormView() {
        nameField = new TextField("Name");
        nameField.setReadOnly(true);
        cityField = new TextField("City");
        cityField.setReadOnly(true);
        datePicker = new DatePicker("Birthday");
        datePicker.setReadOnly(true);

        // Chat-UI
        messageList = new MessageList();
        var input = new MessageInput();
        input.addSubmitListener(this::onSubmit);
        input.setWidthFull();

        scroller = new Scroller(messageList);
        add(nameField, cityField, datePicker, scroller, input);
        setAlignItems(Alignment.CENTER);
        setSizeFull();
    }

    private String sessionId;

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        sessionId = attachEvent.getUI().getSession().getSession().getId();

        // Reactive bindings: fields update automatically when the signal changes
        var signal = state.getCustomerSignal(sessionId);
        nameField.bindValue(signal.map(c -> c != null && c.name() != null ? c.name() : ""), null);
        cityField.bindValue(signal.map(c -> c != null && c.city() != null ? c.city() : ""), null);
        datePicker.bindValue(signal.map(c -> c != null ? c.dateOfBirth() : null), null);
    }

    private void onSubmit(MessageInput.SubmitEvent event) {
        var ui = event.getSource().getUI().orElseThrow();
        var question = event.getValue();

        var userMsg = new MessageListItem(question, Instant.now(), "You");
        userMsg.setUserColorIndex(0);
        messageList.addItem(userMsg);

        var assistantMsg = new MessageListItem("", Instant.now(), "Assistant");
        assistantMsg.setUserColorIndex(1);
        messageList.addItem(assistantMsg);

        // Set session ID so the tool can find the right signal on any thread
        CustomerFormState.setActiveSessionId(sessionId);

        // Each browser tab gets its own chat memory
        var memoryId = ui.getUIId();

        service.assist(memoryId, question).subscribe()
                .with(token -> ui.access(() -> {
                    assistantMsg.appendText(token);
                    scroller.scrollToBottom();
                }));

        scroller.scrollToBottom();
    }
}
