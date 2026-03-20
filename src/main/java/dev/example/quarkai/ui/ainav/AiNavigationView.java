package dev.example.quarkai.ui.ainav;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import dev.example.quarkai.ai.agent.AiNavigationAgent;
import dev.example.quarkai.data.Talk;
import dev.example.quarkai.data.TalkRepository;
import dev.example.quarkai.ui.MainLayout;
import jakarta.inject.Inject;

import java.time.Instant;

@Route(value = "ai-nav", layout = MainLayout.class)
public class AiNavigationView extends SplitLayout {

    private final MessageList messageList;
    private final Scroller scroller;
    private final TabSheet tabSheet;

    @Inject
    AiNavigationAgent navigationAgent;

    @Inject
    NavigationState state;

    private final TalkRepository talkRepository;

    private int memoryId;

    public AiNavigationView(TalkRepository talkRepository) {
        this.talkRepository = talkRepository;
        setSizeFull();
        setSplitterPosition(70);

        tabSheet = new TabSheet();
        tabSheet.setSizeFull();
        tabSheet.add("Talks", createTalksTab());
        tabSheet.add("Customer Form", createCustomerFormTab());
        tabSheet.add("Vaadin Docs", createDocsTab());
        addToPrimary(tabSheet);

        messageList = new MessageList();
        scroller = new Scroller(messageList);
        scroller.setSizeFull();
        addToSecondary(createChatPanel());

        addWelcomeMessage();
    }

    private void addWelcomeMessage() {
        var welcome = new MessageListItem(
                "I can switch between the tabs for you. "
                + "Just say something like \"Show me the talks\" or \"Go to the customer form.\"",
                Instant.now(), "Assistant");
        welcome.setUserColorIndex(1);
        messageList.addItem(welcome);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        memoryId = attachEvent.getUI().getUIId();

        var tabSignal = state.getTabSignal(memoryId);

        Signal.effect(tabSheet, () -> {
            var index = tabSignal.get();
            tabSheet.setSelectedIndex(index);
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        state.remove(memoryId);
    }

    private VerticalLayout createTalksTab() {
        var grid = new Grid<>(Talk.class, false);
        grid.addColumn(Talk::id).setHeader("ID").setSortable(true);
        grid.addColumn(Talk::category).setHeader("Category").setSortable(true);
        grid.addColumn(Talk::topic).setHeader("Topic").setSortable(true);
        grid.addColumn(Talk::speaker).setHeader("Speaker").setSortable(true);
        grid.addColumn(Talk::language).setHeader("Language").setSortable(true);
        grid.setSizeFull();
        grid.setItems(talkRepository.findAll());

        var layout = new VerticalLayout(grid);
        layout.setSizeFull();
        layout.expand(grid);
        return layout;
    }

    private VerticalLayout createCustomerFormTab() {
        var nameField = new TextField("Name");
        var cityField = new TextField("City");
        var datePicker = new DatePicker("Birthday");

        var layout = new VerticalLayout(nameField, cityField, datePicker);
        layout.setAlignItems(VerticalLayout.Alignment.CENTER);
        return layout;
    }

    private VerticalLayout createDocsTab() {
        var link = new Anchor("https://vaadin.com/docs", "Open Vaadin Documentation");
        link.setTarget("_blank");

        var description = new Span("Visit the official Vaadin documentation for guides, API references, and tutorials.");

        var layout = new VerticalLayout(link, description);
        layout.setAlignItems(VerticalLayout.Alignment.CENTER);
        layout.setPadding(true);
        return layout;
    }

    private VerticalLayout createChatPanel() {
        var messageInput = new MessageInput();
        messageInput.addSubmitListener(this::onSubmit);
        messageInput.setWidthFull();

        var chatLayout = new VerticalLayout(scroller, messageInput);
        chatLayout.setSizeFull();
        chatLayout.expand(scroller);
        return chatLayout;
    }

    private void onSubmit(MessageInput.SubmitEvent event) {
        var ui = event.getSource().getUI().orElseThrow();
        var question = event.getValue();

        addMessage(question, "You", 0);
        var assistantMsg = addMessage("", "Assistant", 1);
        scroller.scrollToBottom();

        navigationAgent.chat(memoryId, question)
                .subscribe()
                .with(token -> ui.access(() -> {
                    assistantMsg.appendText(token);
                    scroller.scrollToBottom();
                }));
    }

    private MessageListItem addMessage(String text, String sender, int colorIndex) {
        var msg = new MessageListItem(text, Instant.now(), sender);
        msg.setUserColorIndex(colorIndex);
        messageList.addItem(msg);
        return msg;
    }
}
