package dev.example.quarkai.data;

import com.vaadin.signals.ValueSignal;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class CustomerFormState {

    // Holds the session ID of the currently active AI request.
    // Safe because Vaadin processes one request per session at a time
    // and LangChain4j tool calls are synchronous.
    private static final AtomicReference<String> activeSessionId = new AtomicReference<>();
    private final ConcurrentHashMap<String, ValueSignal<Customer>> signals = new ConcurrentHashMap<>();

    public static void setActiveSessionId(String sessionId) {
        activeSessionId.set(sessionId);
    }

    public ValueSignal<Customer> getCustomerSignal(String sessionId) {
        return signals.computeIfAbsent(sessionId, _ -> new ValueSignal<>(Customer.class));
    }

    public ValueSignal<Customer> getCustomerSignal() {
        var sessionId = activeSessionId.get();
        if (sessionId == null) {
            throw new IllegalStateException("No active session for tool execution");
        }
        return getCustomerSignal(sessionId);
    }
}
