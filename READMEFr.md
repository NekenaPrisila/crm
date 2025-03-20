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

