package site.easy.to.build.crm.entity.csv;

import jakarta.validation.constraints.*;

public class BudgetCsvDto {
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customer_email;

    @NotBlank(message = "Budget amount is required")
    @Pattern(regexp = "^[0-9]+(,[0-9]{3})*(\\.[0-9]{1,2})?$", 
             message = "Invalid amount format (ex: 150000 or 68500.87)")
    private String Budget;

    public String getCustomerEmail() {
        return customer_email;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customer_email = customerEmail;
    }

    public String getBudget() {
        return Budget;
    }

    public void setBudget(String budget) {
        this.Budget = budget;
    }

}