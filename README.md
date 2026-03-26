# Quarkus + Vaadin AI Demo

A demo application showing how AI agents can directly control a Vaadin UI. Built with [Quarkus 3.32](https://quarkus.io/), [Vaadin Flow 25.1](https://vaadin.com/), and [LangChain4j](https://docs.langchain4j.dev/) (via [Quarkus LangChain4j](https://docs.quarkiverse.io/quarkus-langchain4j/dev/index.html)).

Each view pairs a chat interface with UI components. When you chat with the assistant, it calls tools that update the UI in real time — filling forms, filtering grids, or switching tabs.

## Prerequisites

- Java 25+
- An OpenAI-compatible API key

## Demo Views

### Chat (`/`)
A basic streaming AI chat. No tools, just a conversation with the LLM using Vaadin's `MessageList` and `MessageInput`.

### AI Form (`/ai-form`)
A customer form (name, city, birthday) that the AI fills in for you. Tell it about a customer and it populates the fields via `CustomerFormTools`.

### AI Grid (`/ai-grid`)
A conference talk grid that the AI can filter, select, and highlight by color. Ask things like "Show me the AI talks" or "Highlight German talks in blue". Uses `TalkTools` to update the grid.

### AI Navigation (`/ai-nav`)
A tabbed view (Talks, Customer Form, Vaadin Docs) where the AI switches tabs on your behalf. Ask "Go to the customer form" and it navigates there via `NavigationTools`.

## Architecture

### Package Structure

```
ai/agent/     AI service interfaces (@RegisterAiService)
ai/config/    Chat memory configuration
ai/tool/      @Tool classes — bridge between AI and UI state
data/         Domain models (Customer, Talk) and repositories
ui/           Views, layouts, and per-view state classes
```

### AI-Controlled UI Pattern

The core pattern across all tool-enabled views:

```
User message → LLM (agent) → @Tool call → shared State → callback → ui.access() → UI update
```

1. The **View** registers a UI-update callback in a shared **State** bean (keyed by a per-session `UUID`)
2. The user sends a message, which is forwarded to the **Agent** (LLM with streaming response)
3. The LLM decides to call a **Tool** — the tool updates the shared State
4. The State invokes the View's callback, which updates the UI via `ui.access()` (server push)

State beans use `ConcurrentHashMap<UUID, Consumer<T>>` for thread-safe, per-session isolation between the AI background thread and the Vaadin UI thread.

## Configuration

Set your OpenAI API key before starting the application:

```shell
export OPENAI_API_KEY=your-api-key
```

The model (`gpt-4o-mini` by default) is configured in `src/main/resources/application.properties`.

## Running in Dev Mode

```shell
./mvnw quarkus:dev
```

The app is available at http://localhost:8080. Quarkus Dev UI at http://localhost:8080/q/dev/.

## Packaging

```shell
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

## Documentation

- [Vaadin Documentation](https://vaadin.com/docs)
- [Vaadin Quarkus Integration Guide](https://vaadin.com/docs/latest/flow/integrations/quarkus)
- [Quarkus Documentation](https://quarkus.io/guides/)
- [Quarkus LangChain4j Documentation](https://docs.quarkiverse.io/quarkus-langchain4j/dev/index.html)
