package site.easy.to.build.crm.entity.DTO;

import java.util.List;

public class DTOTicket {
    private int id;
    private String subject;
    private String priority;
    private String status;
    private String customer;
    private String assignedEmployee;
    private List<DTOExpense> expenseList;

    public DTOTicket(int id, String subject, String priority, String status, String customer, String assignedEmployee,
            List<DTOExpense> expenseList) {
        this.id = id;
        this.subject = subject;
        this.priority = priority;
        this.status = status;
        this.customer = customer;
        this.assignedEmployee = assignedEmployee;
        this.expenseList = expenseList;
    }

    // Constructeurs
    public DTOTicket() {
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
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


    public List<DTOExpense> getExpenseList() {
        return expenseList;
    }


    public void setExpenseList(List<DTOExpense> expenseList) {
        this.expenseList = expenseList;
    }

}