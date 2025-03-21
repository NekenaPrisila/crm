package site.easy.to.build.crm.controller.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import site.easy.to.build.crm.service.user.UserImportService;

@Controller
@RequestMapping("/import")
public class UserImportController {

    @Autowired
    private UserImportService userImportService;

    @PostMapping("/import-users")
    public String importUsers(@RequestParam String filePath) {
        userImportService.importUsersFromCsv(filePath);
        return "Users imported successfully!";
    }

    @GetMapping("/form-import-users")
    public String showImportPage() {
        return "database/importCSV"; // Retourne le nom de la vue (import-users.html)
    }
}
