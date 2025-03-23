package site.easy.to.build.crm.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import site.easy.to.build.crm.entity.Expense;
import site.easy.to.build.crm.entity.Ticket;
import site.easy.to.build.crm.entity.DTO.DTOTicket;
import site.easy.to.build.crm.service.ticket.TicketService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ticket")
public class TicketApiController {

    @Autowired
    private TicketService ticketService;

    // Récupérer tous les tickets
    @GetMapping("/all-tickets")
    public ResponseEntity<List<DTOTicket>> getAllTickets() {
        List<Ticket> tickets = ticketService.findAll();
        List<DTOTicket> dtoTickets = tickets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoTickets);
    }

    // Méthode pour convertir un Ticket en DTOTicket
    private DTOTicket convertToDTO(Ticket ticket) {
        DTOTicket dtoTicket = new DTOTicket();
        dtoTicket.setId(ticket.getTicketId());
        dtoTicket.setSubject(ticket.getSubject());
        dtoTicket.setPriority(ticket.getPriority());
        dtoTicket.setStatus(ticket.getStatus());
        dtoTicket.setCustomer(ticket.getCustomer().getName()); // Supposons que Customer a une méthode getName()
        dtoTicket.setAssignedEmployee(ticket.getEmployee().getUsername()); // Supposons que User a une méthode getUsername()
        dtoTicket.setExpense(ticket.getExpenses().stream().mapToDouble(Expense::getAmount).sum()); // Supposons que Expense a une méthode getAmount()
        return dtoTicket;
    }

    // Créer un nouveau ticket
    @PostMapping("/create")
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        Ticket createdTicket = ticketService.save(ticket);
        return ResponseEntity.ok(createdTicket);
    }

    // Récupérer un ticket par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable int id) {
        Ticket ticket = ticketService.findByTicketId(id);
        if (ticket != null) {
            return ResponseEntity.ok(ticket);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Mettre à jour un ticket
    @PutMapping("/update/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable int id, @RequestBody Ticket ticket) {
        Ticket existingTicket = ticketService.findByTicketId(id);
        if (existingTicket != null) {
            ticket.setTicketId(id); // Assurez-vous que l'ID est correct
            Ticket updatedTicket = ticketService.save(ticket);
            return ResponseEntity.ok(updatedTicket);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer un ticket
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable int id) {
        Ticket ticket = ticketService.findByTicketId(id);
        if (ticket != null) {
            ticketService.delete(ticket);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
