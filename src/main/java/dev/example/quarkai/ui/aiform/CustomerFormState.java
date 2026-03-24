package dev.example.quarkai.ui.aiform;

import dev.example.quarkai.data.Customer;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@ApplicationScoped
public class CustomerFormState {

    private final ConcurrentHashMap<UUID, Consumer<Customer>> customerUpdaters = new ConcurrentHashMap<>();

    public void register(UUID key, Consumer<Customer> updater) {
        customerUpdaters.put(key, updater);
    }

    public void unregister(UUID key) {
        customerUpdaters.remove(key);
    }

    public void updateCustomer(UUID key, Customer updatedCustomer) {
        var u = customerUpdaters.get(key);
        if (u != null) u.accept(updatedCustomer);
    }
}