package site.easy.to.build.crm.controller.api;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.DTO.DTOBudget;
import site.easy.to.build.crm.entity.DTO.DTOCustomer;
import site.easy.to.build.crm.entity.DTO.DTOExpense;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.database.CustomerImportService;

@RestController
@RequestMapping("/api/customer")
public class CustomerApiController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerImportService importService;

    // Récupérer tous les Customers
    @GetMapping("/all-customers")
    public ResponseEntity<List<DTOCustomer>> getAllCustomers() {
        List<Customer> Customers = customerService.findAll();
        List<DTOCustomer> dtoCustomers = Customers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoCustomers);
    }

    // Méthode pour convertir un Ticket en DTOTicket
    private DTOCustomer convertToDTO(Customer customer) {
        DTOCustomer dtoCustomer = new DTOCustomer();
        dtoCustomer.setId(customer.getCustomerId());
        dtoCustomer.setName(customer.getName());
        dtoCustomer.setEmail(customer.getEmail());
        dtoCustomer.setPhone(customer.getPhone());
        dtoCustomer.setCountry(customer.getCountry());
        dtoCustomer.setAddress(customer.getAddress());
        List<DTOBudget> dtoBudgets = customer.getBudgets().stream()
            .map(expense -> {
                DTOBudget dtoBudget = new DTOBudget();
                dtoBudget.setId((int) (long) expense.getId());
                dtoBudget.setTotalAmount(expense.getTotalAmount());
                dtoBudget.setDescription(expense.getDescription());
                dtoBudget.setCustomer(expense.getCustomer().getCustomerId());
                return dtoBudget;
            })
            .collect(Collectors.toList());
        // Injecter la liste de DTOExpense dans dtoTicket
        dtoCustomer.setBudgetList(dtoBudgets);
        List<DTOExpense> dtoExpenses = customer.getExpenses().stream()
            .map(expense -> {
                DTOExpense dtoExpense = new DTOExpense();
                dtoExpense.setId((int) (long) expense.getId());
                dtoExpense.setAmount(expense.getAmount());
                dtoExpense.setDescription(expense.getDescription());
                dtoExpense.setCustomer(expense.getCustomer().getCustomerId());
                return dtoExpense;
            })
            .collect(Collectors.toList());

        // Injecter la liste de DTOExpense dans dtoTicket
        dtoCustomer.setExpenseList(dtoExpenses);
        return dtoCustomer;
    }

    @PostMapping(value = "/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> importCustomer(@RequestBody String rawJson) {
        try {
            // Log du JSON brut reçu
            System.out.println("JSON brut reçu :");
            System.out.println(rawJson);
            
            // Conversion en Map
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> jsonData = mapper.readValue(rawJson, new TypeReference<Map<String, Object>>() {});
            
            // Log de la conversion
            System.out.println("JSON converti en Map :");
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonData));
            
            Customer customer = importService.processImport(jsonData);
            
            return ResponseEntity.ok()
                .body(Map.of(
                    "message", "Import réussi",
                    "customerId", customer.getCustomerId(),
                    "expensesCount", customer.getExpenses().size(),
                    "budgetsCount", customer.getBudgets().size()
                ));
        } catch (Exception e) {
            System.err.println("Erreur lors de l'import : " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "error", "Erreur lors de l'import",
                    "details", e.getMessage()
                ));
        }
    }
}
