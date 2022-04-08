package com.hairlesscat.app.validation;

import java.util.List;
import java.util.Set;

public class Validator {
    public static void requestBodyTopLevelFieldValidation(List<String> requiredFields, Set<String> requestBodyFields) throws MissingFieldsException {
        for (String requiredField : requiredFields) {
            if (!requestBodyFields.contains(requiredField))
                throw new MissingFieldsException(requiredField);
        }
    }
}
