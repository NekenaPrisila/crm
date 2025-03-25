package site.easy.to.build.crm.service.database;

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

    @Transactional
    public CsvImportResult processFiles(MultipartFile customerFile, MultipartFile budgetFile, MultipartFile ticketLeadFile) {
        List<CsvValidationError> allErrors = new ArrayList<>();
        List<Object> allEntities = new ArrayList<>();

        try {
            // 1. Process Customer file
            if (customerFile != null && !customerFile.isEmpty()) {
                CsvImportResult customerResult = processCustomerCsv(
                    new InputStreamReader(customerFile.getInputStream()),
                    customerFile.getOriginalFilename()
                );
                allErrors.addAll(customerResult.getErrors());
                allEntities.addAll(customerResult.getValidEntities());
            }

            // 2. Process Budget file
            if (budgetFile != null && !budgetFile.isEmpty()) {
                CsvImportResult budgetResult = processBudgetCsv(
                    new InputStreamReader(budgetFile.getInputStream()),
                    budgetFile.getOriginalFilename()
                );
                allErrors.addAll(budgetResult.getErrors());
                allEntities.addAll(budgetResult.getValidEntities());
            }

            // 3. Process Ticket/Lead file
            if (ticketLeadFile != null && !ticketLeadFile.isEmpty()) {
                CsvImportResult ticketLeadResult = processTicketLeadCsv(
                    new InputStreamReader(ticketLeadFile.getInputStream()),
                    ticketLeadFile.getOriginalFilename()
                );
                allErrors.addAll(ticketLeadResult.getErrors());
                allEntities.addAll(ticketLeadResult.getValidEntities());
            }

            return new CsvImportResult(allEntities, allErrors);

        } catch (Exception e) {
            throw new RuntimeException("Failed to process CSV files: " + e.getMessage(), e);
        }
    }

    private CsvImportResult processCustomerCsv(Reader reader, String fileName) {
        List<CsvValidationError> errors = new ArrayList<>();
        List<Customer> validCustomers = new ArrayList<>();

        CsvToBean<CustomerCsvDto> csvToBean = new CsvToBeanBuilder<CustomerCsvDto>(reader)
            .withType(CustomerCsvDto.class)
            .withIgnoreLeadingWhiteSpace(true)
            .build();

        List<CustomerCsvDto> dtos = csvToBean.parse();

        // for (CustomerCsvDto customerCsvDto : dtos) {
        //     System.out.println("name: " + customerCsvDto.getCustomer_name());
        //     System.out.println("email: " + customerCsvDto.getCustomer_email());
        // }

        for (int i = 0; i < dtos.size(); i++) {
            int lineNumber = i + 2; // +1 for header, +1 for 0-based index
            CustomerCsvDto dto = dtos.get(i);

            Set<ConstraintViolation<CustomerCsvDto>> violations = validator.validate(dto);
            if (!violations.isEmpty()) {
                violations.forEach(v -> errors.add(
                    new CsvValidationError(fileName, lineNumber, v.getPropertyPath().toString(), v.getMessage())
                ));
                continue;
            }

            try {
                Customer customer = customerRepository.findByEmail(dto.getCustomer_email());
                if (customer == null) {
                    customer = new Customer();
                    customer.setEmail(dto.getCustomer_email());
                    customer.setName(dto.getCustomer_name());
                    customer.setCreatedAt(LocalDateTime.now());
                    validCustomers.add(customerRepository.save(customer));
                }
            } catch (Exception e) {
                errors.add(new CsvValidationError(fileName, lineNumber, "Processing", e.getMessage()));
            }
        }
        

        return new CsvImportResult(validCustomers, errors);
    }

    private CsvImportResult processBudgetCsv(Reader reader, String fileName) {
        List<CsvValidationError> errors = new ArrayList<>();
        List<Budget> validBudgets = new ArrayList<>();
    
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
    
            try {
                Customer customer = customerRepository.findByEmail(dto.getCustomerEmail());
                if (customer == null) {
                    errors.add(new CsvValidationError(fileName, lineNumber, "customerEmail", 
                        "Customer not found: " + dto.getCustomerEmail()));
                    continue;
                }
    
                Budget budget = new Budget();
                budget.setCustomer(customer);
                budget.setTotalAmount(new BigDecimal(dto.getBudget().replace(",", "")).doubleValue());
                
                // Génération des valeurs manquantes
                budget.setName("Budget " + customer.getName() + " " + LocalDateTime.now().getYear());
                budget.setDescription("Budget importé pour " + customer.getName() + " - " + 
                                   LocalDateTime.now().getYear());
    
                validBudgets.add(budgetRepository.save(budget));
            } catch (Exception e) {
                errors.add(new CsvValidationError(fileName, lineNumber, "Processing", e.getMessage()));
            }
        }
    
        return new CsvImportResult(validBudgets, errors);
    }

    private CsvImportResult processTicketLeadCsv(Reader reader, String fileName) {
        List<CsvValidationError> errors = new ArrayList<>();
        List<Object> validEntities = new ArrayList<>();

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

            try {
                Customer customer = customerRepository.findByEmail(dto.getCustomerEmail());
                if (customer == null) {
                    errors.add(new CsvValidationError(fileName, lineNumber, "customerEmail", 
                        "Customer not found: " + dto.getCustomerEmail()));
                    continue;
                }

                if ("lead".equalsIgnoreCase(dto.getType())) {
                    Lead lead = new Lead();
                    lead.setName(dto.getSubjectOrName());
                    lead.setStatus(dto.getStatus());
                    lead.setCustomer(customer);
                    lead.setCreatedAt(LocalDateTime.now());

                    if (dto.getExpense() != null) {
                        Expense expense = new Expense();
                        expense.setAmount(new BigDecimal(dto.getExpense().replace(",", ".")).doubleValue());
                        expense.setDescription("From CSV import");
                        expense.setLead(lead);
                        lead.getExpenses().add(expense);
                    }

                    validEntities.add(leadRepository.save(lead));
                } else {
                    Ticket ticket = new Ticket();
                    ticket.setSubject(dto.getSubjectOrName());
                    ticket.setStatus(dto.getStatus());
                    ticket.setPriority("medium"); // Default value
                    ticket.setCustomer(customer);
                    ticket.setCreatedAt(LocalDateTime.now());

                    if (dto.getExpense() != null) {
                        Expense expense = new Expense();
                        expense.setAmount(new BigDecimal(dto.getExpense().replace(",", ".")).doubleValue());
                        expense.setDescription("From CSV import");
                        expense.setTicket(ticket);
                        ticket.getExpenses().add(expense);
                    }

                    validEntities.add(ticketRepository.save(ticket));
                }
            } catch (Exception e) {
                errors.add(new CsvValidationError(fileName, lineNumber, "Processing", e.getMessage()));
            }
        }

        return new CsvImportResult(validEntities, errors);
    }
}