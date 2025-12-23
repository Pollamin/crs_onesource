package com.pollaminllc.crs.model;

/**
 * Result of a PO validation operation.
 * Encapsulates success/error response with appropriate HTTP status.
 */
public class ValidationResult {

    private final boolean success;
    private final int httpStatus;
    private final String message;

    private ValidationResult(boolean success, int httpStatus, String message) {
        this.success = success;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    /**
     * Create a success result (HTTP 200).
     */
    public static ValidationResult success(String message) {
        return new ValidationResult(true, 200, message);
    }

    /**
     * Create a bad request error (HTTP 400).
     */
    public static ValidationResult badRequest(String message) {
        return new ValidationResult(false, 400, message);
    }

    /**
     * Create a not found error (HTTP 404).
     */
    public static ValidationResult notFound(String message) {
        return new ValidationResult(false, 404, message);
    }

    /**
     * Create a conflict error (HTTP 409).
     */
    public static ValidationResult conflict(String message) {
        return new ValidationResult(false, 409, message);
    }

    /**
     * Create a server error (HTTP 500).
     */
    public static ValidationResult serverError(String message) {
        return new ValidationResult(false, 500, message);
    }

    /**
     * Create a service unavailable error (HTTP 503).
     */
    public static ValidationResult serviceUnavailable(String message) {
        return new ValidationResult(false, 503, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Convert to JSON response format.
     * Success: { "data": "message" }
     * Error:   { "errorMessage": "message" }
     */
    public String toJson() {
        if (success) {
            return String.format("{\"data\":\"%s\"}", escapeJson(message));
        } else {
            return String.format("{\"errorMessage\":\"%s\"}", escapeJson(message));
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @Override
    public String toString() {
        return String.format(
            "ValidationResult{success=%s, status=%d, message='%s'}",
            success, httpStatus, message
        );
    }
}
