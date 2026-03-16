package dev.example.quarkai.service.tool;

import dev.example.quarkai.data.Customer;
import dev.example.quarkai.data.CustomerFormState;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;

@ApplicationScoped
public class CustomerFormTools {

    @Inject
    CustomerFormState state;

    @Tool("Fills the customer form fields with the given values. Always pass the sessionId from the system message.")
    public String fillCustomerForm(
            @P("The sessionId provided in the system message") String sessionId,
            @P("Full name of the customer") String name,
            @P("City of residence") String city,
            @P("Date of birth in ISO format yyyy-MM-dd") String dateOfBirth
    ) {
        state.getCustomerSignal(sessionId).set(new Customer(name, city, LocalDate.parse(dateOfBirth)));
        return "Form filled successfully";
    }
}
