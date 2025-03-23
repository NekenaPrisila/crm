package site.easy.to.build.crm.service.expense;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.repository.ExpenseRepository;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    // Créer un Expense
    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> findByCustomer(Customer customer) {
        return expenseRepository.findByCustomer(customer);
    }

    public Double getTotalExpenseByCustomer(Customer customer) {
        return expenseRepository.getTotalExpenseByCustomer(customer);
    }

}
