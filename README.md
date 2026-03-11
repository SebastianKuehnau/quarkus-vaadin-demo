# Quarkus + Vaadin AI Chat Demo

A demo application that integrates [Vaadin](https://vaadin.com/) into a [Quarkus](https://quarkus.io/) project to build a streaming AI chat UI using [LangChain4j](https://docs.langchain4j.dev/).

## Prerequisites

- Java 25+
- An OpenAI-compatible API key

## Project Structure

- **`AiChatView`** — A simple AI chat view built with Vaadin's `MessageList` and `MessageInput` components. AI responses are streamed token by token using Mutiny's `Multi` and Vaadin server push.
- **`VaadinConfig`** — Application-wide Vaadin configuration that enables the Aura theme and server push (`@Push`), which is required for asynchronous UI updates from background threads.
- **`AiChatService`** — A LangChain4j AI service interface that streams chat responses via `Multi<String>`.

## Configuration

Set your OpenAI API key before starting the application:

```shell
export OPENAI_API_KEY=your-api-key
```

The model is configured in `src/main/resources/application.properties`.

## Running in Dev Mode

Start the application with live reload enabled:

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
