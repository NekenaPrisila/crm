package site.easy.to.build.crm.entity.DTO;

import java.util.List;

public class DTOBudget {
    private int id;

    private String description;

    private Double totaltotalAmount;
    
    private int customer;

    public DTOBudget(int id, Double totaltotalAmount, String description, int customer) {
        this.id = id;
        this.totaltotalAmount = totaltotalAmount;
        this.description = description;
        this.customer = customer;
    }

    public DTOBudget() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }

    public Double getTotaltotalAmount() {
        return totaltotalAmount;
    }

    public void setTotaltotalAmount(Double totaltotalAmount) {
        this.totaltotalAmount = totaltotalAmount;
    }

    public List<DTOBudget> getBudgetList() {
        return budgetList;
    }

    public void setBudgetList(List<DTOBudget> budgetList) {
        this.budgetList = budgetList;
    }
}

