package com.hairlesscat.app.util;

import java.util.HashMap;
import java.util.Map;

public class ResponseWrapper {
    public static <T> Map<String, T> wrapResponse(String header, T body) {
        Map<String, T> response = new HashMap<>();
        response.put(header, body);
        return response;
    }
}
