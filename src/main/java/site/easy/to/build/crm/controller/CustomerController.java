package site.easy.to.build.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.CustomerLoginInfo;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.OAuthUser;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.google.service.acess.GoogleAccessService;
import site.easy.to.build.crm.google.service.gmail.GoogleGmailApiService;
import site.easy.to.build.crm.service.contract.ContractService;
import site.easy.to.build.crm.service.customer.CustomerLoginInfoService;
import site.easy.to.build.crm.service.customer.CustomerService;
import site.easy.to.build.crm.service.database.CustomerImportService;
import site.easy.to.build.crm.service.lead.LeadService;
import site.easy.to.build.crm.service.ticket.TicketService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;
import site.easy.to.build.crm.util.AuthorizationUtil;
import site.easy.to.build.crm.util.EmailTokenUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper; 
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.beans.BeanUtils;
import org.springframework.http.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Controller
@RequestMapping("/employee/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final UserService userService;
    private final CustomerLoginInfoService customerLoginInfoService;
    private final AuthenticationUtils authenticationUtils;
    private final GoogleGmailApiService googleGmailApiService;
    private final Environment environment;
    private final TicketService ticketService;
    private final ContractService contractService;
    private final LeadService leadService;

    @Autowired
    public CustomerController(CustomerService customerService, UserService userService, CustomerLoginInfoService customerLoginInfoService,
                              AuthenticationUtils authenticationUtils, GoogleGmailApiService googleGmailApiService, Environment environment,
                              TicketService ticketService, ContractService contractService, LeadService leadService) {
        this.customerService = customerService;
        this.userService = userService;
        this.customerLoginInfoService = customerLoginInfoService;
        this.authenticationUtils = authenticationUtils;
        this.googleGmailApiService = googleGmailApiService;
        this.environment = environment;
        this.ticketService = ticketService;
        this.contractService = contractService;
        this.leadService = leadService;
    }

    @GetMapping("/manager/all-customers")
    public String getAllCustomers(Model model){
        List<Customer> customers;
        try {
            customers = customerService.findAll();
        } catch (Exception e){
            return "error/500";
        }
        model.addAttribute("customers",customers);
        return "customer/all-customers";
    }

    @GetMapping("/my-customers")
    public String getEmployeeCustomer(Model model, Authentication authentication){
        List<Customer> customers;

        int userId = authenticationUtils.getLoggedInUserId(authentication);
        if(userId == -1) {
            return "error/not-found";
        }
        customers = customerService.findByUserId(userId);
        model.addAttribute("customers",customers);
        return "customer/all-customers";
    }

    @GetMapping("/{id}")
    public String showCustomerDetail(@PathVariable("id") int id, Model model, Authentication authentication) {
        Customer customer = customerService.findByCustomerId(id);
        if(customer == null) {
            return "error/not-found";
        }

        User employee = customer.getUser();
        int userId = authenticationUtils.getLoggedInUserId(authentication);
        User loggedInUser = userService.findById(userId);
        if(loggedInUser.isInactiveUser()) {
            return "error/account-inactive";
        }
        if(!AuthorizationUtil.checkIfUserAuthorized(employee,loggedInUser)) {
            return "redirect:/access-denied";
        }

        model.addAttribute("customer",customer);
        return "customer/customer-details";
    }

    @GetMapping("/create-customer")
    public String showCreateCustomerForm(Model model, Authentication authentication){
        int userId = authenticationUtils.getLoggedInUserId(authentication);
        User user = userService.findById(userId);
        if(user.isInactiveUser()) {
            return "error/account-inactive";
        }
        boolean hasGoogleGmailAccess = false;
        boolean isGoogleUser = false;
        if(!(authentication instanceof UsernamePasswordAuthenticationToken)) {
            isGoogleUser = true;
            OAuthUser oAuthUser = authenticationUtils.getOAuthUserFromAuthentication(authentication);
            if(oAuthUser.getGrantedScopes().contains(GoogleAccessService.SCOPE_GMAIL)){
                hasGoogleGmailAccess = true;
            }
        }
        model.addAttribute("customer", new Customer());
        model.addAttribute("isGoogleUser", isGoogleUser);
        model.addAttribute("hasGoogleGmailAccess", hasGoogleGmailAccess);

        return "customer/create-customer";
    }

    @PostMapping("/create-customer")
    public String createNewCustomer(@ModelAttribute("customer") @Validated Customer customer, BindingResult bindingResult,
                                    Authentication authentication, @RequestParam(value = "SendEmail", defaultValue = "false") boolean sendEmail, Model model) {

        if(bindingResult.hasErrors()) {
            boolean hasGoogleGmailAccess = false;
            boolean isGoogleUser = false;
            if(!(authentication instanceof UsernamePasswordAuthenticationToken)) {
                isGoogleUser = true;
                OAuthUser oAuthUser = authenticationUtils.getOAuthUserFromAuthentication(authentication);
                if(oAuthUser.getGrantedScopes().contains(GoogleAccessService.SCOPE_GMAIL)){
                    hasGoogleGmailAccess = true;
                }
            }
            model.addAttribute("isGoogleUser", isGoogleUser);
            model.addAttribute("hasGoogleGmailAccess", hasGoogleGmailAccess);
            return "customer/create-customer";
        }

        int userId = authenticationUtils.getLoggedInUserId(authentication);
        User user = userService.findById(userId);
        if(user.isInactiveUser()) {
            return "error/account-inactive";
        }
        customer.setUser(user);
        customer.setCreatedAt(LocalDateTime.now());

        CustomerLoginInfo customerLoginInfo = new CustomerLoginInfo();
        customerLoginInfo.setEmail(customer.getEmail());
        String token = EmailTokenUtils.generateToken();
        customerLoginInfo.setToken(token);
        customerLoginInfo.setPasswordSet(false);

        CustomerLoginInfo customerLoginInfo1 = customerLoginInfoService.save(customerLoginInfo);
        customer.setCustomerLoginInfo(customerLoginInfo1);
        Customer createdCustomer = customerService.save(customer);
        customerLoginInfo1.setCustomer(createdCustomer);

        if (!(authentication instanceof UsernamePasswordAuthenticationToken) && sendEmail && googleGmailApiService != null) {
            OAuthUser oAuthUser = authenticationUtils.getOAuthUserFromAuthentication(authentication);
            String baseUrl = environment.getProperty("app.base-url");
            String url = baseUrl + "set-password?token=" + customerLoginInfo.getToken();
            EmailTokenUtils.sendRegistrationEmail(customerLoginInfo1.getEmail(), customer.getName(), url, oAuthUser, googleGmailApiService);
        }
        return "redirect:/employee/customer/my-customers";
    }

    @PostMapping("/delete-customer/{id}")
    @Transactional
    public String deleteCustomer(@ModelAttribute("customer") Customer tempCustomer, BindingResult bindingResult ,@PathVariable("id") int id,
                                 Authentication authentication, RedirectAttributes redirectAttributes) {
        Customer customer;
        int userId = authenticationUtils.getLoggedInUserId(authentication);
        User user = userService.findById(userId);
        if(user.isInactiveUser()) {
            return "error/account-inactive";
        }
        try {
            if(!AuthorizationUtil.hasRole(authentication,"ROLE_MANAGER")) {
                bindingResult.rejectValue("failedErrorMessage", "error.failedErrorMessage",
                        "Sorry, you are not authorized to delete this customer. Only administrators have permission to delete customers.");
                redirectAttributes.addFlashAttribute("bindingResult", bindingResult);
                return "redirect:/employee/customer/my-customers";
            }

            customer = customerService.findByCustomerId(id);
            CustomerLoginInfo customerLoginInfo = customer.getCustomerLoginInfo();

            contractService.deleteAllByCustomer(customer);
            leadService.deleteAllByCustomer(customer);
            ticketService.deleteAllByCustomer(customer);

            customerLoginInfoService.delete(customerLoginInfo);
            customerService.delete(customer);

        } catch (Exception e){
            return "error/500";
        }
        return "redirect:/employee/customer/my-customers";
    }

    @PostMapping("/export-customer/{id}")
    @Transactional
    public ResponseEntity<String> exportCustomer(@PathVariable("id") int id, Authentication authentication) throws Exception {
    try {
        int userId = authenticationUtils.getLoggedInUserId(authentication);
        User user = userService.findById(userId);
            
        if(user.isInactiveUser()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account inactive");
        }
            
        if(!AuthorizationUtil.hasRole(authentication,"ROLE_MANAGER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access");
        }

        Customer customer = customerService.findByCustomerId(id);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }

        // Create a deep copy of the customer to modify email without affecting original
        Customer customerCopy = new Customer();
        BeanUtils.copyProperties(customer, customerCopy);
        customerCopy.setCustomerId(null); // Avoid ID conflict if imported
        customerCopy.setEmail("Copy_" + customer.getEmail());
            
        // Handle relationships
        if (customer.getBudgets() != null) {
            List<Budget> budgetCopies = customer.getBudgets().stream().map(budget -> {
                Budget budgetCopy = new Budget();
                BeanUtils.copyProperties(budget, budgetCopy);
                budgetCopy.setId(null);
                budgetCopy.setCustomer(customerCopy);
                return budgetCopy;
            }).collect(Collectors.toList());
            customerCopy.setBudgets(budgetCopies);
        }

        // Copie des dépenses avec leurs relations
        if (customer.getExpenses() != null) {
            System.out.println("isanyyy " + customer.getExpenses().size());
            List<Expense> expenseCopies = customer.getExpenses().stream().map(expense -> {
                Expense expenseCopy = new Expense();
                BeanUtils.copyProperties(expense, expenseCopy);
                expenseCopy.setId(null);
                    
                if (expense.getTicket() != null) {
                    Ticket ticketCopy = new Ticket();
                    BeanUtils.copyProperties(expense.getTicket(), ticketCopy);
                    ticketCopy.setTicketId(0);
                    // Ajoutez cette ligne pour éviter les références circulaires :
                    ticketCopy.setCustomer(null);
                    ticketCopy.setExpenses(null);
                    expenseCopy.setTicket(ticketCopy);
                }
                    
                if (expense.getLead() != null) {
                    Lead leadCopy = new Lead();
                    BeanUtils.copyProperties(expense.getLead(), leadCopy);
                    leadCopy.setLeadId(0);
                    // Ajoutez cette ligne :
                    leadCopy.setCustomer(null);
                    leadCopy.setExpenses(null);
                    expenseCopy.setLead(leadCopy);
                }
                    
                expenseCopy.setCustomer(customerCopy);
                return expenseCopy;
            }).collect(Collectors.toList());
            customerCopy.setExpenses(expenseCopies);
        }

        // Configuration de ObjectMapper
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            
        // Ignorer les propriétés Hibernate et circulaires
        om.addMixIn(Object.class, IgnoreHibernateProperties.class);
        om.addMixIn(Customer.class, IgnoreCustomerProperties.class);
        om.addMixIn(Ticket.class, IgnoreTicketProperties.class);
        om.addMixIn(Lead.class, IgnoreLeadProperties.class);
        om.addMixIn(Expense.class, IgnoreHibernateProperties.class);

        // Génération du JSON
        String json = om.writeValueAsString(customerCopy);

        // Retourner la réponse
        String fileName = "customer_export_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);

        } catch (Exception e) {
            throw e;
            // return ResponseEntity.internalServerError().body("Error exporting customer");
        }
    }

    // MixIn classes to handle Hibernate and circular references
    @JsonIgnoreProperties({
        "hibernateLazyInitializer", 
        "handler", 
        "persistentBag",
        "$$_hibernate_interceptor",
        "customer"
    })
    abstract class IgnoreHibernateProperties {}
    @JsonIgnoreProperties({
        "customerLoginInfo",
        "user"
    })
    abstract class IgnoreCustomerProperties {}
    @JsonIgnoreProperties({
        "expenses",
        "customer",
        "manager",
        "employee"
    })
    abstract class IgnoreTicketProperties {}
    @JsonIgnoreProperties({
        "expenses",
        "customer",
        "leadActions",
        "files",
        "googleDriveFiles",
        "manager",
        "employee"
    })
    abstract class IgnoreLeadProperties {}

}
