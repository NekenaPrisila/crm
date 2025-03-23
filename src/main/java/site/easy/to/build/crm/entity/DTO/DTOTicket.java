package site.easy.to.build.crm.entity.DTO;

public class DTOTicket {
    private int id;
    private String subject;
    private String priority;
    private String status;
    private String customer;
    private String assignedEmployee;
    private double expense;

    // Constructeurs
    public DTOTicket() {
    }

    public DTOTicket(int id, String subject, String priority, String status, String customer, String assignedEmployee, double expense) {
        this.id = id;
        this.subject = subject;
        this.priority = priority;
        this.status = status;
        this.customer = customer;
        this.assignedEmployee = assignedEmployee;
        this.expense = expense;
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

    public double getExpense() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }
}