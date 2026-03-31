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
import java.util.UUID;


// Bridge between AI and UI: the Agent calls this tool, which updates shared state → triggers UI refresh
@ApplicationScoped
public class CustomerFormTools {

    private static final Logger log = Logger.getLogger(CustomerFormTools.class);

    @Inject
    CustomerFormState state;

    // @Tool description is sent to the LLM so it knows when/how to call this function
    @Tool("Fills the customer form fields with the given values.")
    public String fillCustomerForm(
            @ToolMemoryId UUID memoryId,
            // @P descriptions help the LLM map user input to the right parameters
            @P("Full name of the customer") String name,
            @P("City of residence") String city,
            @P("Date of birth in ISO format yyyy-MM-dd") LocalDate dateOfBirth
    ) {
        log.debugf("fillCustomerForm called [memoryId=%s, name=%s, city=%s, dateOfBirth=%s]", memoryId, name, city, dateOfBirth);

        state.updateCustomer(memoryId, new Customer(name, city, dateOfBirth));

        // Return value is sent back to the LLM as tool result
        return "Form filled successfully";
    }
}
