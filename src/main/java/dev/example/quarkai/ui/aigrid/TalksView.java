package dev.example.quarkai.ui.aigrid;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import dev.example.quarkai.ai.agent.AiGridFilterAgent;
import dev.example.quarkai.data.Talk;
import dev.example.quarkai.data.TalkRepository;
import dev.example.quarkai.ui.MainLayout;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CssImport(value = "./styles/grid-highlight.css")
@Route(value = "ai-grid", layout = MainLayout.class)
public class TalksView extends SplitLayout {

    private final MessageList messageList;
    private final Scroller scroller;
    private final Grid<Talk> grid;

    @Inject
    AiGridFilterAgent agent;

    @Inject
    GridQueryState state;

    @Inject
    TalkRepository talkRepository;

    private final UUID memoryId = UUID.randomUUID();

    // View owns the signals — they are an implementation detail
    private final ValueSignal<List<Integer>> filterSignal = new ValueSignal<>(List.of());
    private final ValueSignal<List<Integer>> selectionSignal = new ValueSignal<>(List.of());
    private final ValueSignal<Map<Integer, String>> highlightSignal = new ValueSignal<>(Map.of());

    public TalksView() {
        setOrientation(Orientation.VERTICAL);
        setSplitterPosition(66);
        setSizeFull();

        grid = createTalkGrid();
        var gridLayout = new VerticalLayout(new H3("Conference Talks"), grid);
        gridLayout.setSizeFull();
        gridLayout.expand(grid);
        addToPrimary(gridLayout);

        messageList = new MessageList();
        scroller = new Scroller(messageList);
        scroller.setSizeFull();
        addToSecondary(createChatPanel());

        addWelcomeMessage();
    }

    private void addWelcomeMessage() {
        var welcome = new MessageListItem(
                "Ask me about the talks and I'll filter, select, or highlight them in the grid. "
                + "Try things like \"Show me all AI talks\" or \"Highlight the German talks in blue.\"",
                Instant.now(), "Assistant");
        welcome.setUserColorIndex(1);
        messageList.addItem(welcome);
    }

    /**
     * Reactive signal bindings:
     *   filterSignal ──► filteredTalks (computed) ──► grid.setItems
     *   selectionSignal ──────────────────────────► grid selection
     *   highlightSignal ──────────────────────────► grid part names (row colors)
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        var allTalks = talkRepository.findAll();

        // Register signal setters/updaters with the state bean so tools can update them
        state.register(memoryId,
                filterSignal::set,
                selectionSignal::set,
                highlightSignal::set);

        // Derived signal: filters the full talk list based on all contained signals (here: filterSignal)
        var filteredTalks = Signal.computed(() -> {
            var filterIds = filterSignal.get();
            if (filterIds == null || filterIds.isEmpty()) {
                return allTalks;
            }
            return allTalks.stream()
                    .filter(talk -> filterIds.contains(talk.id()))
                    .toList();
        });

        // Effect: updates grid items whenever the filter changes
        Signal.effect(grid, () -> grid.setItems(filteredTalks.get()));

        // Effect: updates grid selection whenever selectionSignal changes,
        // when filteredTalks changes, nothing will happen (because of peek())
        Signal.effect(grid, () -> {
            var selectedIds = selectionSignal.get();
            grid.deselectAll();
            if (selectedIds != null && !selectedIds.isEmpty()) {
                filteredTalks.peek().stream()
                        .filter(talk -> selectedIds.contains(talk.id()))
                        .forEach(grid::select);
            }
        });

        // Effect: re-renders row part names (CSS highlighting) whenever highlightSignal changes
        Signal.effect(grid, () -> {
            var highlights = highlightSignal.get();
            if (highlights == null || highlights.isEmpty()) {
                grid.setPartNameGenerator(talk -> "");
            } else {
                grid.setPartNameGenerator(talk ->
                        highlights.containsKey(talk.id())
                                ? "highlighted-" + highlights.get(talk.id()) : ""
                );
            }
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        state.unregister(memoryId);
    }

    private Grid<Talk> createTalkGrid() {
        var talkGrid = new Grid<>(Talk.class, false);
        talkGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        talkGrid.addColumn(Talk::id).setHeader("ID").setSortable(true);
        talkGrid.addColumn(Talk::category).setHeader("Category").setSortable(true);
        talkGrid.addColumn(Talk::topic).setHeader("Topic").setSortable(true);
        talkGrid.addColumn(Talk::speaker).setHeader("Speaker").setSortable(true);
        talkGrid.addColumn(Talk::language).setHeader("Language").setSortable(true);
        talkGrid.setSizeFull();
        return talkGrid;
    }

    private VerticalLayout createChatPanel() {
        var messageInput = new MessageInput();
        messageInput.addSubmitListener(this::onSubmit);

        var clearButton = new Button("Clear", _ -> {
            filterSignal.set(List.of());
            selectionSignal.set(List.of());
            highlightSignal.set(Map.of());
            messageList.setItems();
        });
        clearButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        var inputLayout = new HorizontalLayout();
        inputLayout.setWidthFull();
        inputLayout.addAndExpand(messageInput);
        inputLayout.add(clearButton);
        inputLayout.setAlignItems(HorizontalLayout.Alignment.END);

        var chatLayout = new VerticalLayout(scroller, inputLayout);
        chatLayout.setSizeFull();
        chatLayout.expand(scroller);
        return chatLayout;
    }

    private void onSubmit(MessageInput.SubmitEvent event) {
        var ui = event.getSource().getUI().orElseThrow();
        var question = event.getValue();

        addMessage(question, "You", 0);
        var assistantMsg = addMessage("", "Assistant", 1);

        agent.query(memoryId, question)
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
        scroller.scrollToBottom();
        return msg;
    }
}
