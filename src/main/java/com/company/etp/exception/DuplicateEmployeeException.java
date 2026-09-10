package com.company.etp.exception;
import java.util.HashMap;
import java.util.Map;


public class DuplicateEmployeeException extends RuntimeException {

    private final Map<String, String> fieldErrors = new HashMap<>();

    public DuplicateEmployeeException(String field, String message) {
        this.fieldErrors.put(field, message);
    }

    public DuplicateEmployeeException(Map<String, String> fieldErrors) {
        this.fieldErrors.putAll(fieldErrors);
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
