package site.easy.to.build.crm.service.user;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.entity.User;
import site.easy.to.build.crm.repository.UserRepository;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UserImportService {

    @Autowired
    private UserRepository userRepository;

    public void importUsersFromCsv(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Skip the header row
            reader.readNext();

            while ((nextLine = reader.readNext()) != null) {
                User user = new User();
                user.setUsername(nextLine[0]);
                user.setEmail(nextLine[1]);
                user.setPassword(nextLine[2]);
                user.setHireDate(LocalDate.parse(nextLine[3], dateFormatter));
                user.setCreatedAt(LocalDateTime.parse(nextLine[4], dateTimeFormatter));
                user.setUpdatedAt(LocalDateTime.parse(nextLine[5], dateTimeFormatter));
                user.setStatus(nextLine[6]);
                user.setToken(nextLine[7]);
                user.setPasswordSet(Boolean.parseBoolean(nextLine[8]));

                userRepository.save(user);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}
