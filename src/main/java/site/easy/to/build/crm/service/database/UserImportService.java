package site.easy.to.build.crm.service.database;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import site.easy.to.build.crm.entity.Role;
import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.repository.RoleRepository;
import site.easy.to.build.crm.repository.UserRepository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserImportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public void importUsersFromCsv(MultipartFile file) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] nextLine;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Skip the header row
            reader.readNext();

            while ((nextLine = reader.readNext()) != null) {
                User user = new User();
                user.setEmail(nextLine[0]);
                user.setPassword(nextLine[1]);
                user.setHireDate(LocalDate.parse(nextLine[2], dateFormatter));
                user.setCreatedAt(LocalDateTime.parse(nextLine[3], dateTimeFormatter));
                user.setUpdatedAt(LocalDateTime.parse(nextLine[4], dateTimeFormatter));
                user.setUsername(nextLine[5]);
                user.setStatus(nextLine[6]);
                user.setToken(nextLine[7]);
                user.setPasswordSet(Boolean.parseBoolean(nextLine[8]));

                // Récupérer les rôles
                String[] roleIds = nextLine[9].split(",");
                List<Role> roles = new ArrayList<>();
                for (String roleId : roleIds) {
                    Role role = roleRepository.findById(Integer.parseInt(roleId))
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
                    roles.add(role);
                }
                user.setRoles(roles);

                userRepository.save(user);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}
