package site.easy.to.build.crm.service.database;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import site.easy.to.build.crm.entity.Budget;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.repository.BudgetRepository;
import site.easy.to.build.crm.repository.CustomerRepository;
import site.easy.to.build.crm.repository.ExpenseRepository;
import site.easy.to.build.crm.repository.LeadRepository;
import site.easy.to.build.crm.repository.TicketRepository;

@Service
@Transactional
public class CustomerImportService {

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private LeadRepository leadRepository;

    public Customer processImport(Map<String, Object> jsonData) {
        // 1. Création du Customer
        Customer customer = new Customer();
        customer.setName((String) jsonData.get("name"));
        customer.setEmail((String) jsonData.get("email"));
        customer.setPosition((String) jsonData.get("position"));
        customer.setPhone((String) jsonData.get("phone"));
        customer.setAddress((String) jsonData.get("address"));
        customer.setCity((String) jsonData.get("city"));
        customer.setState((String) jsonData.get("state"));
        customer.setCountry((String) jsonData.get("country"));
        customer.setDescription((String) jsonData.get("description"));
        customer.setTwitter((String) jsonData.get("twitter"));
        customer.setFacebook((String) jsonData.get("facebook"));
        customer.setYoutube((String) jsonData.get("youtube"));
        
        // Conversion de la date createdAt
        if (jsonData.get("createdAt") != null) {
            customer.setCreatedAt(LocalDateTime.parse((String) jsonData.get("createdAt")));
        }

        Customer newCustomer = customerRepository.save(customer);

        // 2. Traitement des budgets
        if (jsonData.containsKey("budgets")) {
            List<Map<String, Object>> budgetsData = (List<Map<String, Object>>) jsonData.get("budgets");
            List<Budget> budgets = budgetsData.stream()
                .map(this::mapToBudget)
                .peek(b -> b.setCustomer(newCustomer))
                .collect(Collectors.toList());
            customer.setBudgets(budgets);
            budgetRepository.saveAll(budgets);
        }

        // 3. Traitement des expenses
        if (jsonData.containsKey("expenses")) {
            List<Map<String, Object>> expensesData = (List<Map<String, Object>>) jsonData.get("expenses");
            List<Expense> expenses = expensesData.stream()
                .map(expenseData -> {
                    Expense expense = mapToExpense(expenseData);
                    expense.setCustomer(newCustomer);
                    
                    // Gestion des relations Ticket
                    if (expenseData.containsKey("ticket") && expenseData.get("ticket") != null) {
                        Map<String, Object> ticketData = (Map<String, Object>) expenseData.get("ticket");
                        Ticket ticket = mapToTicket(ticketData);
                        ticket.setCustomer(newCustomer);
                        ticket = ticketRepository.save(ticket);
                        expense.setTicket(ticket);
                    }
                    
                    // Gestion des relations Lead
                    if (expenseData.containsKey("lead") && expenseData.get("lead") != null) {
                        Map<String, Object> leadData = (Map<String, Object>) expenseData.get("lead");
                        Lead lead = mapToLead(leadData);
                        lead.setCustomer(newCustomer);
                        lead = leadRepository.save(lead);
                        expense.setLead(lead);
                    }
                    
                    return expense;
                })
                .collect(Collectors.toList());
            customer.setExpenses(expenses);
        }

        // Sauvegarde finale
        return customerRepository.save(customer);
    }

    private Budget mapToBudget(Map<String, Object> data) {
        Budget budget = new Budget();
        budget.setDescription((String) data.get("description"));
        budget.setName((String) data.get("name"));
        
        // Gestion du montant (Double ou Integer)
        Object amount = data.get("totalAmount");
        if (amount instanceof Double) {
            budget.setTotalAmount((Double) amount);
        } else if (amount instanceof Integer) {
            budget.setTotalAmount(((Integer) amount).doubleValue());
        }
        
        return budget;
    }

    private Expense mapToExpense(Map<String, Object> data) {
        Expense expense = new Expense();
        
        // Gestion du montant (Double ou Integer)
        Object amount = data.get("amount");
        if (amount instanceof Double) {
            expense.setAmount((Double) amount);
        } else if (amount instanceof Integer) {
            expense.setAmount(((Integer) amount).doubleValue());
        }
        
        expense.setDescription((String) data.get("description"));
        return expense;
    }
    
    private Ticket mapToTicket(Map<String, Object> data) {
        Ticket ticket = new Ticket();
        ticket.setTicketId((Integer) data.get("ticketId"));
        ticket.setSubject((String) data.get("subject"));
        ticket.setDescription((String) data.get("description"));
        ticket.setStatus((String) data.get("status"));
        ticket.setPriority((String) data.get("priority"));
        
        if (data.get("createdAt") != null) {
            ticket.setCreatedAt(LocalDateTime.parse((String) data.get("createdAt")));
        }
        
        return ticket;
    }
    
    private Lead mapToLead(Map<String, Object> data) {
        Lead lead = new Lead();
        lead.setLeadId((Integer) data.get("leadId"));
        lead.setName((String) data.get("name"));
        lead.setStatus((String) data.get("status"));
        lead.setPhone((String) data.get("phone"));
        lead.setMeetingId((String) data.get("meetingId"));
        
        // Gestion du booléen googleDrive (peut être null, Boolean ou String)
        Object googleDrive = data.get("googleDrive");
        if (googleDrive != null) {
            if (googleDrive instanceof Boolean) {
                lead.setGoogleDrive((Boolean) googleDrive);
            } else if (googleDrive instanceof String) {
                lead.setGoogleDrive(Boolean.parseBoolean((String) googleDrive));
            }
        }
        
        lead.setGoogleDriveFolderId((String) data.get("googleDriveFolderId"));
        
        if (data.get("createdAt") != null) {
            lead.setCreatedAt(LocalDateTime.parse((String) data.get("createdAt")));
        }
        
        return lead;
    }
}
