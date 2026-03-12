package dev.example.quarkai.data;

import com.vaadin.signals.ValueSignal;
import jakarta.enterprise.context.SessionScoped;

import java.io.Serializable;

@SessionScoped
public class CustomerFormState implements Serializable {

    private final ValueSignal<Customer> customerSignal = new ValueSignal<>(Customer.class);

    public ValueSignal<Customer> getCustomerSignal() {
        return customerSignal;
    }
}
