package com.jimi.exception;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends Exception {

    private static final long serialVersionUID = 1L;
    private Map<String, String> errorFields = new HashMap<String, String>();

    public ValidationException() {
    }

    public void addErrorField(String name, String message) {
        errorFields.put(name, message);
    }

    public String getErrorField(String name) {
        if (errorFields.containsKey(errorFields)) {
            return errorFields.get(name);
        }

        return "";
    }

    public Map<String, String> getErrorFields() {
        return errorFields;
    }

    public boolean isErrorField() {
        return !errorFields.isEmpty();
    }
}
