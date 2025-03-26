package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.DTO.DTOExpense;
import site.easy.to.build.crm.entity.DTO.DTOLead;
import site.easy.to.build.crm.service.expense.ExpenseService;
import site.easy.to.build.crm.service.lead.LeadService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lead")
public class LeadApiController {

    @Autowired
    private LeadService leadService;

    @Autowired
    private ExpenseService expenseService;

    // Récupérer tous les leads
    @GetMapping("/all-leads")
    public ResponseEntity<List<DTOLead>> getAllLeads() {
        List<Lead> leads = leadService.findAll();
        List<DTOLead> dtoLeads = leads.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoLeads);
    }

    // Méthode pour convertir un Ticket en DTOTicket
    private DTOLead convertToDTO(Lead Lead) {
        DTOLead dtoLead = new DTOLead();
        dtoLead.setId(Lead.getLeadId());
        dtoLead.setStatus(Lead.getStatus());
        dtoLead.setCustomer(Lead.getCustomer().getName()); // Supposons que Customer a une méthode getName()
        dtoLead.setAssignedEmployee(Lead.getEmployee().getUsername()); // Supposons que User a une méthode getUsername()
        dtoLead.setCreatedAt(Lead.getCreatedAt());
        // Supposons que ticket.getExpenses() retourne une List<Expense>
        List<DTOExpense> dtoExpenses = Lead.getExpenses().stream()
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
        dtoLead.setExpenseList(dtoExpenses);
        return dtoLead;
    }

    // Créer un nouveau lead

    @PostMapping("/create")
    public ResponseEntity<Lead> createLead(@RequestBody Lead lead) {
        Lead createdLead = leadService.save(lead);
        return ResponseEntity.ok(createdLead);
    }

    // Récupérer un lead par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Lead> getLeadById(@PathVariable int id) {
        Lead lead = leadService.findByLeadId(id);
        if (lead != null) {
            return ResponseEntity.ok(lead);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Mettre à jour un lead
    @PutMapping("/update/{id}")
    public ResponseEntity<Lead> updateLead(@PathVariable int id, @RequestBody Lead lead) {
        Lead existingLead = leadService.findByLeadId(id);
        if (existingLead != null) {
            lead.setLeadId(id); // Assurez-vous que l'ID est correct
            Lead updatedLead = leadService.save(lead);
            return ResponseEntity.ok(updatedLead);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer un lead
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable int id) {
        Lead lead = leadService.findByLeadId(id);
        if (lead != null) {
            leadService.delete(lead);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/update-expense")
    public ResponseEntity<Void> updateLeadExpense(
        @RequestBody Map<String, Object> requestBody) {

        System.out.println("Received data: " + requestBody);

        // Récupérer le lead par son ID
        Lead lead = leadService.findByLeadId((int) requestBody.get("id"));
        if (lead == null) {
            return ResponseEntity.notFound().build();
        }

        Double newAmount = (Double) requestBody.get("expense"); // Conversion explicite

        // Trouver la première dépense associée au lead (ou une logique spécifique si nécessaire)
        Expense expenseToUpdate = lead.getExpenses().stream()
            .findFirst()
            .orElse(null);

        if (expenseToUpdate == null) {
            return ResponseEntity.notFound().build();
        }

        // Mettre à jour le montant de la dépense
        expenseToUpdate.setAmount(newAmount);

        // Sauvegarder la dépense mise à jour
        expenseService.createExpense(expenseToUpdate);

        return ResponseEntity.ok().build();
    }
    
}
