package dev.example.quarkai.data;

import com.vaadin.flow.signals.local.ValueSignal;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class CustomerFormState {

    private final ConcurrentHashMap<Object, ValueSignal<Customer>> signals = new ConcurrentHashMap<>();

    public ValueSignal<Customer> getCustomerSignal(Object key) {
        return signals.computeIfAbsent(key, _ -> new ValueSignal<>(Customer.empty()));
    }
}
