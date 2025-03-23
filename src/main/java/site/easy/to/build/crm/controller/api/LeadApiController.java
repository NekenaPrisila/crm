package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.Lead;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.DTO.DTOLead;
import site.easy.to.build.crm.entity.DTO.DTOTicket;
import site.easy.to.build.crm.service.lead.LeadService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lead")
public class LeadApiController {

    @Autowired
    private LeadService leadService;

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
        dtoLead.setExpense(Lead.getExpenses().stream().mapToDouble(Expense::getAmount).sum()); // Supposons que Expense a une méthode getAmount()
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
}
