package site.easy.to.build.crm.service.database;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class TableImportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void importFromCsv(MultipartFile file) {
        String tableName = extractTableNameFromFileName(file.getOriginalFilename());
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] headers = reader.readNext(); // Lire la ligne d'en-tête
            if (headers == null) {
                throw new RuntimeException("Le fichier CSV est vide ou ne contient pas d'en-têtes.");
            }

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                Map<String, Object> rowData = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    rowData.put(headers[i], nextLine[i]);
                }
                insertRow(tableName, rowData);
            }

            System.out.println("import dans la table " + tableName + "reussi");
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

    private void insertRow(String tableName, Map<String, Object> rowData) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, Object> entry : rowData.entrySet()) {
            if (columns.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }
            columns.append(entry.getKey());
            values.append("?");
            params.add(entry.getValue());
        }

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, values);
        jdbcTemplate.update(sql, params.toArray());
    }
}
