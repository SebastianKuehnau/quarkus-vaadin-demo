package dev.example.quarkai.data;

import java.time.LocalDate;

public record Customer(String name, String city, LocalDate dateOfBirth) {
    public static Customer empty() {
        return new Customer("", "", null);
    }
}
