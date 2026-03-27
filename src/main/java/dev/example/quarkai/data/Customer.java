package dev.example.quarkai.data;

import java.time.LocalDate;

public record Customer(String name, String city, LocalDate dateOfBirth) {
    public static Customer empty() {
        return new Customer("", "", null);
    }

    public Customer mergeWith(Customer incoming) {
        return new Customer(
                incoming.name() != null ? incoming.name() : this.name(),
                incoming.city() != null ? incoming.city() : this.city(),
                incoming.dateOfBirth() != null ? incoming.dateOfBirth() : this.dateOfBirth()
        );
    }
}
