package dev.example.quarkai.ai.tool;

import dev.example.quarkai.ui.ainav.NavigationState;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class NavigationTools {

    @Inject
    NavigationState state;

    @Tool("Navigate to a tab by index. Available tabs: 0 = Talks (conference talk grid), 1 = Customer Form (name, city, birthday fields), 2 = Vaadin Docs (link to Vaadin documentation)")
    public String navigateToTab(
            @ToolMemoryId Object memoryId,
            @P("Tab index: 0 for Talks, 1 for Customer Form, 2 for Vaadin Docs") int tabIndex
    ) {
        if (tabIndex < 0 || tabIndex > 2) {
            return "Invalid tab index. Use 0 (Talks), 1 (Customer Form), or 2 (Vaadin Docs).";
        }
        state.getTabSignal(memoryId).set(tabIndex);
        String[] tabNames = {"Talks", "Customer Form", "Vaadin Docs"};
        return "Navigated to tab: " + tabNames[tabIndex];
    }
}
