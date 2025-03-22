package site.easy.to.build.crm.entity;

import jakarta.persistence.*;

@Entity
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description; // Description du budget
    private String name; // Nom du budget
    private Double totalAmount; // Montant total du budget

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer; // Relation avec le client

    public Budget(String name, String description, Double totalAmount, Customer customer) {
        this.name = name;
        this.description = description;
        this.totalAmount = totalAmount;
        this.customer = customer;
    }

    public Budget() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    
}
