package com.pollaminllc.crs.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * JSON utility class using Gson for serialization/deserialization.
 */
public class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Gson GSON_COMPACT = new Gson();

    /**
     * Parse JSON string to object.
     *
     * @param json  JSON string
     * @param clazz Target class
     * @return Parsed object
     * @throws JsonSyntaxException if JSON is invalid
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            throw new JsonSyntaxException("Empty JSON");
        }
        return GSON.fromJson(json, clazz);
    }

    /**
     * Convert object to JSON string (pretty printed).
     */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * Convert object to compact JSON string (no whitespace).
     */
    public static String toJsonCompact(Object obj) {
        return GSON_COMPACT.toJson(obj);
    }
}
