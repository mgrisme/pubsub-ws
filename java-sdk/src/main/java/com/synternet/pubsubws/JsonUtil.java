package com.synternet.pubsubws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Simple wrapper around Jackson's ObjectMapper. */
public final class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtil() {}

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize JSON", e);
        }
    }
}
