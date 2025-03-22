package site.easy.to.build.crm.service.budget;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.repository.BudgetRepository;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    // Créer un budget
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public List<Budget> findByCustomer(Customer customer) {
        return budgetRepository.findByCustomer(customer);
    }

    public Double getTotalBudgetByCustomer(Customer customer) {
        return budgetRepository.getTotalBudgetByCustomer(customer);
    }

}
