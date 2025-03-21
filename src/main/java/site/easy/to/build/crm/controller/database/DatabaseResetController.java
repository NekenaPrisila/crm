package site.easy.to.build.crm.controller.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import site.easy.to.build.crm.service.database.DatabaseResetService;

@Controller
@RequestMapping("/database")
public class DatabaseResetController {

    @Autowired
    private DatabaseResetService databaseResetService;

    @PostMapping("/reset-database")
    public String resetDatabase() {
        databaseResetService.resetDatabase();
        System.out.println("Database reset successfully!");
        return "redirect:/import/from-import-csv";
    }
}
