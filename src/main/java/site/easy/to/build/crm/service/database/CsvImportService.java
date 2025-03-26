package site.easy.to.build.crm.service.database;

import com.github.javafaker.Faker;
import com.opencsv.bean.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.entity.csv.*;
import site.easy.to.build.crm.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Validated
public class CsvImportService {
    
    @Autowired private Validator validator;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private LeadRepository leadRepository;
    @Autowired private TicketRepository ticketRepository;
    @Autowired private BudgetRepository budgetRepository;
    @Autowired private CustomerLoginInfoRepository customerLoginInfoRepository;

    @Transactional
    public CsvImportResult processFiles(MultipartFile customerFile, MultipartFile budgetFile, MultipartFile ticketLeadFile, User user) throws Exception {
        List<CsvValidationError> allErrors = new ArrayList<>();
        List<Object> allEntities = new ArrayList<>();
        Faker faker = new Faker(new Locale("fr"));

        // Phase 1: Validation de tous les fichiers
        Map<String, Customer> tempCustomers = new HashMap<>();
        
        // 1. Valider et préparer les Customers
        if (customerFile != null && !customerFile.isEmpty()) {
            CsvImportResult customerResult = validateAndPrepareCustomers(
                new InputStreamReader(customerFile.getInputStream()),
                customerFile.getOriginalFilename(),
                user,
                faker,
                tempCustomers
            );
            allErrors.addAll(customerResult.getErrors());
        }

        // 2. Valider les Budgets (vérifie que les customers existent)
        if (budgetFile != null && !budgetFile.isEmpty()) {
            CsvImportResult budgetResult = validateBudgets(
                new InputStreamReader(budgetFile.getInputStream()),
                budgetFile.getOriginalFilename(),
                tempCustomers
            );
            allErrors.addAll(budgetResult.getErrors());
        }

        // 3. Valider les Tickets/Leads (vérifie que les customers existent)
        if (ticketLeadFile != null && !ticketLeadFile.isEmpty()) {
            CsvImportResult ticketLeadResult = validateTicketsLeads(
                new InputStreamReader(ticketLeadFile.getInputStream()),
                ticketLeadFile.getOriginalFilename(),
                tempCustomers
            );
            allErrors.addAll(ticketLeadResult.getErrors());
        }

        // Si erreurs détectées, on arrête avant toute insertion
        if (!allErrors.isEmpty()) {
            return new CsvImportResult(Collections.emptyList(), allErrors);
        }

        // Phase 2: Insertion des données (seulement si validation OK)
        
        // 1. Insérer les Customers
        if (customerFile != null && !customerFile.isEmpty()) {
            CsvImportResult customerResult = insertCustomers(tempCustomers.values());
            allEntities.addAll(customerResult.getValidEntities());
        }

        // 2. Insérer les Budgets
        if (budgetFile != null && !budgetFile.isEmpty()) {
            CsvImportResult budgetResult = insertBudgets(
                new InputStreamReader(budgetFile.getInputStream()),
                budgetFile.getOriginalFilename(),
                user,
                faker
            );
            allEntities.addAll(budgetResult.getValidEntities());
        }

        // 3. Insérer les Tickets/Leads
        if (ticketLeadFile != null && !ticketLeadFile.isEmpty()) {
            CsvImportResult ticketLeadResult = insertTicketsLeads(
                new InputStreamReader(ticketLeadFile.getInputStream()),
                ticketLeadFile.getOriginalFilename(),
                user,
                faker
            );
            allEntities.addAll(ticketLeadResult.getValidEntities());
        }

        return new CsvImportResult(allEntities, allErrors);
    }

    // private boolean validateCsvHeaders(Reader reader, String[] expectedHeaders, String fileName) throws IOException {
    //     try (BufferedReader br = new BufferedReader(reader)) {
    //         String firstLine = br.readLine();
    //         if (firstLine == null) {
    //             throw new IOException("Fichier CSV vide");
    //         }
            
    //         String[] actualHeaders = firstLine.split(",");
    //         if (actualHeaders.length != expectedHeaders.length) {
    //             return false;
    //         }
            
    //         for (int i = 0; i < expectedHeaders.length; i++) {
    //             if (!expectedHeaders[i].equalsIgnoreCase(actualHeaders[i].trim())) {
    //                 return false;
    //             }
    //         }
    //         return true;
    //     }
    // }

    // Méthodes de validation et préparation
    private CsvImportResult validateAndPrepareCustomers(Reader reader, String fileName, User user, Faker faker, Map<String, Customer> tempCustomers) {
        List<CsvValidationError> errors = new ArrayList<>();
        List<Customer> customers = new ArrayList<>();

        CsvToBean<CustomerCsvDto> csvToBean = new CsvToBeanBuilder<CustomerCsvDto>(reader)
            .withType(CustomerCsvDto.class)
            .withIgnoreLeadingWhiteSpace(true)
            .build();

        List<CustomerCsvDto> dtos = csvToBean.parse();

        for (int i = 0; i < dtos.size(); i++) {
            int lineNumber = i + 2;
            CustomerCsvDto dto = dtos.get(i);

            if (tempCustomers.containsKey(dto.getCustomer_email())) {
                errors.add(new CsvValidationError(
                    fileName, 
                    lineNumber, 
                    "customer_email", 
                    "Email en double dans le fichier CSV"
                ));
                continue;
            }

            Set<ConstraintViolation<CustomerCsvDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                violations.forEach(v -> errors.add(
                    new CsvValidationError(fileName, lineNumber, v.getPropertyPath().toString(), v.getMessage())
                ));
                continue;
            }

            // Préparation de l'objet Customer sans le persister
            Customer customer = new Customer();
            customer.setEmail(dto.getCustomer_email());
            customer.setName(dto.getCustomer_name());
            customer.setPhone("+261 " + faker.phoneNumber().subscriberNumber(8));
            customer.setAddress(faker.address().streetAddress());
            customer.setCity(faker.address().city());
            customer.setState(faker.address().state());
            customer.setCountry(faker.country().name());
            customer.setUser(user);
            customer.setCreatedAt(LocalDateTime.now());
            
            // Préparation du CustomerLoginInfo
            CustomerLoginInfo loginInfo = new CustomerLoginInfo();
            loginInfo.setEmail(dto.getCustomer_email());
            loginInfo.setPasswordSet(false);
            customer.setCustomerLoginInfo(loginInfo);
            
            tempCustomers.put(dto.getCustomer_email(), customer);
        }
        
        return new CsvImportResult(customers, errors);
    }

    private CsvImportResult validateBudgets(Reader reader, String fileName, Map<String, Customer> tempCustomers) {
        List<CsvValidationError> errors = new ArrayList<>();

        CsvToBean<BudgetCsvDto> csvToBean = new CsvToBeanBuilder<BudgetCsvDto>(reader)
            .withType(BudgetCsvDto.class)
            .withIgnoreLeadingWhiteSpace(true)
            .build();

        List<BudgetCsvDto> dtos = csvToBean.parse();

        for (int i = 0; i < dtos.size(); i++) {
            int lineNumber = i + 2;
            BudgetCsvDto dto = dtos.get(i);

            Set<ConstraintViolation<BudgetCsvDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                violations.forEach(v -> errors.add(
                    new CsvValidationError(fileName, lineNumber, v.getPropertyPath().toString(), v.getMessage())
                ));
                continue;
            }

            // Vérification que le customer existe (dans les nouveaux ou en base)
            if (!tempCustomers.containsKey(dto.getCustomerEmail()) && 
                customerRepository.findByEmail(dto.getCustomerEmail()) == null) {
                errors.add(new CsvValidationError(
                    fileName, lineNumber, "customerEmail", 
                    "Customer not found: " + dto.getCustomerEmail()
                ));
            }
        }

        return new CsvImportResult(Collections.emptyList(), errors);
    }

    private CsvImportResult validateTicketsLeads(Reader reader, String fileName, Map<String, Customer> tempCustomers) {
        List<CsvValidationError> errors = new ArrayList<>();

        CsvToBean<TicketLeadCsvDto> csvToBean = new CsvToBeanBuilder<TicketLeadCsvDto>(reader)
            .withType(TicketLeadCsvDto.class)
            .withIgnoreLeadingWhiteSpace(true)
            .build();

        List<TicketLeadCsvDto> dtos = csvToBean.parse();

        for (int i = 0; i < dtos.size(); i++) {
            int lineNumber = i + 2;
            TicketLeadCsvDto dto = dtos.get(i);

            Set<ConstraintViolation<TicketLeadCsvDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                violations.forEach(v -> errors.add(
                    new CsvValidationError(fileName, lineNumber, v.getPropertyPath().toString(), v.getMessage())
                ));
                continue;
            }

            // Vérification que le customer existe (dans les nouveaux ou en base)
            if (!tempCustomers.containsKey(dto.getCustomerEmail())) {
                Customer existingCustomer = customerRepository.findByEmail(dto.getCustomerEmail());
                if (existingCustomer == null) {
                    errors.add(new CsvValidationError(
                        fileName, lineNumber, "customerEmail", 
                        "Customer not found: " + dto.getCustomerEmail()
                    ));
                }
            }
        }

        return new CsvImportResult(Collections.emptyList(), errors);
    }

    // Méthodes d'insertion
    private CsvImportResult insertCustomers(Collection<Customer> customers) {
        List<Customer> savedCustomers = new ArrayList<>();
        List<CsvValidationError> errors = new ArrayList<>();

        for (Customer customer : customers) {
            try {
                // Vérifier si le customer existe déjà
                if (customerRepository.findByEmail(customer.getEmail()) == null) {
                    // Sauvegarder d'abord le login info
                    CustomerLoginInfo savedLoginInfo = customerLoginInfoRepository.save(customer.getCustomerLoginInfo());
                    customer.setCustomerLoginInfo(savedLoginInfo);
                    
                    // Puis sauvegarder le customer
                    savedCustomers.add(customerRepository.save(customer));
                }
            } catch (Exception e) {
                errors.add(new CsvValidationError("Customers", 0, "Processing", e.getMessage()));
                throw new RuntimeException("Error saving customers", e); // Rollback transaction
            }
        }

        return new CsvImportResult(savedCustomers, errors);
    }

    private CsvImportResult insertBudgets(Reader reader, String fileName, User user, Faker faker) {
        List<Budget> budgets = new ArrayList<>();
        List<CsvValidationError> errors = new ArrayList<>();

        CsvToBean<BudgetCsvDto> csvToBean = new CsvToBeanBuilder<BudgetCsvDto>(reader)
            .withType(BudgetCsvDto.class)
            .withIgnoreLeadingWhiteSpace(true)
            .build();

        List<BudgetCsvDto> dtos = csvToBean.parse();

        for (int i = 0; i < dtos.size(); i++) {
            int lineNumber = i + 2;
            BudgetCsvDto dto = dtos.get(i);

            try {
                // Rechercher le customer (doit exister après validation)
                Customer customer = customerRepository.findByEmail(dto.getCustomerEmail());
                if (customer == null) {
                    throw new RuntimeException("Customer not found: " + dto.getCustomerEmail());
                }

                Budget budget = new Budget();
                budget.setCustomer(customer);
                budget.setTotalAmount(new BigDecimal(dto.getBudget().replace(",", ".")).doubleValue());
                budget.setName("Budget " + customer.getName() + " " + LocalDateTime.now().getYear());
                budget.setDescription("Budget importé pour " + customer.getName() + " - " + 
                                   LocalDateTime.now().getYear());

                budgets.add(budgetRepository.save(budget));
            } catch (Exception e) {
                errors.add(new CsvValidationError(fileName, lineNumber, "Processing", e.getMessage()));
                throw new RuntimeException("Error saving budgets", e); // Rollback transaction
            }
        }

        return new CsvImportResult(budgets, errors);
    }

    private CsvImportResult insertTicketsLeads(Reader reader, String fileName, User user, Faker faker) {
        List<Object> entities = new ArrayList<>();
        List<CsvValidationError> errors = new ArrayList<>();
    
        CsvToBean<TicketLeadCsvDto> csvToBean = new CsvToBeanBuilder<TicketLeadCsvDto>(reader)
            .withType(TicketLeadCsvDto.class)
            .withIgnoreLeadingWhiteSpace(true)
            .build();
    
        List<TicketLeadCsvDto> dtos = csvToBean.parse();
    
        for (int i = 0; i < dtos.size(); i++) {
            int lineNumber = i + 2;
            TicketLeadCsvDto dto = dtos.get(i);
    
            try {
                Customer customer = customerRepository.findByEmail(dto.getCustomerEmail());
                if (customer == null) {
                    throw new RuntimeException("Customer not found: " + dto.getCustomerEmail());
                }
    
                if ("lead".equalsIgnoreCase(dto.getType())) {
                    // Étape 1: Créer et sauvegarder le Lead sans Expense
                    Lead lead = new Lead();
                    lead.setName(dto.getSubjectOrName());
                    lead.setStatus(dto.getStatus());
                    lead.setPhone("+261 " + faker.phoneNumber().subscriberNumber(8));
                    lead.setCustomer(customer);
                    lead.setCreatedAt(LocalDateTime.now());
                    lead.setEmployee(user);
                    
                    // Sauvegarde initiale pour obtenir l'ID
                    lead = leadRepository.save(lead);
                    
                    // Étape 2: Créer l'Expense si nécessaire
                    if (dto.getExpense() != null) {
                        Expense expense = new Expense();
                        expense.setAmount(new BigDecimal(dto.getExpense().replace(",", ".")).doubleValue());
                        expense.setDescription("From CSV import");
                        expense.setLead(lead);
                        expense.setCustomer(customer); // Associer aussi le customer
                        
                        // Ajouter à la collection et sauvegarder
                        lead.getExpenses().add(expense);
                        lead = leadRepository.save(lead); // Mise à jour
                    }
                    
                    entities.add(lead);
                } else {
                    // Même logique pour les Tickets
                    Ticket ticket = new Ticket();
                    ticket.setSubject(dto.getSubjectOrName());
                    ticket.setStatus(dto.getStatus());
                    ticket.setPriority("medium");
                    ticket.setCustomer(customer);
                    ticket.setCreatedAt(LocalDateTime.now());
                    ticket.setEmployee(user);
                    
                    // Sauvegarde initiale
                    ticket = ticketRepository.save(ticket);
                    
                    if (dto.getExpense() != null) {
                        Expense expense = new Expense();
                        expense.setAmount(new BigDecimal(dto.getExpense().replace(",", ".")).doubleValue());
                        expense.setDescription("From CSV import");
                        expense.setTicket(ticket);
                        expense.setCustomer(customer);
                        
                        ticket.getExpenses().add(expense);
                        ticket = ticketRepository.save(ticket);
                    }
                    
                    entities.add(ticket);
                }
            } catch (Exception e) {
                errors.add(new CsvValidationError(fileName, lineNumber, "Processing", e.getMessage()));
                throw new RuntimeException("Error saving tickets/leads", e);
            }
        }
        return new CsvImportResult(entities, errors);
    }
}
