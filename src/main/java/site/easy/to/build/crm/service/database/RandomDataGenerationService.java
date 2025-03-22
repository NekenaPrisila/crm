package site.easy.to.build.crm.service.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;

import site.easy.to.build.crm.entity.*;
import site.easy.to.build.crm.repository.*;

@Service
public class RandomDataGenerationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerLoginInfoRepository customerLoginInfoRepository;

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;


    private final Faker faker = new Faker();

    public void generateRandomDataForAllTables(int numberOfRecords) {

        // Générer des utilisateurs
        for (int i = 0; i < numberOfRecords; i++) {
            User user = new User();
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(faker.internet().password());
            user.setUsername(faker.name().username());
            userRepository.save(user);


            // Générer des profils utilisateur
            UserProfile userProfile = new UserProfile();
            userProfile.setFirstName(faker.name().firstName());
            userProfile.setLastName(faker.name().lastName());
            userProfile.setUser(user);
            userProfileRepository.save(userProfile);

            // Générer des clients
            CustomerLoginInfo customerLoginInfo = new CustomerLoginInfo();
            customerLoginInfo.setEmail(faker.name().username());
            customerLoginInfo.setPassword(faker.internet().password());
            customerLoginInfoRepository.save(customerLoginInfo);

            Customer customer = new Customer();
            customer.setName(faker.company().name());
            customer.setEmail(faker.internet().emailAddress());
            customer.setCustomerLoginInfo(customerLoginInfo);
            customerRepository.save(customer);

            // Générer des modèles d'email
            EmailTemplate emailTemplate = new EmailTemplate();
            emailTemplate.setName(faker.lorem().word());
            emailTemplate.setContent(faker.lorem().paragraph());
            emailTemplate.setUser(user);
            emailTemplateRepository.save(emailTemplate);

        }
    }
}
