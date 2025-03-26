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

    @Pattern(regexp = "^\"?[0-9]+(?:[,.][0-9]{3})*(?:[.,][0-9]{1,2})?\"?$", 
             message = "Invalid expense format (ex: 150000, 350000.23, \"350000,23\" or 1,500,000)")
    private String expense;

    // Getters
    public String getCustomerEmail() {
        return customer_email;
    }

    public String getSubjectOrName() {
        return subject_or_name;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getExpense() {
        return expense;
    }

    // Setters avec normalisation automatique
    public void setCustomerEmail(String customerEmail) {
        this.customer_email = customerEmail;
    }

    public void setSubjectOrName(String subjectOrName) {
        this.subject_or_name = subjectOrName;
    }

    public void setType(String type) {
        this.type = type;
        // Quand le type change, on normalise le statut
        normalizeStatus();
    }

    public void setStatus(String status) {
        this.status = status;
        normalizeStatus();
    }

    public void setExpense(String expense) {
        if (expense == null || expense.trim().isEmpty()) {
            this.expense = "0";
        } else {
            this.expense = expense != null ? expense.trim().replace("\"", "") : null;
        }
    }

    // Méthodes de validation
    public BigDecimal getExpenseValue() {
        if (expense == null || expense.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            String normalized = expense.replace("\"", "")
                                    .replace(".", "")
                                    .replace(",", ".");
            return new BigDecimal(normalized);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    @AssertTrue(message = "Expense must be positive or zero")
    public boolean isExpenseValid() {
        try {
            BigDecimal value = getExpenseValue();
            return value.compareTo(BigDecimal.ZERO) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Normalisation du statut
    private void normalizeStatus() {
        if (type == null || status == null) {
            return;
        }

        if ("lead".equalsIgnoreCase(type)) {
            if (!status.matches("^(meeting-to-schedule|scheduled|archived|success|assign-to-sales)$")) {
                this.status = "meeting-to-schedule";
            }
        } else if ("ticket".equalsIgnoreCase(type)) {
            if (!status.matches("^(open|assigned|on-hold|in-progress|resolved|closed|reopened|pending-customer-response|escalated|archived)$")) {
                this.status = "open";
            }
        }
    }
}