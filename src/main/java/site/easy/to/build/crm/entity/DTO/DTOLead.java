package site.easy.to.build.crm.entity.DTO;

import java.time.LocalDateTime;

public class DTOLead {
    private int id;
    private String status;
    private String customer;
    private String assignedEmployee;
    private LocalDateTime createdAt;
    private double expense;

    public DTOLead(int id, String status, String customer, String assignedEmployee, LocalDateTime createdAt,
            double expense) {
        this.id = id;
        this.status = status;
        this.customer = customer;
        this.assignedEmployee = assignedEmployee;
        this.createdAt = createdAt;
        this.expense = expense;
    }

    // Constructeurs
    public DTOLead() {
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getAssignedEmployee() {
        return assignedEmployee;
    }

    public void setAssignedEmployee(String assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }

    public double getExpense() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
