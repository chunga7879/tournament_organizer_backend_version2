package com.hairlesscat.app.validation;

public class MissingFieldsException extends Exception {
    public MissingFieldsException(String missingField) {
        super("Request body is missing the top level field: " + missingField);
    }
}
