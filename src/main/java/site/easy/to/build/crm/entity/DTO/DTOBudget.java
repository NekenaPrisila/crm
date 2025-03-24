package site.easy.to.build.crm.entity.DTO;


public class DTOBudget {
    private int id;

    private String description;

    private Double totalAmount;
    
    private int customer;

    public DTOBudget(int id, Double totalAmount, String description, int customer) {
        this.id = id;
        this.totalAmount = totalAmount;
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

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}

