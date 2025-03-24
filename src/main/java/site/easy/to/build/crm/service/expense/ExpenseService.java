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

    // Méthode pour mettre à jour une dépense
    public Expense updateExpense(int idExpense, Expense updatedExpense) {
        Expense existingExpense = expenseRepository.findById((long) idExpense).orElse(null);
        if (existingExpense != null) {
            // Mettre à jour les champs de la dépense existante
            existingExpense.setAmount(updatedExpense.getAmount());
            existingExpense.setDescription(updatedExpense.getDescription());
            existingExpense.setCustomer(updatedExpense.getCustomer());
            return expenseRepository.save(existingExpense);
        }
        return null; // Retourne null si la dépense n'existe pas
    }

}
