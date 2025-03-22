package site.easy.to.build.crm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.service.budget.BudgetService;
import site.easy.to.build.crm.service.customer.CustomerService;

@Controller
@RequestMapping("/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private CustomerService customerService;

    // Traiter la soumission du formulaire
    @PostMapping
    public String createBudget(@ModelAttribute("budget") Budget budget, BindingResult result, Model model) {
        // Valider les données du formulaire
        if (result.hasErrors()) {
            // Si des erreurs de validation sont détectées, réafficher le formulaire avec les erreurs
            List<Customer> customers = customerService.findAll();
            model.addAttribute("customers", customers);
            return "budget/create-budget";
        }

        // Enregistrer le budget dans la base de données
        budgetService.createBudget(budget);

        // Rediriger vers une page de succès ou la liste des budgets
        return "redirect:/budgets/create-budget";
    }

    @GetMapping("/create-budget")
    public String showCreateBudgetForm(Model model) {
        // Add a new Budget object to the model
        model.addAttribute("budget", new Budget());

        // Fetch the list of customers and add it to the model
        List<Customer> customers = customerService.findAll();
        model.addAttribute("customers", customers);

        return "budget/create-budget"; // Thymeleaf template name
    }

}
