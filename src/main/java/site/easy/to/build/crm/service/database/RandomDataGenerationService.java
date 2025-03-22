package site.easy.to.build.crm.service.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import site.easy.to.build.crm.repository.*;

@Service
public class RandomDataGenerationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OAuthUserRepository oAuthUserRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerLoginInfoRepository customerLoginInfoRepository;

    @Autowired
    private TriggerLeadRepository triggerLeadRepository;

    @Autowired
    private TriggerTicketRepository triggerTicketRepository;

    @Autowired
    private TriggerContractRepository triggerContractRepository;

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private GoogleDriveFileRepository googleDriveFileRepository;

    @Autowired
    private LeadActionRepository leadActionRepository;

    @Autowired
    private LeadSettingsRepository leadSettingsRepository;

    @Autowired
    private TicketSettingsRepository ticketSettingsRepository;

    @Autowired
    private ContractSettingsRepository contractSettingsRepository;

    private final Faker faker = new Faker();

    public void generateRandomDataForAllTables(int numberOfRecords) {
        // Récupérer les rôles existants depuis la base de données
        Role roleManager = roleRepository.findByName("ROLE_MANAGER")
                .orElseThrow(() -> new RuntimeException("ROLE_MANAGER not found"));
        Role roleEmployee = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseThrow(() -> new RuntimeException("ROLE_EMPLOYEE not found"));

        // Générer des utilisateurs
        for (int i = 0; i < numberOfRecords; i++) {
            User user = new User();
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(faker.internet().password());
            user.setUsername(faker.name().username());
            userRepository.save(user);

            // Assigner des rôles aux utilisateurs
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(faker.bool().bool() ? roleManager : roleEmployee);
            userRoleRepository.save(userRole);

            // Générer des profils utilisateur
            UserProfile userProfile = new UserProfile();
            userProfile.setFirstName(faker.name().firstName());
            userProfile.setLastName(faker.name().lastName());
            userProfile.setUser(user);
            userProfileRepository.save(userProfile);

            // Générer des clients
            CustomerLoginInfo customerLoginInfo = new CustomerLoginInfo();
            customerLoginInfo.setUsername(faker.name().username());
            customerLoginInfo.setPassword(faker.internet().password());
            customerLoginInfoRepository.save(customerLoginInfo);

            Customer customer = new Customer();
            customer.setName(faker.company().name());
            customer.setEmail(faker.internet().emailAddress());
            customer.setProfileId(customerLoginInfo.getId());
            customerRepository.save(customer);

            // Générer des leads
            TriggerLead lead = new TriggerLead();
            lead.setCustomer(customer);
            lead.setUser(user);
            lead.setName(faker.company().name());
            lead.setPhone(faker.phoneNumber().cellPhone());
            triggerLeadRepository.save(lead);

            // Générer des tickets
            TriggerTicket ticket = new TriggerTicket();
            ticket.setSubject(faker.lorem().sentence());
            ticket.setDescription(faker.lorem().paragraph());
            ticket.setCustomer(customer);
            ticket.setManager(user);
            ticket.setEmployee(user);
            triggerTicketRepository.save(ticket);

            // Générer des contrats
            TriggerContract contract = new TriggerContract();
            contract.setSubject(faker.lorem().sentence());
            contract.setCustomer(customer);
            contract.setUser(user);
            contract.setLead(lead);
            triggerContractRepository.save(contract);

            // Générer des modèles d'email
            EmailTemplate emailTemplate = new EmailTemplate();
            emailTemplate.setName(faker.lorem().word());
            emailTemplate.setContent(faker.lorem().paragraph());
            emailTemplate.setUser(user);
            emailTemplateRepository.save(emailTemplate);

            // Générer des fichiers
            File file = new File();
            file.setFileName(faker.file().fileName());
            file.setLead(lead);
            fileRepository.save(file);

            // Générer des fichiers Google Drive
            GoogleDriveFile googleDriveFile = new GoogleDriveFile();
            googleDriveFile.setDriveFileId(faker.internet().uuid());
            googleDriveFile.setLead(lead);
            googleDriveFileRepository.save(googleDriveFile);

            // Générer des actions de lead
            LeadAction leadAction = new LeadAction();
            leadAction.setLead(lead);
            leadAction.setAction(faker.lorem().word());
            leadAction.setDateTime(faker.date().past(10, TimeUnit.DAYS));
            leadActionRepository.save(leadAction);

            // Générer des paramètres de lead
            LeadSettings leadSettings = new LeadSettings();
            leadSettings.setUser(user);
            leadSettings.setStatus(faker.bool().bool());
            leadSettingsRepository.save(leadSettings);

            // Générer des paramètres de ticket
            TicketSettings ticketSettings = new TicketSettings();
            ticketSettings.setUser(user);
            ticketSettings.setStatus(faker.bool().bool());
            ticketSettingsRepository.save(ticketSettings);

            // Générer des paramètres de contrat
            ContractSettings contractSettings = new ContractSettings();
            contractSettings.setUser(user);
            contractSettings.setStatus(faker.bool().bool());
            contractSettingsRepository.save(contractSettings);
        }
    }
}
