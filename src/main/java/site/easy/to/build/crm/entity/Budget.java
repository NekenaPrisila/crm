package site.easy.to.build.crm.entity;

import jakarta.persistence.*;

@Entity
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Nom du budget
    private Double totalAmount; // Montant total du budget
    private Double currentAmount; // Montant actuellement utilisé

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer; // Relation avec le client

    public Budget() {
    }

    public Budget(String name, Double totalAmount, Double currentAmount, Customer customer) {
        this.name = name;
        this.totalAmount = totalAmount;
        this.currentAmount = currentAmount;
        this.customer = customer;
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

    public Double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(Double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    
}
