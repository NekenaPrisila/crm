package site.easy.to.build.crm.entity.csv;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public class BudgetCsvDto {
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customer_email;

    @NotBlank(message = "Budget amount is required")
    @Pattern(regexp = "^\"?[0-9]+(?:[,.][0-9]{3})*(?:[.,][0-9]{1,2})?\"?$", 
             message = "Invalid expense format (ex: 150000, 350000.23, \"350000,23\" or 1,500,000)")
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
        // Nettoie la valeur en supprimant les guillemets et espaces
        this.Budget = budget != null ? budget.trim().replace("\"", "") : null;
    }

    @AssertTrue(message = "Budget amount must be positive")
    public boolean isBudgetValid() {
        try {
            BigDecimal value = getBudgetValue();
            return value.compareTo(BigDecimal.ZERO) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public BigDecimal getBudgetValue() {
        if (Budget == null || Budget.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            String normalized = Budget.replace("\"", "")
                                    .replace(".", "")
                                    .replace(",", ".");
            return new BigDecimal(normalized);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}