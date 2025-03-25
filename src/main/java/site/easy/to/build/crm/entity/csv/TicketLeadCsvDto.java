package site.easy.to.build.crm.entity.csv;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

public class TicketLeadCsvDto {
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customer_email;

    @NotBlank(message = "Subject/Name is required")
    private String subject_or_name;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(ticket|lead)$", message = "Type must be 'ticket' or 'lead'")
    private String type;

    @NotBlank(message = "Status is required")
    private String status;

    @Pattern(regexp = "^[0-9]+(,[0-9]{3})*(\\.[0-9]{1,2})?$", 
             message = "Invalid expense format (ex: 150000 or 68500.87)")
    private String expense;

    public String getCustomerEmail() {
        return customer_email;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customer_email = customerEmail;
    }

    public String getSubjectOrName() {
        return subject_or_name;
    }

    public void setSubjectOrName(String subjectOrName) {
        this.subject_or_name = subjectOrName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }

    @AssertTrue(message = "Expense must be positive")
    public boolean isExpensePositive() {
        if (expense == null || expense.isEmpty()) {
            return true;
        }
        try {
            String normalized = expense.replace(",", "");
            return new BigDecimal(normalized).compareTo(BigDecimal.ZERO) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}