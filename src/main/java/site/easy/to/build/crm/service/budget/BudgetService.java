package site.easy.to.build.crm.service.budget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.repository.BudgetRepository;
import site.easy.to.build.crm.repository.ExpenseRepository;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    // Créer un budget
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    // Ajouter une dépense à un budget
    public Expense addExpenseToBudget(Long budgetId, Expense expense) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget non trouvé"));
        expense.setBudget(budget);
        budget.setCurrentAmount(budget.getCurrentAmount() + expense.getAmount());
        checkBudgetAlerts(budget); // Vérifier les alertes
        return expenseRepository.save(expense);
    }

    // Vérifier les alertes de budget
    private void checkBudgetAlerts(Budget budget) {
        double usedPercentage = (budget.getCurrentAmount() / budget.getTotalAmount()) * 100;
        if (usedPercentage >= budget.getAlertThreshold()) {
            System.out.println("Alerte : Le budget " + budget.getName() + " a atteint " + usedPercentage + "%");
        }
        if (budget.getCurrentAmount() > budget.getTotalAmount()) {
            System.out.println("Confirmation : Le budget " + budget.getName() + " a été dépassé");
        }
    }
}
