package site.easy.to.build.crm.controller.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import site.easy.to.build.crm.service.database.TableImportService;
import site.easy.to.build.crm.service.database.UserImportService;

@Controller
@RequestMapping("/import")
public class ImportCSVController {

    @Autowired
    private UserImportService userImportService;

    @Autowired
    private TableImportService tableImportService;

    @PostMapping("/import-csv")
    public String importUsers(@RequestParam("file") MultipartFile file) {
        userImportService.importUsersFromCsv(file);
        System.out.println("Users imported successfully!");
        return "redirect:/import/from-import-csv"; // Redirige vers la page d'importation
    }

    @GetMapping("/from-import-csv")
    public String showImportPage() {
        return "database/importCSV";
    }

    @PostMapping("/csv-import")
    public String importCsv(@RequestParam("file") MultipartFile file) {
        tableImportService.importFromCsv(file);
        return "redirect:/import/from-import-csv";
    }
    
}
