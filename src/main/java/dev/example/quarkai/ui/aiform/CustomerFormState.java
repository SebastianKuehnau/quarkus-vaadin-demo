package dev.example.quarkai.ui.aiform;

import dev.example.quarkai.data.Customer;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

// Shared state that connects AI tools (background thread) to the UI (session thread).
// The View registers a callback; the Tool pushes updates through it.
@ApplicationScoped
public class CustomerFormState {

    // Thread-safe map: each browser session registers its own UI updater keyed by memoryId
    private final ConcurrentHashMap<UUID, Consumer<Customer>> customerUpdaters = new ConcurrentHashMap<>();
    // Tracks current form state per session so multiple tool calls can be merged
    private final ConcurrentHashMap<UUID, Customer> currentCustomers = new ConcurrentHashMap<>();

    public void register(UUID key, Consumer<Customer> updater) {
        customerUpdaters.put(key, updater);
    }

    public void unregister(UUID key) {
        customerUpdaters.remove(key);
        currentCustomers.remove(key);
    }

    public void updateCustomer(UUID key, Customer incoming) {
        var merged = currentCustomers.merge(key, incoming, Customer::mergeWith);

        Optional.ofNullable(customerUpdaters.get(key))
                .ifPresent(updater -> updater.accept(merged));
    }
}