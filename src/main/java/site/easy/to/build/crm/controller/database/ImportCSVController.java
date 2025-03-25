package site.easy.to.build.crm.controller.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.entity.csv.CsvImportResult;
import site.easy.to.build.crm.service.database.CsvImportService;
import site.easy.to.build.crm.service.user.UserService;
import site.easy.to.build.crm.util.AuthenticationUtils;

@Controller
@RequestMapping("/import")
public class ImportCSVController {

    @Autowired
    private CsvImportService csvImportService;

    @Autowired
    private AuthenticationUtils authenticationUtils;

    @Autowired
    private UserService userService;

    @GetMapping("/from-import-csv")
    public String showImportPage() {
        return "database/importCSV";
    }

    @PostMapping("/csv-import")
    public String handleCsvImport(
            @RequestParam("fileCustomer") MultipartFile customerFile,
            @RequestParam("fileBudget") MultipartFile budgetFile,
            @RequestParam("fileTicketLead") MultipartFile ticketLeadFile,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        int userId = authenticationUtils.getLoggedInUserId(authentication);
        User user = userService.findById(userId);

        try {
            CsvImportResult result = csvImportService.processFiles(customerFile, budgetFile, ticketLeadFile, user);

            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("errors", result.getErrors());
                redirectAttributes.addFlashAttribute("importedCount", result.getValidEntities().size());
            } else {
                System.out.println("true");
                redirectAttributes.addFlashAttribute("success", 
                    "Successfully imported " + result.getValidEntities().size() + " records");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Failed to process files: " + e.getMessage());
        }

        return "redirect:/import/from-import-csv";
    }
    
}
