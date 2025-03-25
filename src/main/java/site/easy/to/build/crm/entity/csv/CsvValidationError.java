package site.easy.to.build.crm.entity.csv;

public class CsvValidationError {
    private String fileName;
    private int lineNumber;
    private String fieldName;
    private String errorMessage;

    public CsvValidationError(String fileName, int lineNumber, String fieldName, String errorMessage) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }

    // Getters
    public String getFileName() { return fileName; }
    public int getLineNumber() { return lineNumber; }
    public String getFieldName() { return fieldName; }
    public String getErrorMessage() { return errorMessage; }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}