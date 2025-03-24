package site.easy.to.build.crm.entity.DTO;

import java.util.List;

public class DTOCustomer {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String country;
    private String address;

    private List<DTOBudget> budgetList;

    private List<DTOExpense> expenseList;

    public DTOCustomer(int id, String name, String email, String phone, String country, String address,
            List<DTOBudget> budgetList, List<DTOExpense> expenseList) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.address = address;
        this.budgetList = budgetList;
        this.expenseList = expenseList;
    }


    public DTOCustomer() {
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }


    public List<DTOBudget> getBudgetList() {
        return budgetList;
    }


    public void setBudgetList(List<DTOBudget> budgetList) {
        this.budgetList = budgetList;
    }


    public List<DTOExpense> getExpenseList() {
        return expenseList;
    }


    public void setExpenseList(List<DTOExpense> expenseList) {
        this.expenseList = expenseList;
    }
}
