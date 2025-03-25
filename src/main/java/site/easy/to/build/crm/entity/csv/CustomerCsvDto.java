package site.easy.to.build.crm.entity.csv;

import jakarta.validation.constraints.*;

public class CustomerCsvDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String customer_email;

    @NotBlank(message = "Name is required")
    private String customer_name;

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    // Getters and Setters
}