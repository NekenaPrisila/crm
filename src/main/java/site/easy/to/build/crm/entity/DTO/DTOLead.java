package site.easy.to.build.crm.entity.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class DTOLead {
    private int id;
    private String status;
    private String customer;
    private String assignedEmployee;
    private LocalDateTime createdAt;
    private List<DTOExpense> expenseList;

    public DTOLead(int id, String status, String customer, String assignedEmployee, LocalDateTime createdAt,
            List<DTOExpense> expenseList) {
        this.id = id;
        this.status = status;
        this.customer = customer;
        this.assignedEmployee = assignedEmployee;
        this.createdAt = createdAt;
        this.expenseList = expenseList;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<DTOExpense> getExpenseList() {
        return expenseList;
    }

    public void setExpenseList(List<DTOExpense> expenseList) {
        this.expenseList = expenseList;
    }
}
