# Application Web CRM

Cette application CRM (Gestion de la Relation Client) est construite en utilisant Spring Boot MVC, Thymeleaf, Hibernate, MySQL et Java 17. L'application offre une solution complète pour gérer les interactions avec les clients, les tâches, les rendez-vous et la communication. Elle intègre également divers services Google, notamment Google Drive, Gmail et Google Calendar, pour améliorer la productivité et la collaboration.

## **Prérequis**

Avant d'installer l'application CRM, assurez-vous que les éléments suivants sont en place :

- Java 17 est installé sur votre machine.
- La base de données MySQL est configurée et en cours d'exécution.
- Obtenez les informations de connexion MySQL valides (URL, nom d'utilisateur, mot de passe).
- Obtenez les identifiants de l'API Google pour l'intégration avec les services Google (Drive, Gmail, Calendar).

## Installation

Pour installer et exécuter l'application CRM, suivez ces étapes :

1. Clonez le dépôt depuis GitHub.
2. Configurez les informations de connexion à la base de données MySQL dans le fichier `application.properties` :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/crm?createDatabaseIfNotExist=true
spring.datasource.username=VotreNomUtilisateur
spring.datasource.password=VotreMotDePasse
spring.jpa.hibernate.ddl-auto=none
spring.sql.init.mode=always
```
Remplacez `YourUserName` et `YourPassword` par vos identifiants de base de données MySQL.

1. **Configurez les identifiants nécessaires de l'API Google pour l'intégration avec Google :**
    - Allez sur la [Console Google Cloud](https://console.cloud.google.com/).
    - Créez un nouveau projet ou sélectionnez un projet existant.
    - Activez les API nécessaires pour votre projet (par exemple, Google Drive, Gmail, Calendar).
    - Dans le tableau de bord du projet, accédez à la section **Identifiants**.
    - Cliquez sur **Créer des identifiants** et sélectionnez **ID client OAuth**.
    - Configurez l'écran de consentement OAuth avec les informations requises.
    - Choisissez le type d'application **Application Web**.
    - Ajoutez les URI de redirection autorisés dans la section **URI de redirection autorisés**. Par exemple :
        - `http://localhost:8080/login/oauth2/code/google`
        - `http://localhost:8080/employee/settings/handle-granted-access`
        Remplacez `localhost:8080` par l'URL de base de votre application CRM.
    - Terminez la configuration et notez l'**ID client** et le **Secret client**.

2. **Modifiez les scopes de l'API Google pour accéder aux services Google :**
    
    Lors de la configuration des identifiants de l'API Google, vous devez ajouter les scopes requis pour définir le niveau d'accès de l'application à votre compte Google. Les scopes requis dépendent des fonctionnalités spécifiques que vous souhaitez utiliser. Voici les scopes pour les services Google courants :
    
    - Google Drive : `https://www.googleapis.com/auth/drive`
    - Gmail : `https://www.googleapis.com/auth/gmail.readonly`
    - Google Calendar : `https://www.googleapis.com/auth/calendar`
        
    Pendant la configuration de vos identifiants Google, trouvez la section pour ajouter les scopes de l'API et incluez les scopes pertinents pour les fonctionnalités que vous comptez utiliser.
        
    [![scopes non sensibles](https://github.com/wp-ahmed/crm/assets/54330098/f1bc7026-591a-4d40-affa-e038e29591b2)](https://github.com/wp-ahmed/crm/assets/54330098/f1bc7026-591a-4d40-affa-e038e29591b2)

    ![scopes sensibles](https://github.com/wp-ahmed/crm/assets/54330098/14d82922-0904-45d0-9874-da18c90fb352)

    ![scopes restreints](https://github.com/wp-ahmed/crm/assets/54330098/b76a5cf8-c342-42e9-9848-6d0844f83575)

3. **Configurez l'URI de redirection pour le flux d'authentification Google :**

```properties
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}
```

1. Personnalisez les URL d'autorisation et d'authentification pour l'application si nécessaire :
```properties
spring.security.oauth2.client.provider.google.authorization-uri=https://accounts.google.com/o/oauth2/auth
spring.security.oauth2.client.provider.google.token-uri=https://accounts.google.com/o/oauth2/token
```

1. Build the application using Maven:

```bash
mvn clean install

```

1. Run the application:

```bash
mvn spring-boot:run

```

1. Access the CRM application in your web browser at `http://localhost:8080`.

## Fonctionnalités

### Authentification et Autorisation des Utilisateurs

- Les utilisateurs peuvent se connecter en utilisant leurs identifiants classiques ou choisir de se connecter via leurs comptes Google.
- La connexion Google permet aux utilisateurs d'accorder l'accès à Google Drive, Gmail et Google Calendar.

### Intégration avec Google Drive

- Les utilisateurs peuvent créer, supprimer et partager des fichiers et dossiers avec leurs collègues directement depuis l'application CRM.
- L'intégration avec Google Drive permet une collaboration fluide et une gestion efficace des documents.

### Intégration avec Google Calendar

- Intégrée avec la bibliothèque FullCalendar JS, cette fonctionnalité permet aux utilisateurs de gérer facilement leur calendrier, de créer, modifier et supprimer des réunions.
- Des notifications par e-mail sont envoyées automatiquement aux participants lorsqu'une réunion est programmée ou modifiée.

### Intégration avec Google Gmail

- Les utilisateurs peuvent envoyer des e-mails, enregistrer des brouillons et gérer leur boîte de réception, les éléments envoyés, les brouillons et la corbeille directement dans l'application CRM.
- L'intégration avec Gmail facilite la communication et permet une gestion efficace des e-mails.

### Rôles et Permissions des Utilisateurs

- L'application prend en charge différents rôles, notamment Manager, Employé, Commercial et Client.
- Chaque rôle dispose d'un accès et de permissions spécifiques adaptés à ses responsabilités.

### Rôle Manager

- Les managers ont accès à toutes les fonctionnalités de l'application CRM.
- Ils peuvent créer de nouveaux utilisateurs, leur attribuer des rôles spécifiques, définir de nouveaux rôles et gérer l'accès aux différentes pages pour les employés.
- Les managers peuvent attribuer des tickets et des prospects aux employés pour une répartition efficace des tâches.

### Rôle Employé

- Les employés ont accès à leurs tickets, prospects, contrats et historique des tâches assignées.
- Ils peuvent gérer leurs clients et créer de nouveaux tickets.
- Les employés reçoivent des notifications par e-mail pour les nouvelles tâches qui leur sont assignées (configurable dans les paramètres utilisateurs).

### Rôle Client

- Les clients ont accès à leurs tickets, prospects et contrats.
- Ils reçoivent des notifications par e-mail en cas de modification de leurs tickets, prospects ou contrats.
- Les clients peuvent gérer leurs préférences de notification dans leurs paramètres.

### Gestion des Prospects

- Les utilisateurs peuvent créer, mettre à jour, supprimer et consulter des prospects.
- L'intégration avec Google Drive permet l'enregistrement automatique des pièces jointes des prospects.
- L'intégration avec Google Calendar facilite la planification des réunions avec les clients.

### Gestion des Tickets

- Les utilisateurs peuvent créer, mettre à jour, supprimer et consulter des tickets.
- L'intégration avec Google Drive permet l'enregistrement automatique des pièces jointes des tickets.
- L'intégration avec Google Calendar permet la planification de réunions liées aux tickets.

### Gestion des Contrats

- Les utilisateurs peuvent créer, mettre à jour, supprimer et consulter des contrats.
- Les contrats peuvent inclure des détails tels que le montant, les dates de début et de fin, la description et les pièces jointes.
- L'intégration avec Google Drive permet le téléchargement et le partage des contrats avec les clients.

### Modèles d'E-mails et Campagnes

- Les utilisateurs peuvent créer des modèles d'e-mails personnalisés en utilisant la fonctionnalité glisser-déposer de la bibliothèque Unlayer.
- Des campagnes d'e-mails peuvent être créées à partir des modèles prédéfinis.

### Paramètres Utilisateur

- Les utilisateurs peuvent configurer les paramètres des e-mails et l'accès aux services Google depuis leur page de paramètres.
- Les paramètres e-mail permettent aux employés d'activer ou de désactiver l'envoi automatique d'e-mails aux clients en utilisant des modèles d'e-mails prédéfinis lorsque des tickets, prospects ou autres objets sont mis à jour.
- Les paramètres Google permettent aux utilisateurs de gérer l'accès aux services Google, en activant ou désactivant l'intégration avec Google Drive, Gmail et Google Calendar.

### Screenshots

![login](https://github.com/wp-ahmed/crm/assets/54330098/2cb1fe3f-6e9f-4696-aa03-672893c17af3)
![Ticket details](https://github.com/wp-ahmed/crm/assets/54330098/a7aa060b-7724-4f7e-814d-6f0150d447aa)
![create user](https://github.com/wp-ahmed/crm/assets/54330098/e7b161bb-7555-4a83-9511-3a138ebb61f3)
![show users](https://github.com/wp-ahmed/crm/assets/54330098/3535c32b-560d-4896-a33f-25ad98853c01)
![profile details](https://github.com/wp-ahmed/crm/assets/54330098/bc0e33c7-20b7-4384-8532-5f569740869a)
![Google services](https://github.com/wp-ahmed/crm/assets/54330098/23dd8852-b3e7-40e8-b962-a0b72a14f08e)
![Create google drive folder](https://github.com/wp-ahmed/crm/assets/54330098/b169882a-c48a-49da-859f-fbcbb41430df)
![Create google drive file](https://github.com/wp-ahmed/crm/assets/54330098/94e6e672-3ecf-4ded-91c9-dcfa48a37cd4)
![Listing Drive folder and files](https://github.com/wp-ahmed/crm/assets/54330098/b9832bcf-7b9a-4e82-a137-7ac6ea851b47)
![Compose email](https://github.com/wp-ahmed/crm/assets/54330098/ef4d6d74-1c72-46ce-847a-a8c6df740561)
![Calendar events](https://github.com/wp-ahmed/crm/assets/54330098/7d6b6dde-ba45-4e62-ba9f-f887b287f49d)
![Adding new calendar event](https://github.com/wp-ahmed/crm/assets/54330098/cdaacedb-1bfb-4bf9-8348-afc6424e56c5)
![Adding calendar event](https://github.com/wp-ahmed/crm/assets/54330098/8d88b0cd-717a-4305-a3b9-80bc8747b146)
![inbox emails](https://github.com/wp-ahmed/crm/assets/54330098/c31563e8-956f-4cfb-84fd-b6e8b2003ac9)
![Email notification settings](https://github.com/wp-ahmed/crm/assets/54330098/d2793a76-3c35-4f1d-a4a0-3944b78c409a)
![customer details](https://github.com/wp-ahmed/crm/assets/54330098/964b4af6-1be4-4396-970f-3b4fa96b3843)
![create new ticket](https://github.com/wp-ahmed/crm/assets/54330098/72fa8161-abe2-4cff-b4d1-805d8092f1c4)
![show tickets](https://github.com/wp-ahmed/crm/assets/54330098/694eb71c-a20b-45aa-b2e5-04f08f459ad0)
![create email templates](https://github.com/wp-ahmed/crm/assets/54330098/90e9093e-81aa-41c3-a9a6-0956df3b3716)
![contract details](https://github.com/wp-ahmed/crm/assets/54330098/b5819c49-e8fa-4a81-9e42-df81fdba2cec)

## Contribution

Les contributions à l'application web CRM sont les bienvenues ! Si vous repérez des bugs ou souhaitez proposer de nouvelles fonctionnalités, n'hésitez pas à ouvrir une issue ou à soumettre une pull request.

## Licence

Ce projet est sous licence MIT.
