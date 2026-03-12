package dev.example.quarkai.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEffect;
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
        cityField = new TextField("City");
        datePicker = new DatePicker("Birthday");

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

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // Reactive bindings: fields update automatically when the signal changes
        var signal = state.getCustomerSignal();
        ComponentEffect.effect(nameField, () -> {
            var customer = signal.value();
            if (customer != null) nameField.setValue(customer.name() != null ? customer.name() : "");
        });
        ComponentEffect.effect(cityField, () -> {
            var customer = signal.value();
            if (customer != null) cityField.setValue(customer.city() != null ? customer.city() : "");
        });
        ComponentEffect.effect(datePicker, () -> {
            var customer = signal.value();
            if (customer != null && customer.dateOfBirth() != null) datePicker.setValue(customer.dateOfBirth());
        });
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

        service.assist(question).subscribe()
                .with(token -> ui.access(() -> {
                    assistantMsg.appendText(token);
                    scroller.scrollToBottom();
                }));

        scroller.scrollToBottom();
    }
}
