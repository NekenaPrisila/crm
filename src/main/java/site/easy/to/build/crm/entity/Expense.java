package site.easy.to.build.crm.entity;

import jakarta.persistence.*;

@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount; // Montant de la dépense
    private String description; // Description de la dépense

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket; // Relation avec un ticket

    @ManyToOne
    @JoinColumn(name = "lead_id")
    private Lead lead; // Relation avec un lead

    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget; // Relation avec un budget

    public Expense() {
    }

    public Expense(Double amount, String description, Ticket ticket, Lead lead, Budget budget) {
        this.amount = amount;
        this.description = description;
        this.ticket = ticket;
        this.lead = lead;
        this.budget = budget;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Lead getLead() {
        return lead;
    }

    public void setLead(Lead lead) {
        this.lead = lead;
    }

    public Budget getBudget() {
        return budget;
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
    }

}
