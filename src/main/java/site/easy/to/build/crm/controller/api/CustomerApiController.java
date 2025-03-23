package site.easy.to.build.crm.controller.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.DTO.DTOCustomer;
import site.easy.to.build.crm.service.customer.CustomerService;

@RestController
@RequestMapping("/api/customer")
public class CustomerApiController {
    @Autowired
    private CustomerService customerService;

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
        return dtoCustomer;
    }
}
