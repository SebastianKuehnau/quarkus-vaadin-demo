# Quarkus + Vaadin AI Demo

A demo application showing how AI agents can directly control a Vaadin UI. Built with [Quarkus](https://quarkus.io/), [Vaadin Flow](https://vaadin.com/), and [LangChain4j](https://docs.langchain4j.dev/).

Each view pairs a chat interface with UI components. When you chat with the assistant, it calls tools that update the UI in real time — filling forms, filtering grids, or switching tabs.

## Prerequisites

- Java 25+
- An OpenAI-compatible API key

## Views

### Chat (`/`)
A basic streaming AI chat. No tools, just a conversation with the LLM using Vaadin's `MessageList` and `MessageInput`.

### AI Form (`/ai-form`)
A customer form (name, city, birthday) that the AI fills in for you. Tell it about a customer and it populates the fields via `CustomerFormTools`. Form state is managed with Vaadin Signals.

### AI Grid (`/ai-grid`)
A conference talk grid that the AI can filter, select, and highlight by color. Ask things like "Show me the AI talks" or "Highlight German talks in blue". Uses `TalkTools` and reactive Signals to update the grid.

### AI Navigation (`/ai-nav`)
A tabbed view (Talks, Customer Form, Vaadin Docs) where the AI switches tabs on your behalf. Ask "Go to the customer form" and it navigates there via `NavigationTools`.

## Architecture

```
ai/agent/     AI service interfaces (@RegisterAiService)
ai/config/    Chat memory configuration
ai/tool/      @Tool classes that bridge AI to UI state
data/         Domain models (Customer, Talk) and repositories
ui/           Views, layouts, and per-view state classes
```

The pattern across all views: an AI agent calls a tool, the tool updates a shared `ValueSignal`, and the view reacts to the signal change. All UI updates go through `ui.access()` for thread safety.

## Configuration

Set your OpenAI API key before starting the application:

```shell
export OPENAI_API_KEY=your-api-key
```

The model is configured in `src/main/resources/application.properties`.

## Running in Dev Mode

```shell
./mvnw quarkus:dev
```

The app is available at http://localhost:8080.

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
