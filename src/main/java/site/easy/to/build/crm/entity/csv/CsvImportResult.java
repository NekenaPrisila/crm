package site.easy.to.build.crm.entity.csv;

import java.util.List;

public class CsvImportResult<T> {
    private List<T> validEntities;
    private List<CsvValidationError> errors;

    public CsvImportResult(List<T> validEntities, List<CsvValidationError> errors) {
        this.validEntities = validEntities;
        this.errors = errors;
    }

    // Getters
    public List<T> getValidEntities() { return validEntities; }
    public List<CsvValidationError> getErrors() { return errors; }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public void setValidEntities(List<T> validEntities) {
        this.validEntities = validEntities;
    }

    public void setErrors(List<CsvValidationError> errors) {
        this.errors = errors;
    }
}