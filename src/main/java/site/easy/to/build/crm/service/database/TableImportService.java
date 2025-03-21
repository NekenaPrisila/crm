package site.easy.to.build.crm.service.database;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TableImportService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void importFromCsv(MultipartFile file) {
        String tableName = extractTableNameFromFileName(file.getOriginalFilename());

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] headers = reader.readNext(); // Lire la ligne d'en-tête
            if (headers == null) {
                throw new RuntimeException("Le fichier CSV est vide ou ne contient pas d'en-têtes.");
            }

            // Étape 1 : Trouver l'entité correspondant au nom de la table
            Class<?> entityClass = findEntityClassByTableName(tableName);
            if (entityClass == null) {
                throw new RuntimeException("Aucune entité trouvée pour la table : " + tableName);
            }

            // Étape 2 : Lire les données du CSV et les mapper à l'entité
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                Object entity = createEntityFromCsvRow(entityClass, headers, nextLine);
                entityManager.persist(entity); // Sauvegarder l'entité
            }

            System.out.println("Import dans la table " + tableName + " réussi.");
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Échec de l'importation des données CSV : " + e.getMessage(), e);
        }
    }

    private String extractTableNameFromFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Le nom du fichier ne peut pas être vide.");
        }

        // Supprimer l'extension .csv
        String tableName = fileName.replace(".csv", "");

        // Valider le nom de la table (exemple : pas de caractères spéciaux)
        if (!tableName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Le nom du fichier contient des caractères invalides pour un nom de table.");
        }

        return tableName;
    }

    private Class<?> findEntityClassByTableName(String tableName) {
        // Parcourir les entités JPA pour trouver celle qui correspond au nom de la table
        for (Class<?> clazz : getEntityClasses()) {
            Table tableAnnotation = clazz.getAnnotation(Table.class);
            if (tableAnnotation != null && tableAnnotation.name().equalsIgnoreCase(tableName)) {
                return clazz;
            }
        }
        return null;
    }

    private Set<Class<?>> getEntityClasses() {
        // Retourner un ensemble d'entités JPA (à adapter selon votre projet)
        // Exemple : retourner manuellement les classes d'entités
        Set<Class<?>> entityClasses = new HashSet<>();
        entityClasses.add(site.easy.to.build.crm.entity.User.class);
        entityClasses.add(site.easy.to.build.crm.entity.Role.class);
        // Ajouter d'autres entités ici
        return entityClasses;
    }

    private Object createEntityFromCsvRow(Class<?> entityClass, String[] headers, String[] row) {
        try {
            Object entity = entityClass.getDeclaredConstructor().newInstance();

            for (int i = 0; i < headers.length; i++) {
                String header = headers[i];
                String value = row[i];

                // Trouver le champ correspondant dans l'entité
                Field field = entityClass.getDeclaredField(header);
                field.setAccessible(true);

                // Convertir la valeur en type approprié
                Object convertedValue = convertValueToFieldType(field, value);

                // Définir la valeur du champ
                field.set(entity, convertedValue);
            }

            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de l'entité : " + e.getMessage(), e);
        }
    }

    private Object convertValueToFieldType(Field field, String value) {
        Class<?> fieldType = field.getType();

        if (fieldType == String.class) {
            return value;
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return Integer.parseInt(value);
        } else if (fieldType == Long.class || fieldType == long.class) {
            return Long.parseLong(value);
        } else if (fieldType == Double.class || fieldType == double.class) {
            return Double.parseDouble(value);
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (fieldType == LocalDate.class) {
            return LocalDate.parse(value);
        } else if (fieldType == LocalDateTime.class) {
            return LocalDateTime.parse(value);
        } else {
            throw new RuntimeException("Type de champ non supporté : " + fieldType.getName());
        }
    }
}
