package dev.example.quarkai.ai.tool;

import dev.example.quarkai.data.Customer;
import dev.example.quarkai.ui.aiform.CustomerFormState;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDate;


@ApplicationScoped
public class CustomerFormTools {

    private static final Logger log = Logger.getLogger(CustomerFormTools.class);

    @Inject
    CustomerFormState state;

    @Tool("Fills the customer form fields with the given values.")
    public String fillCustomerForm(
            @ToolMemoryId Object memoryId,
            @P("Full name of the customer") String name,
            @P("City of residence") String city,
            @P("Date of birth in ISO format yyyy-MM-dd") String dateOfBirth
    ) {
        log.debugf("fillCustomerForm called [memoryId=%s, name=%s, city=%s, dateOfBirth=%s]", memoryId, name, city, dateOfBirth);

        var dob = dateOfBirth != null ? LocalDate.parse(dateOfBirth) : null;

        state.getCustomerSignal(memoryId).update(current -> new Customer(
                name != null ? name : current.name(),
                city != null ? city : current.city(),
                dob != null ? dob : current.dateOfBirth()
        ));

        return "Form filled successfully";
    }
}
