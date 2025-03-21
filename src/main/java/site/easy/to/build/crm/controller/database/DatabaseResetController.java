package site.easy.to.build.crm.controller.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import site.easy.to.build.crm.service.database.DatabaseResetService;

@RestController
@RequestMapping("/admin")
public class DatabaseResetController {

    @Autowired
    private DatabaseResetService databaseResetService;

    @PostMapping("/reset-database")
    public String resetDatabase() {
        databaseResetService.resetDatabase();
        return "Database reset successfully!";
    }
}
