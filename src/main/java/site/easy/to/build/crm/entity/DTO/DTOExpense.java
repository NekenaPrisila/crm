package site.easy.to.build.crm.entity.DTO;


public class DTOExpense {
    private int id;

    private Double amount;

    private String description;

    private int customer;

    public DTOExpense() {
    }

    public DTOExpense(int id, Double amount, String description, int customer) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.customer = customer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getCustomer() {
        return customer;
    }

    public void setCustomer(int customer) {
        this.customer = customer;
    }
}
